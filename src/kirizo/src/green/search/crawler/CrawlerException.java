package green.search.crawler;

public class CrawlerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CrawlerException() {
		super();
	}

	public CrawlerException(String message) {
		super(message);
	}

	public CrawlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public CrawlerException(Throwable cause) {
		super(cause);
	}

}
