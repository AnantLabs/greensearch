package green.search.query;

import green.search.lsi.matrix.QueryMatrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Weight;
import org.apache.lucene.store.FSDirectory;

public class SVDWeight implements Weight {

	// List<Double> queryVec = new ArrayList<Double>();

	private static IndexReader reader;

	private Query query;

	private Weight weight;

	private QueryMatrix qm;

	// private RealMatrixImpl qm;

	private static RealMatrix z_vt_k = null;

	private static RealMatrix ut_k = null;

	private static List<Term> TERMDIC = null;

	// private static ThreadLocal<RealMatrix> QL = new
	// ThreadLocal<RealMatrix>();

	// private static ThreadLocal<RealMatrix> UTDQ = new
	// ThreadLocal<RealMatrix>();

	Searcher searcher;

	public SVDWeight(Searcher searcher, Query query) throws IOException {
		this.query = query;
		this.searcher = searcher;
		this.weight = query.weight(searcher);
	}

	public Explanation explain(IndexReader arg0, int arg1) throws IOException {
		System.out.println("## SVDWeight:explain");
		return null;
	}

	public Query getQuery() {
		System.out.println("## SVDWeight:getQuery");
		return null;
	}

	public float getValue() {
		System.out.println("## SVDWeight:getValue");
		return 0;
	}

	public void normalize(float arg0) {
		System.out.println("## SVDWeight:normalize");
	}

	/**
	 * @param query
	 * @return
	 */
	private Term[] getTermsFromQuery(Query qquery) {
		Term[] queryTerms = null;

		if (qquery instanceof PhraseQuery) {

			PhraseQuery pq = (PhraseQuery) qquery;
			queryTerms = pq.getTerms();

		} else if (qquery instanceof TermQuery) {

			TermQuery tq = (TermQuery) qquery;
			Term t = tq.getTerm();
			queryTerms = new Term[] { t };

		} else if (qquery instanceof BooleanQuery) {

			BooleanQuery bq = (BooleanQuery) qquery;
			List<BooleanClause> clauses = bq.clauses();

			List<Term> tlist = new ArrayList<Term>();
			for (BooleanClause bc : clauses) {
				if (bc.getQuery() instanceof PhraseQuery
						|| bc.getQuery() instanceof TermQuery) {
					Term[] terms = getTermsFromQuery(bc.getQuery());
					for (Term t : terms) {
						tlist.add(t);
					}
				}
			}
			queryTerms = new Term[tlist.size()];
			queryTerms = tlist.toArray(queryTerms);
		}// 他にも対応するべきクエリがあるが、とりあえずここまで。
		return queryTerms;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.lucene.search.Weight#scorer(org.apache.lucene.index.IndexReader
	 * )
	 */
	public Scorer scorer(IndexReader reader) throws IOException {
		SVDWeight.reader = reader;

		Term[] queryTerms = getTermsFromQuery(this.query);

		if (TERMDIC == null) {
			System.out.println("### 辞書を作成");
			TERMDIC = new ArrayList<Term>();
			TermEnum te = reader.terms();
			while (te.next()) {
				Term t = te.term();
				TERMDIC.add(t);
			}
		}
		// 質問ベクトルを作成
		if (queryTerms != null) {
			if (qm == null || qm.getRowDimension() != queryTerms.length) {
				qm = new QueryMatrix(queryTerms.length);
			} else {
				qm.reset();
			}

			for (Term term : TERMDIC) {
				boolean ent = false;
				for (Term qt : queryTerms) {
					if (term.equals(qt)) {
						qm.addWightValue(1.0);
						ent = true;
					}
				}
				if (!ent) {
					qm.addWightValue(0.0);
				}
			}
		}

		if (z_vt_k == null || ut_k == null) {
			loadMatrix();
		}

		return new SVDScorer(new SVDZeroSimilarity());
	}

	public static void loadMatrix() throws IOException {
		System.out.println("## READ..");
		z_vt_k = readMatrix("SVD_DOC_SVD_ZVt");
		ut_k = readMatrix("SVD_DOC_SVD_Ut");
		System.out.println("## READED");

		TERMDIC = null;
	}

