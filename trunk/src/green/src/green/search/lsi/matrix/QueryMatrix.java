package green.search.lsi.matrix;

import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.RealMatrix;

public class QueryMatrix implements RealMatrix {

	// private List<Integer> ptr = new ArrayList<Integer>();
	int[] ptr;

	// private List<Double> value = new ArrayList<Double>();
	double[] value;

	private int cnt = 0;
	private int dimcnt = 0;

	public QueryMatrix(int length) {
		ptr = new int[length];
		value = new double[length];
	}

	public void reset() {
		dimcnt = 0;
		cnt = 0;
	}

	public void addWightValue(double w) {
		if (w != 0) {
			// ptr.add(dimcnt);
			// value.add(w);
			ptr[cnt] = dimcnt;
			value[cnt] = w;
			cnt++;
		}
		dimcnt++;
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
		return 1;
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

		int index = -1;
		for (int i = 0; i < this.ptr.length; i++) {
			if (row == this.ptr[i]) {
				index = i;
				break;
			}
		}
		if (index == -1)
			return 0;
		return this.value[index];
	}

	public double getNorm() {
		double maxColSum = 0;
		for (int col = 0; col < this.getColumnDimension(); col++) {
			double sum = 0;
			for (int i = 0; i < value.length; i++) {
				sum += Math.abs(value[i]);
			}
			// for (int i = 0; i < value.size(); i++) {
			// sum += Math.abs(value.get(i));
			// }
			// for (int row = 0; row < this.getRowDimension(); row++) {
			// sum += Math.abs(getEntry(row, col));
			// }
			maxColSum = Math.max(maxColSum, sum);
		}
		return maxColSum;
	}

	public double[] getRow(int row) throws MatrixIndexException {
		throw new RuntimeException("Not supported method call");
	}

	public int getRowDimension() {
		return this.dimcnt;
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
