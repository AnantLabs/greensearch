package green.search.lsi.matrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;

public class CRSMatrix implements RealMatrix, Serializable {

	private static final long serialVersionUID = 1L;

	private int col = 0;
	private int row = 0;

	private int pnum = 0;
	private int iptr = 0;

	private int[] ptr;
	private int[] idx;
	private int[] val;

	public CRSMatrix(int cols, int rows, int vnum) {
		col = cols;
		row = rows;
		ptr = new int[rows + 1];
		idx = new int[vnum];
		val = new int[vnum];
	}

	public void addValue(int index, int value) {
		idx[pnum] = index;
		val[pnum] = value;
		pnum++;
	}

	public void next() {
		ptr[iptr] = pnum;
		iptr++;
	}

	// public void freez() {
	// this.iptr = freez(ptr);
	// this.iidx = freez(idx);
	// this.ival = freez(val);
	// }

	private int[] freez(List<Integer> list) {
		int[] target = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			target[i] = list.get(i);
		}
		return target;
	}

	@Override
	public RealMatrix add(RealMatrix arg0) throws IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealMatrix copy() {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double[] getColumn(int arg0) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public int getColumnDimension() {
		return col;
	}

	@Override
	public RealMatrix getColumnMatrix(int arg0) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double[][] getData() {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double getDeterminant() {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double getEntry(int row, int column) throws MatrixIndexException {

		int col_ind_s = this.ptr[row];
		int col_ind_e = this.ptr[row + 1];
		for (int i = col_ind_s; i < col_ind_e; i++) {
			if (this.idx[i] == column) {
				return this.val[i];
			}
		}
		return 0;
	}

	@Override
	public double getNorm() {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double[] getRow(int arg0) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public int getRowDimension() {
		return row;
	}

	@Override
	public RealMatrix getRowMatrix(int arg0) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealMatrix getSubMatrix(int[] arg0, int[] arg1)
			throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealMatrix getSubMatrix(int arg0, int arg1, int arg2, int arg3)
			throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double getTrace() {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealMatrix inverse() throws InvalidMatrixException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public boolean isSingular() {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public boolean isSquare() {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealMatrix multiply(RealMatrix m) throws IllegalArgumentException {
		QueryMatrix qm = (QueryMatrix) m;
		if (this.getColumnDimension() != qm.getRowDimension()) {
			throw new IllegalArgumentException(
					"Matrices are not multiplication compatible.");
		}
		int nRows = this.getRowDimension();
		int nCols = qm.getColumnDimension();
		int nSum = this.getColumnDimension();
		double[][] outData = new double[nRows][nCols];
		double sum = 0;
		for (int row = 0; row < nRows; row++) {
			// System.out.println("row = " + row);
			for (int col = 0; col < nCols; col++) {
				sum = 0;
				for (int i = 0; i < nSum; i++) {
					// sum += data[row][i] * m.getEntry(i, col);
					double s1 = qm.getEntry(i, col);
					if (s1 == 0)
						continue;
					double s2 = this.getEntry(row, i);
					if (s2 == 0)
						continue;
					sum += s1 * s2;
				}
				outData[row][col] = sum;
			}
		}
		return new RealMatrixImpl(outData);

	}

	@Override
	public double[] operate(double[] arg0) throws IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealMatrix preMultiply(RealMatrix arg0)
			throws IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double[] preMultiply(double[] arg0) throws IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealMatrix scalarAdd(double arg0) {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealMatrix scalarMultiply(double arg0) {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double[] solve(double[] arg0) throws IllegalArgumentException,
			InvalidMatrixException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealMatrix solve(RealMatrix arg0) throws IllegalArgumentException,
			InvalidMatrixException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealMatrix subtract(RealMatrix arg0) throws IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealMatrix transpose() {
		throw new RuntimeException("Not supported method call");
	}

}