	/**
	 * @param idf
	 * @return
	 * @throws IOException
	 */
	private static RealMatrix readMatrix(String idf) throws IOException {

		FSDirectory fsdirectory = (FSDirectory) reader.directory();
		File f = fsdirectory.getFile();
		File fmat = new File(f.getPath() + "/../../mat/" + idf);
		RealMatrix mat = null;
		if (fmat.exists()) {
			FileInputStream inFile = new FileInputStream(fmat);
			ObjectInputStream inObject = new ObjectInputStream(inFile);
			try {
				mat = (RealMatrix) inObject.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			inObject.close();
			inFile.close();
		}

		// System.out.println("### " + mat);
		return mat;
	}

	/**
	 * @param matStr
	 * @return
	 * @throws IOException
	 */
	public float sumOfSquaredWeights() throws IOException {
		System.out.println("## SVDWeight:sumOfSquaredWeights");
		return 0;
	}

	/**
	 * @author haruyosi
	 */
	class SVDScorer extends Scorer {

		Scorer scorer;

		RealMatrix utdq = null;

		// RealMatrix upper = null;

		double nrmdr = -1;

		protected SVDScorer(Similarity similarity) throws IOException {
			super(similarity);
			scorer = weight.scorer(reader);
		}

		@Override
		public Explanation explain(int arg0) throws IOException {
			System.out.println("## SVDScorer:explain");
			return scorer.explain(arg0);
		}

		@Override
		public float score() throws IOException {

			int docnm = scorer.doc();

			// 質問ベクトルのサイズを調整。
			// 質問ベクトルの作成
			// System.out.println("z_vt_k = " + z_vt_k.getClass());
			// System.out.println("z_vt_k c = " + z_vt_k.getColumnDimension());
			// System.out.println("z_vt_k r = " + z_vt_k.getRowDimension());
			// System.out.println("ut_k = " + ut_k.getClass());
			// System.out.println("ut_k c = " + ut_k.getColumnDimension());
			// System.out.println("ut_k r = " + ut_k.getRowDimension());

			// RealMatrix utdq = null;
			// RealMatrix qm = null;
			if (utdq == null) {
				System.out.println("q = start");

				// System.out.println("qm_c = " + qm.getColumnDimension());
				// System.out.println("qm_r = " + qm.getRowDimension());

				System.out.println("ut_k = " + ut_k.getClass());
				System.out.println("z_vt_k = " + z_vt_k.getClass());
				utdq = ut_k.multiply(qm);
				System.out.println("q = end");
			}

			// System.out.println("z_vt_k = " + z_vt_k.getColumnDimension());
			// System.out.println("z_vt_k = " + z_vt_k.getRowDimension());

			RealMatrix z_vt_k_e = z_vt_k.getColumnMatrix(docnm);

			// ノルムの計算
			double nrmdl = z_vt_k_e.getNorm();
			if (nrmdl == 0)
				return 0;

			// System.out.println("z_vt_k_e = " + z_vt_k_e.getNorm());
			// System.out.println("z_vt_k_e = " +
			// z_vt_k_e.getColumnDimension());
			// System.out.println("z_vt_k_e = " + z_vt_k_e.getRowDimension());

			RealMatrix upper = z_vt_k_e.transpose().multiply(utdq);

			// System.out.println("z_vt_k_e = " + z_vt_k_e.getClass());
			// System.out.println("upper_c = " + z_vt_k_e.getNorm());
			// System.out.println("upper_c = " + z_vt_k_e.getColumnDimension());
			// System.out.println("upper_r = " + z_vt_k_e.getRowDimension());

			// ノルムの計算
			if (nrmdr == -1) {
				nrmdr = qm.getNorm();
				System.out.println("### nrmdr = " + nrmdr);
			}

			// System.out.println("## nrmdl = " + nrmdl);
			// System.out.println("## nrmdr = " + nrmdr);
			// System.out.println("## upper.getNorm() = " + upper.getNorm());

			// 類頻度の計算
			double sim = Math.pow(10, 16) * (1 + upper.getNorm()) / (nrmdl * nrmdr);

			// if (sim != 0)
			System.out.println("### docnm = " + docnm + "\tsim = "
					+ (float) (sim) + "\t: " + (1 + upper.getNorm()) + "/ ("
					+ (nrmdl * nrmdr) + ")" + "\t: " + this.scorer.score());

			return (float) sim;
		}

		@Override
		public int doc() {
			return scorer.doc();
		}

		@Override
		public boolean next() throws IOException {
			return scorer.next();
		}

		@Override
		public boolean skipTo(int arg0) throws IOException {
			System.out.println("## SVDScorer:skipTo");
			return false;
		}

	}

	/**
	 * @author haruyosi
	 */
	class SVDZeroSimilarity extends Similarity {

		@Override
		public float coord(int arg0, int arg1) {
			System.out.println("## SVDZeroSimilarity:coord");
			return 0;
		}

		@Override
		public float idf(int arg0, int arg1) {
			System.out.println("## SVDZeroSimilarity:idf");
			return 0;
		}

		@Override
		public float lengthNorm(String arg0, int arg1) {
			System.out.println("## SVDZeroSimilarity:lengthNorm");
			return 0;
		}

		@Override
		public float queryNorm(float arg0) {
			System.out.println("## SVDZeroSimilarity:queryNorm");
			return 0;
		}

		@Override
		public float sloppyFreq(int arg0) {
			System.out.println("## SVDZeroSimilarity:sloppyFreq");
			return 0;
		}

		@Override
		public float tf(float arg0) {
			System.out.println("## SVDZeroSimilarity:tf");
			return 0;
		}
	}

}
