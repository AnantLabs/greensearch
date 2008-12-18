package green.search.lsi.matrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;

public class CCSMatrix implements RealMatrix, Serializable {

	private int[] ptr;
	private int[] idx;
	private double[] val;

	private int col = 0;
	private int row = 0;

	private int pnum = 0;
	private int iptr = 0;

	// private transient List<Long> lPtr = new ArrayList<Long>();
	// private transient List<Integer> lIdx = new ArrayList<Integer>();
	// private transient List<Double> lVal = new ArrayList<Double>();

	public CCSMatrix(int cols, int rows, int vnum) {
		col = cols;
		row = rows;
		ptr = new int[cols + 1];
		idx = new int[vnum];
		val = new double[vnum];
	}

	public void addValue(int index, double value) {
		// lIdx.add(index);
		idx[pnum] = index;
		// lVal.add(value);
		val[pnum] = value;
		pnum++;
	}

	public void next() {
		// lPtr.add(num);
		ptr[iptr] = pnum;
		iptr++;
	}

	public RealMatrix add(RealMatrix m) throws IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	public RealMatrix copy() {
		throw new RuntimeException("Not supported method call");
	}

	public double[] getColumn(int col) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	public int getColumnDimension() {
		return this.ptr.length - 1;
	}

	// private transient double[][] out = null;

	public RealMatrix getColumnMatrix(int column) throws MatrixIndexException {
		int nRows = this.getRowDimension();
		double[][] out = new double[nRows][1];
		int stidx;
		int edidx;
		stidx = ptr[column];
		edidx = ptr[column + 1];
		for (int i = stidx; i < edidx; i++) {
			out[idx[i]][0] = val[i];
		}
		return new RealMatrixImpl(out);
	}

	public double[][] getData() {
		throw new RuntimeException("Not supported method call");
	}

	public double getDeterminant() {
		throw new RuntimeException("Not supported method call");
	}

	public double getEntry(int row, int column) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	public double getNorm() {
		throw new RuntimeException("Not supported method call");
	}

	public double[] getRow(int row) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	public int getRowDimension() {
		return this.row;
	}

	public RealMatrix getRowMatrix(int row) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	public RealMatrix getSubMatrix(int[] selectedRows, int[] selectedColumns)
			throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	public RealMatrix getSubMatrix(int startRow, int endRow, int startColumn,
			int endColumn) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	public double getTrace() {
		throw new RuntimeException("Not supported method call");
	}

	public RealMatrix inverse() throws InvalidMatrixException {
		throw new RuntimeException("Not supported method call");
	}

	public boolean isSingular() {
		throw new RuntimeException("Not supported method call");
	}

	public boolean isSquare() {
		throw new RuntimeException("Not supported method call");
	}

	public RealMatrix multiply(RealMatrix m) throws IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	public double[] operate(double[] v) throws IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	public RealMatrix preMultiply(RealMatrix m) throws IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	public double[] preMultiply(double[] v) throws IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	public RealMatrix scalarAdd(double d) {
		throw new RuntimeException("Not supported method call");
	}

	public RealMatrix scalarMultiply(double d) {
		throw new RuntimeException("Not supported method call");
	}

	public double[] solve(double[] b) throws IllegalArgumentException,
			InvalidMatrixException {
		throw new RuntimeException("Not supported method call");
	}

	public RealMatrix solve(RealMatrix b) throws IllegalArgumentException,
			InvalidMatrixException {
		throw new RuntimeException("Not supported method call");
	}

	public RealMatrix subtract(RealMatrix m) throws IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	public RealMatrix transpose() {
		throw new RuntimeException("Not supported method call");
	}

}
