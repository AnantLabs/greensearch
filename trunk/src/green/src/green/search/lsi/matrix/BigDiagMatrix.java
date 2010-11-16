package green.search.lsi.matrix;

import java.io.Serializable;

import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.MatrixVisitorException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixChangingVisitor;
import org.apache.commons.math.linear.RealMatrixImpl;
import org.apache.commons.math.linear.RealMatrixPreservingVisitor;
import org.apache.commons.math.linear.RealVector;

public class BigDiagMatrix implements RealMatrix, Serializable {

	private double[] data = null;

	public BigDiagMatrix(double[] data) {
		this.data = data;
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
		return data.length;
	}

	public RealMatrix getColumnMatrix(int column) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	public double[][] getData() {
		throw new RuntimeException("Not supported method call");
	}

	public double getDeterminant() {
		throw new RuntimeException("Not supported method call");
	}

	public double getEntry(int row, int column) throws MatrixIndexException {
		if (row != column)
			return 0;
		return data[row];
	}

	public double getNorm() {
		throw new RuntimeException("Not supported method call");
	}

	public double[] getRow(int row) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	public int getRowDimension() {
		return data.length;
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
		if (this.getColumnDimension() != m.getRowDimension()) {
			throw new IllegalArgumentException(
					"Matrices are not multiplication compatible.");
		}
		int nRows = this.getRowDimension();
		int nCols = m.getColumnDimension();
		int nSum = this.getColumnDimension();
		// RealMatrixImpl ri = new RealMatrixImpl(nRows, nCols);
		CRSMatrix crsMat = (CRSMatrix) m;
		int nNum = crsMat.getValueNum();
		// for (int row = 0; row < nRows; row++) {
		// for (int col = 0; col < nCols; col++) {
		// double d = m.getEntry(row, col);
		// if (d != 0) {
		// nNum++;
		// }
		// }
		// }
		CCSMatrix crsm = new CCSMatrix(nCols, nRows, nNum);
		// double[][] outData = ri.getDataRef();
		double sum = 0;
		crsm.next();
		for (int col = 0; col < nCols; col++) {
			for (int row = 0; row < nRows; row++) {
				sum = 0;
				for (int i = 0; i < nSum; i++) {
					double s1 = this.getEntry(row, i);
					if (s1 == 0)
						continue;
					double s2 = m.getEntry(i, col);
					if (s2 == 0)
						continue;
					sum += s1 * s2;
				}
				// outData[row][col] = sum;
				if (sum != 0) {
					crsm.addValue(row, sum);
				}
			}
			crsm.next();
		}
		// crsm.next();
		// return ri;
		return crsm;
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
		return new BigDiagMatrix(this.data);
	}

	public static void main(String[] args) {

		double[][] data = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
		RealMatrix mat = new RealMatrixImpl(data);
		System.out.println(mat);
		double[] d = { 1, 2, 3 };
		RealMatrix dmat = new BigDiagMatrix(d);
		System.out.println(dmat);
		RealMatrix out = dmat.multiply(mat);
		System.out.println(out);

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
		throw new RuntimeException("Not supported method call");
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
	public double walkInOptimizedOrder(RealMatrixPreservingVisitor arg0)
			throws MatrixVisitorException {
		throw new RuntimeException("Not supported method call");
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
	public double walkInRowOrder(RealMatrixPreservingVisitor arg0)
			throws MatrixVisitorException {
		throw new RuntimeException("Not supported method call");
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
