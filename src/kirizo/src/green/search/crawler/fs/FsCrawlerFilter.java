package green.search.crawler.fs;

import java.io.File;
import java.io.FileFilter;

public class FsCrawlerFilter implements FileFilter {

	public static final String ELS = "xls";

	public static final String PDF = "pdf";

	public static final String TXT = "txt";

	public static final String HTML = "html";

	public static final String HTM = "htm";

	public static final String DOC = "doc";

	public static final String PPT = "ppt";

	private static String[] TYPES = { ELS, PDF, TXT, DOC, PPT, HTML, HTM };

	public FsCrawlerFilter() {
		super();
	}

	public FsCrawlerFilter(String type) {
		TYPES = new String[] { type };
	}

	public boolean accept(File pathname) {
		if (!pathname.canRead()) {
			return false;
		}
		boolean retFlg = false;
		if (pathname.isDirectory()) {
			return true;
		} else if (pathname.isFile()) {
			for (String type : TYPES) {
				if (pathname.getName().endsWith(type)) {
					retFlg = true;
				}
			}
		}
		return retFlg;
	}

	public static boolean isMsExcelFile(File file) {
		return file.getName().endsWith(ELS);
	}

	public static boolean isMsWordFile(File file) {
		return file.getName().endsWith(DOC);
	}

	public static boolean isMsPowerPoint(File file) {
		return file.getName().endsWith(PPT);
	}

	public static boolean isPdf(File file) {
		return file.getName().endsWith(PDF);
	}

	public static boolean isText(File file) {
		return file.getName().endsWith(TXT);
	}

	public static boolean isHtml(File file) {
		return file.getName().endsWith(HTML) || file.getName().endsWith(HTM);
	}
}