package green.search.crawler.converter;

/**
 * 文章をインデックス可能なテキスト形式に変換するクラス。
 * 
 * @author haruyosi
 */
public interface DocumentConverter {

	/**
	 * 対象ドキュメントデータをテキスト形式に変換する。 すべてのドキュメントを完全にインデックスしたい場合以外は、
	 * このメソッドの実装で例外をスローするべきではない。
	 * 
	 * @return
	 */
	public String convert();
}
