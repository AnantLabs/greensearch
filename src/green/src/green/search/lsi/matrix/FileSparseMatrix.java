package green.search.lsi.matrix;

import java.io.File;
import java.io.IOException;

public class FileSparseMatrix implements SparseMatrix {

	private static final String VALUE = "_value";
	private static final String IND = "_ind";
	private static final String PTR = "_ptr";

	private static final int INT_SIZE = (Integer.SIZE / 8);

	private static final int DOUBLE_SIZE = (Double.SIZE / 8);

	private int rows;
	private int cols;
	private int vals;

	/** �s��̊i�[�f�B���N�g�� */
	File file;

	FileSparseMatrixElement value;

	FileSparseMatrixElement ind;

	FileSparseMatrixElement ptr;

	/**
	 * 
	 */
	FileSparseMatrix() {
		super();
	}

	/**
	 * 
	 * @throws IOException
	 * @throws IOException
	 */
	public FileSparseMatrix(File file, String idf) throws IOException {
		super();
		this.file = file;

		this.value = new FileSparseMatrixElement(file, idf, VALUE, DOUBLE_SIZE);
		this.ind = new FileSparseMatrixElement(file, idf, IND, INT_SIZE);
		this.ptr = new FileSparseMatrixElement(file, idf, PTR, INT_SIZE);
	}

	/**
	 * �s��̍s�Ɨ���J�E���g����B
	 * 
	 * @throws IOException
	 */
	public void countColRow() throws IOException {
		this.rows = this.ptr.getCountRows();
		this.cols = this.ind.getCountCols();
		this.vals = this.ind.getCountVals();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see green.search.crawler.lsi.SparseMatrix#getRows()
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @param rows
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see green.search.crawler.lsi.SparseMatrix#getCols()
	 */
	public int getCols() {
		return cols;
	}

	/**
	 * @param cols
	 */
	public void setCols(int cols) {
		this.cols = cols;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see green.search.crawler.lsi.SparseMatrix#getVals()
	 */
	public int getVals() {
		return vals;
	}

	/**
	 * @param vals
	 */
	public void setVals(int vals) {
		this.vals = vals;
	}

	/**
	 * @param index
	 * @param value
	 * @throws IOException
	 */
	public void addValue(int index, double value) throws IOException {

		this.value.storeDoubleValue(value);
		this.ind.storeIntValue(index);
		crs_store_cnt++;

	}

	/**
	 * @throws IOException
	 */
	public void addPtr() throws IOException {
		this.ptr.storeIntValue(crs_store_cnt);
	}

	int crs_store_cnt = 0;

	/**
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public int getPointer(long index) throws IOException {
		// System.out.println("# P # = " + index);
		return this.ptr.getIntValue(index);
	}

	/**
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public int getIndex(long index) throws IOException {
		// System.out.println("# I # = " + index);
		return this.ind.getIntValue(index);
	}

	/**
	 * Harwell-Boeing Format�̍s��̒l���擾����B
	 * 
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public double getValue(long index) throws IOException {
		// System.out.println("# V # = " + index);
		return this.value.getDoubleValue(index);
	}

	/**
	 * ���̓x�N�g���̓]�u�x�N�g�����쐬����B
	 * 
	 * @param S
	 * @param idf
	 * @param formatOnly
	 * @return
	 * @throws IOException
	 */
	public SparseMatrix transpose(SparseMatrix S, String idf, boolean formatOnly)
			throws IOException {

		FileSparseMatrix N;
		if (formatOnly) {
			N = createInstance(idf, S.getRows(), S.getCols(), S.getVals());
		} else {
			N = createInstance(idf, S.getCols(), S.getRows(), S.getVals());
		}

		// BoundedRangeModelHodler.get().get(2).setMaximum(S.getCols());

		long stidx, edidx, target;
		N.addPtr();
		for (int i = 0; i < S.getCols(); i++) {
			for (int j = 0; j < S.getRows(); j++) {
				stidx = S.getPointer(j);
				edidx = S.getPointer(j + 1);
				target = findTarget(S, stidx, edidx, i);
				if (target > -1) {
					N.addValue(j, S.getValue(target));
				}
			}
			N.addPtr();
			// BoundedRangeModelHodler.get().get(2).setValue(i + 1);
		}
		N.flush();
		// �i���ɃX�e�b�v�̊������
		// BoundedRangeModelHodler.get().setJobid(
		// BoundedRangeModelHodler.get().getJobid() + 1);

		return N;
	}

	/**
	 * �w�肳�ꂽPosission�͈̔͂Ɏw�肳�ꂽ�l�����邩�T���B
	 * 
	 * @param S
	 * @param start
	 * @param end
	 * @param value
	 * @return ���������ʒu
	 * @throws IOException
	 */
	private long findTarget(SparseMatrix S, long start, long end, int value)
			throws IOException {
		long residx = -1;
		int target;
		for (long i = start; i < end; i++) {
			target = S.getIndex(i);
			if (target == value) {
				residx = i;
				return residx;
			}
		}
		return residx;
	}

	/**
	 * @param name
	 * @param rows
	 * @param cols
	 * @param vals
	 * @return
	 * @throws IOException
	 */
	private FileSparseMatrix createInstance(String name, int rows, int cols,
			int vals) throws IOException {

		FileSparseMatrix S = new FileSparseMatrix(this.file, name);

		S.setRows(rows);
		S.setCols(cols);
		S.setVals(vals);

		return S;
	}

	/**
	 * ���̃x�N�g�����ێ�����e�t�@�C���`���l�������B
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		this.value.close();
		this.ind.close();
		this.ptr.close();

	}

	/**
	 * @return
	 */
	public void relese() {
		this.value.relese();
		this.ind.relese();
		this.ptr.relese();
	}

	public void flush() throws IOException {
		this.value.flush();
		this.ind.flush();
		this.ptr.flush();
	}

}
