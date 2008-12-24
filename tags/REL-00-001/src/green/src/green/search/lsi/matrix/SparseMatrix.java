package green.search.lsi.matrix;

import java.io.IOException;

public interface SparseMatrix {

	/**
	 * @return
	 */
	public int getRows();

	/**
	 * @return
	 */
	public int getCols();

	/**
	 * @return
	 */
	public int getVals();

	public int getPointer(long index) throws IOException;

	public int getIndex(long index) throws IOException;

	public double getValue(long index) throws IOException;

	public SparseMatrix transpose(SparseMatrix S, String idf, boolean formatOnly)
			throws IOException;

	public void flush() throws IOException;

	public void close() throws IOException;

	public void relese();

}