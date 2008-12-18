package green.search.lsi.matrix;

import java.io.Serializable;

import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;

public class TwoBitMatrix implements RealMatrix, Serializable {

	private byte[][] data = null;

	int col, row;

	byte bufffer = 0;
	int colPtr = 0;
	int rowPtr = 0;

	public TwoBitMatrix(int col, int row) {
		this.col = col;
		this.row = row;
		int bcol = (col % 4 != 0) ? col / 4 + 1 : col / 4;
		data = new byte[row][bcol];
	}

	public void addOne() {
		storeToMat();
		bufffer |= 1;
	}

	public void addZero() {
		storeToMat();
		bufffer |= 0;
	}

	public void addOneInv() {
		storeToMat();
		bufffer |= 3;
	}

	private void storeToMat() {
		if (colPtr != 0 && colPtr % 4 == 0) {
			this.setData(colPtr / 4 - 1, rowPtr, bufffer);
			bufffer = 0;
		}
		bufffer <<= 2;
		colPtr++;
	}

	public void next() {
		if (colPtr % 4 != 0) {
			int s = 4 - colPtr % 4;
			colPtr = (colPtr / 4 + 1) * 4;
			bufffer <<= s * 2;
		}
		storeToMat();
		// mat.trace(rowPtr);
		colPtr = 0;
		rowPtr++;
	}

	void setData(byte[][] data) {
		this.data = data;
	}

	void setData(int col, int row, byte d) {
		this.data[row][col] = d;
	}

	public void trace(int r) {

		for (int i = 0; i < this.data[r].length; i++) {
			System.out.print(this.data[r][i] < 0 ? Integer.toString(
					this.data[r][i] + 256, 2) : Integer.toString(
					this.data[r][i], 2));
			System.out.print(", ");
		}
		System.out.println();
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
		return col;
	}

	public RealMatrix getColumnMatrix(int column) throws MatrixIndexException {
		if (!isValidCoordinate(0, column)) {
			throw new MatrixIndexException("illegal column argument");
		}
		int nRows = this.getRowDimension();
		// double[][] out = new double[nRows][1];
		TwoBitMatrix out = new TwoBitMatrix(nRows, 1);
		for (int row = 0; row < nRows; row++) {
			// out[row][0] = data[row][column];
			int d = (int) this.getEntry(row, column);
			switch (d) {
			case 1:
				out.addOne();
				break;
			case -1:
				out.addOneInv();
				break;
			default:
				out.addZero();
			}
		}
		out.next();
		return out.transpose();
	}

	public double[][] getData() {
		throw new RuntimeException("Not supported method call");
	}

	public double getDeterminant() {
		throw new RuntimeException("Not supported method call");
	}

	public double getEntry(int r, int c) throws MatrixIndexException {
		byte b = data[r][c / 4];
		// make mask bit
		int mask = 3;
		int shift = 3 - c % 4;
		mask <<= shift * 2;
		b &= mask;
		b >>= shift * 2;
		switch (b) {
		case 1:
			return 1;
		case 3:
			return -1;
		default:
			if (b < 0)
				return -1;
			return 0;
		}
	}

	public double getNorm() {
		throw new RuntimeException("Not supported method call");
	}

	public double[] getRow(int row) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	public int getRowDimension() {
		return row;
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
		// System.out.println("# nRows = " + nRows);
		// System.out.println("# nCols = " + nCols);
		// System.out.println("# nSum = " + nSum);
		double[][] outData = new double[nRows][nCols];
		double sum = 0;
		for (int row = 0; row < nRows; row++) {
			for (int col = 0; col < nCols; col++) {
				sum = 0;
				for (int i = 0; i < nSum; i++) {
					double d2 = m.getEntry(i, col);
					if (d2 == 0)
						continue;
					double d1 = this.getEntry(row, i);
					if (d1 == 0)
						continue;
					sum += d1 * d2;
					// System.out.println("# sum = " + sum);
				}
				outData[row][col] = sum;
			}
		}
		return new RealMatrixImpl(outData);

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
		int nRows = this.getRowDimension();
		int nCols = this.getColumnDimension();
		TwoBitMatrix out = new TwoBitMatrix(nRows, nCols);
		for (int col = 0; col < nCols; col++) {
			for (int row = 0; row < nRows; row++) {
				int d = (int) this.getEntry(row, col);
				switch (d) {
				case 1:
					out.addOne();
					break;
				case -1:
					out.addOneInv();
					break;
				default:
					out.addZero();
				}
			}
			out.next();
		}
		return out;
	}

	private boolean isValidCoordinate(int row, int col) {
		int nRows = this.getRowDimension();
		int nCols = this.getColumnDimension();

		return !(row < 0 || row > nRows - 1 || col < 0 || col > nCols - 1);
	}

	@Override
	public String toString() {
		System.out.println();
		StringBuffer stbf = new StringBuffer();
		for (int i = 0; i < this.getRowDimension(); i++) {
			for (int j = 0; j < this.getColumnDimension(); j++) {
				stbf.append(this.getEntry(i, j) + "\t");
			}
			stbf.append("\n");
		}
		return stbf.toString();
	}
}
