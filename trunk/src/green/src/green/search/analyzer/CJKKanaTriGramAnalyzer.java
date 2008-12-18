package green.search.analyzer;

import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballFilter;

public class CJKKanaTriGramAnalyzer extends Analyzer {

	/**
	 * An array containing some common English words that are not usually useful
	 * for searching and some double-byte interpunctions.
	 */
	public final static String[] STOP_WORDS = { "a", "and", "are", "as", "at",
			"be", "but", "by", "for", "if", "in", "into", "is", "it", "no",
			"not", "of", "on", "or", "s", "such", "t", "that", "the", "their",
			"then", "there", "these", "they", "this", "to", "was", "will",
			"with", "", "www" };

	private Set stopTable;

	public CJKKanaTriGramAnalyzer() {
		stopTable = StopFilter.makeStopSet(STOP_WORDS);
	}

	public CJKKanaTriGramAnalyzer(String[] stopWords) {
		stopTable = StopFilter.makeStopSet(stopWords);
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream result = new CJKKanaTriGramTokenizer(reader);
		result = new LowerCaseFilter(result);
		result = new SnowballFilter(result, "English");
		result = new StopFilter(result, stopTable);
		return result;
	}

}
