package green.search.crawler.converter;

import java.io.File;

public abstract class AbstractDocumentConverter {

	protected final String CR = System.getProperty("line.separator");

	protected File file = null;

	public AbstractDocumentConverter(File file) {
		this.file = file;
	}

}
