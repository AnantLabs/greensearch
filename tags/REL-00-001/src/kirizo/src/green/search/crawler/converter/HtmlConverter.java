package green.search.crawler.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlConverter extends AbstractDocumentConverter implements
		DocumentConverter {

	public HtmlConverter(File file) {
		super(file);
	}

	public String convert() {

		// 文字列に変換するためのバッファを用意
		StringBuffer stbf = new StringBuffer();
		String resstr = stbf.toString();
		try {
			FileReader fr = new FileReader(super.file);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				parseLine(s);
				stbf.append(s);
				stbf.append(CR);
			}
			br.close();
			fr.close();

			resstr = stbf.toString();
			// if (parseCharset != null) {
			// resstr = new String(resstr.getBytes(), Charset
			// .forName(parseCharset));
			// System.out.println("#### [" + parseCharset + "]");
			// }
		} catch (RuntimeException e) {
			e.printStackTrace(System.err);
			System.err.println("#####################" + super.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		resstr = tagremove(resstr);
		return resstr;
	}

	boolean metabool = true;

	String parseCharset = null;

	private void parseLine(String line) {

		if (metabool) {
			try {
				String tmp = line.toLowerCase();
				int metast = tmp.indexOf("<meta");
				String CHARSETMARK = "charset=";
				int chardef = tmp.indexOf(CHARSETMARK, metast);
				if (metast != -1 && chardef != -1) {
					int qidx = tmp.indexOf('\"', chardef);
					int dif = tmp.indexOf('\'', chardef);
					if (dif != -1 && qidx > dif)
						qidx = dif;
					dif = tmp.indexOf('>', chardef);
					if (dif != -1 && qidx > dif)
						qidx = dif;
					parseCharset = line.substring(chardef
							+ CHARSETMARK.length(), qidx);
					metabool = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String tagremove(String str) {
		Pattern pattern = Pattern.compile("<.+?>", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(str);
		String string = matcher.replaceAll("");
		return string;
	}

	public static void main(String[] args) {

		File file = new File(
				"C:\\Documents and Settings\\haruyosi\\デスクトップ\\岡本さんへ\\index.html");
		HtmlConverter cvt = new HtmlConverter(file);
		String str = cvt.convert();

		System.out.println("### str " + str);
	}
}
