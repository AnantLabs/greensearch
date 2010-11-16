package green.search.lsi.matrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.MatrixVisitorException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixChangingVisitor;
import org.apache.commons.math.linear.RealMatrixImpl;
import org.apache.commons.math.linear.RealMatrixPreservingVisitor;
import org.apache.commons.math.linear.RealVector;

public class CCSMatrix implements RealMatrix, Serializable {

	private int[] ptr;
	private int[] idx;
	private double[] val;

	private int col = 0;
	private int row = 0;

	private int pnum = 0;
	private int iptr = 0;

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
		int col_ind_s = this.ptr[column];
		int col_ind_e = this.ptr[column + 1];
		for (int i = col_ind_s; i < col_ind_e; i++) {
			if (this.idx[i] == row) {
				return this.val[i];
			}
		}
		return 0;
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

	@Override
	public void addToEntry(int arg0, int arg1, double arg2)
			throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public void copySubMatrix(int[] arg0, int[] arg1, double[][] arg2)
			throws MatrixIndexException, IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public void copySubMatrix(int arg0, int arg1, int arg2, int arg3,
			double[][] arg4) throws MatrixIndexException,
			IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealMatrix createMatrix(int arg0, int arg1) {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealVector getColumnVector(int arg0) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double getFrobeniusNorm() {
        return walkInOptimizedOrder(new RealMatrixPreservingVisitor() {

            /** Sum of squared entries. */
            private double sum;

            /** {@inheritDoc} */
            public void start(final int rows, final int columns,
                              final int startRow, final int endRow,
                              final int startColumn, final int endColumn) {
                sum = 0;
            }

            /** {@inheritDoc} */
            public void visit(final int row, final int column, final double value) {
                sum += value * value;
            }

            /** {@inheritDoc} */
            public double end() {
                return Math.sqrt(sum);
            }

        });
	}

	@Override
	public RealVector getRowVector(int arg0) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public void multiplyEntry(int arg0, int arg1, double arg2)
			throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealVector operate(RealVector arg0) throws IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public RealVector preMultiply(RealVector arg0)
			throws IllegalArgumentException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public void setColumn(int arg0, double[] arg1) throws MatrixIndexException,
			InvalidMatrixException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public void setColumnMatrix(int arg0, RealMatrix arg1)
			throws MatrixIndexException, InvalidMatrixException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public void setColumnVector(int arg0, RealVector arg1)
			throws MatrixIndexException, InvalidMatrixException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public void setEntry(int arg0, int arg1, double arg2)
			throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public void setRow(int arg0, double[] arg1) throws MatrixIndexException,
			InvalidMatrixException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public void setRowMatrix(int arg0, RealMatrix arg1)
			throws MatrixIndexException, InvalidMatrixException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public void setRowVector(int arg0, RealVector arg1)
			throws MatrixIndexException, InvalidMatrixException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public void setSubMatrix(double[][] arg0, int arg1, int arg2)
			throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double walkInColumnOrder(RealMatrixChangingVisitor arg0)
			throws MatrixVisitorException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double walkInColumnOrder(RealMatrixPreservingVisitor arg0)
			throws MatrixVisitorException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double walkInColumnOrder(RealMatrixChangingVisitor arg0, int arg1,
			int arg2, int arg3, int arg4) throws MatrixIndexException,
			MatrixVisitorException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double walkInColumnOrder(RealMatrixPreservingVisitor arg0, int arg1,
			int arg2, int arg3, int arg4) throws MatrixIndexException,
			MatrixVisitorException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double walkInOptimizedOrder(RealMatrixChangingVisitor arg0)
			throws MatrixVisitorException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double walkInOptimizedOrder(RealMatrixPreservingVisitor visitor)
			throws MatrixVisitorException {
        return walkInRowOrder(visitor);
	}

	@Override
	public double walkInOptimizedOrder(RealMatrixChangingVisitor arg0,
			int arg1, int arg2, int arg3, int arg4)
			throws MatrixIndexException, MatrixVisitorException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double walkInOptimizedOrder(RealMatrixPreservingVisitor arg0,
			int arg1, int arg2, int arg3, int arg4)
			throws MatrixIndexException, MatrixVisitorException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double walkInRowOrder(RealMatrixChangingVisitor arg0)
			throws MatrixVisitorException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double walkInRowOrder(RealMatrixPreservingVisitor visitor)
			throws MatrixVisitorException {
        final int rows    = getRowDimension();
        final int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < columns; ++column) {
                visitor.visit(row, column, getEntry(row, column));
            }
        }
        return visitor.end();
	}

	@Override
	public double walkInRowOrder(RealMatrixChangingVisitor arg0, int arg1,
			int arg2, int arg3, int arg4) throws MatrixIndexException,
			MatrixVisitorException {
		throw new RuntimeException("Not supported method call");
	}

	@Override
	public double walkInRowOrder(RealMatrixPreservingVisitor arg0, int arg1,
			int arg2, int arg3, int arg4) throws MatrixIndexException,
			MatrixVisitorException {
		throw new RuntimeException("Not supported method call");
	}

}
