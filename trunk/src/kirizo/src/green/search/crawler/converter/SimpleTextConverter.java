package green.search.crawler.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class SimpleTextConverter extends AbstractDocumentConverter implements
		DocumentConverter {

	private String[] han = new String[] { "ｶﾞ", "ｷﾞ", "ｸﾞ", "ｹﾞ", "ｺﾞ", "ｻﾞ",
			"ｼﾞ", "ｽﾞ", "ｾﾞ", "ｿﾞ", "ﾀﾞ", "ﾁﾞ", "ﾂﾞ", "ﾃﾞ", "ﾄﾞ", "ﾊﾞ", "ﾊﾟ",
			"ﾋﾞ", "ﾋﾟ", "ﾌﾞ", "ﾌﾟ", "ﾍﾞ", "ﾍﾟ", "ﾎﾞ", "ﾎﾟ", "ｳﾞ", "ｧ", "ｱ",
			"ｨ", "ｲ", "ｩ", "ｳ", "ｪ", "ｴ", "ｫ", "ｵ", "ｶ", "ｷ", "ｸ", "ｹ", "ｺ",
			"ｻ", "ｼ", "ｽ", "ｾ", "ｿ", "ﾀ", "ﾁ", "ｯ", "ﾂ", "ﾃ", "ﾄ", "ﾅ", "ﾆ",
			"ﾇ", "ﾈ", "ﾉ", "ﾊ", "ﾋ", "ﾌ", "ﾍ", "ﾎ", "ﾏ", "ﾐ", "ﾑ", "ﾒ", "ﾓ",
			"ｬ", "ﾔ", "ｭ", "ﾕ", "ｮ", "ﾖ", "ﾗ", "ﾘ", "ﾙ", "ﾚ", "ﾛ", "ﾜ", "ｦ",
			"ﾝ", "｡", "｢", "｣", "､", "･", "ｰ", "ﾞ", "ﾟ" };
	private String[] zenn = new String[] { "ガ", "ギ", "グ", "ゲ", "ゴ", "ザ", "ジ",
			"ズ", "ゼ", "ゾ", "ダ", "ヂ", "ヅ", "デ", "ド", "バ", "パ", "ビ", "ピ", "ブ",
			"プ", "ベ", "ペ", "ボ", "ポ", "ヴ", "ァ", "ア", "ィ", "イ", "ゥ", "ウ", "ェ",
			"エ", "ォ", "オ", "カ", "キ", "ク", "ケ", "コ", "サ", "シ", "ス", "セ", "ソ",
			"タ", "チ", "ッ", "ツ", "テ", "ト", "ナ", "ニ", "ヌ", "ネ", "ノ", "ハ", "ヒ",
			"フ", "ヘ", "ホ", "マ", "ミ", "ム", "メ", "モ", "ャ", "ヤ", "ュ", "ユ", "ョ",
			"ヨ", "ラ", "リ", "ル", "レ", "ロ", "ワ", "ヲ", "ン", "。", "「", "」", "、",
			"・", "ー", "゛", "゜" };

	private String hanToZen(String str) {

		for (int i = 0; i < han.length; i++) {
			while (str.indexOf(han[i]) != -1) {
				str = str.replaceAll(han[i], zenn[i]);
			}
		}
		return str;
	}

	/** MB */
	private int default_max_size = 32;

	public SimpleTextConverter(File file) {
		super(file);
	}

	public String convert() {

		// ファイルサイズが上限値に達していたら読み込みしない。
		if (file.length() / 1024 / 1024 >= default_max_size)
			return null;

		// 文字列に変換するためのバッファを用意
		StringBuffer stbf = new StringBuffer();
		try {
			FileInputStream fin = new FileInputStream(super.file);
			// InputStreamReader isr = new InputStreamReader(fin, "JISAutoDetect");
			InputStreamReader isr = new InputStreamReader(fin, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String s;
			while ((s = br.readLine()) != null) {
				stbf.append(s);
				stbf.append(CR);
			}
			br.close();
			isr.close();
			fin.close();
		} catch (RuntimeException e) {
			e.printStackTrace(System.err);
			System.err.println("#####################" + super.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hanToZen(stbf.toString());
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String paht = "C:\\Documents and Settings\\haruyosi\\デスクトップ\\郵便番号A.txt";
		SimpleTextConverter cnv = new SimpleTextConverter(new File(paht));
		System.out.println(cnv.convert());
	}
}
