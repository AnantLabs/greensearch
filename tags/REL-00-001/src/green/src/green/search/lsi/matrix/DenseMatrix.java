package green.search.lsi.matrix;

public class DenseMatrix {
	long rows;
	long cols;
	/** Accessed by [row][col]. Free value[0] and value to free. */
	double[][] value;

	public long getRows() {
		return rows;
	}

	public long getCols() {
		return cols;
	}

	public double[][] getValue() {
		return value;
	}
}
