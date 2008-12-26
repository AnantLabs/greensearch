package green.search.lsi;

import green.search.lsi.SddConputer;
import green.search.lsi.SddConputer.Sdd;
import green.search.lsi.matrix.BigDiagMatrix;
import green.search.lsi.matrix.CRSMatrix;
import green.search.lsi.matrix.FileSparseMatrix;
import green.search.lsi.matrix.SparseMatrix;
import green.search.lsi.matrix.TwoBitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.core.SolrCore;

public class LsaFacade {

	// private static int JOBID = 1;

	private static final String PATH_AD = "/data/index";

	// String baseDir;
	SolrCore core;

	// private SolrServer server = null;

	private RealMatrix z_vt_k;

	private RealMatrix ut_k;

	public LsaFacade(SolrCore core) throws IOException {
		this.core = core;
	}

	/**
	 * LuceneにCRSフォーマットの行列データを作成する。
	 * 
	 * @throws IOException
	 */
	public FileSparseMatrix createCrsMatrix(String idf) throws IOException {

		String baseDir = core.getIndexDir();
		System.out.println("### >> baseDir = " + baseDir);
		String full_path = baseDir;
		File directory = new File(full_path);
		IndexReader reader = IndexReader.open(directory);
		// 全文章の単語一覧
		TermEnum te = reader.terms();

		// 大規模疎行列オブジェクトを生成
		File mat = new File(core.getDataDir() + "../mat");
		FileSparseMatrix sm = new FileSparseMatrix(mat, idf);

		int jcntmax = 0;
		while (te.next()) {
			jcntmax++;
		}

		te = reader.terms();

		int jcnt = 0;
		// 全単語一覧だけループ
		sm.addPtr();
		if (true) {
			while (te.next()) {
				Term t = te.term();
				// 残差IDFの算出
				double gi = ridf(reader, te);
				double nj = 1;
				TermDocs td = reader.termDocs(t);
				double d = 1;
				// while (td.next() && t.field().equals("content")) {
				while (td.next()) {
					int docnum = td.doc();
					double tf = tf(td.freq());
					// 重みの決定
					if (gi == 0) {
						d = 0;
					} else {
						d = tf * gi / nj;
						// d = 1;
					}
					// System.out.println("### tf = " + tf);
					sm.addValue(docnum, d);
					// if (d < min)
					// min = d;
					// if (max < d)
					// max = d;
				}
				sm.addPtr();
				jcnt++;
				// BoundedRangeModelHodler.get().get(JOBID).setValue(jcnt);
			}
		} else {

			// 検算
			sm.addValue(0, 1);
			sm.addValue(4, 1);

			sm.addPtr();
			sm.addValue(1, 1);
			sm.addValue(3, 1);
			sm.addValue(5, 1);

			sm.addPtr();
			sm.addValue(1, 1);

			sm.addPtr();
			sm.addValue(1, 1);

			sm.addPtr();
			sm.addValue(2, 1);
			sm.addValue(5, 1);

			sm.addPtr();
			sm.addValue(0, 1);
			sm.addValue(1, 1);
			sm.addValue(2, 1);
			sm.addValue(3, 1);

			sm.addPtr();
			sm.addValue(2, 1);
			sm.addValue(3, -2);
			sm.addValue(4, 1);

			sm.addPtr();
			sm.addValue(0, 1);
			sm.addValue(1, 1);
			sm.addPtr();

			// BoundedRangeModelHodler.get().get(JOBID).setValue(jcntmax);
		}
		sm.flush();

		sm.countColRow();

		return sm;

	}

	/**
	 * 単語の重み算出のためのRIFDの算出を行う。
	 * 
	 * @param reader
	 * @param te
	 * @return
	 * @throws IOException
	 */
	private static double ridf(IndexReader reader, TermEnum te)
			throws IOException {
		double n = reader.numDocs();
		double ni = reader.docFreq(te.term());
		double log1 = Math.log(n / ni);
		double fi = log1 * ni;
		if (fi == 0) {
			return 0;
		}
		double log2 = Math.log(1 - Math.exp(-1 * fi / n));
		double ridf = (log1 + log2);
		// if (ridf < 0) {
		// System.out.println(ridf + ":" + log2 + " : " + log1 + " : " + fi
		// + " : " + ni + " : " + n + " : " + Math.exp(-1 * fi / n)
		// + " : " + te.term().text());
		// }
		return ridf;
	}

	/**
	 * 単語の頻度を算出する。
	 * 
	 * @param freq
	 * @return
	 */
	private double tf(double freq) {
		return Math.log(1.0 + freq);
	}

	public void executeLatentSemanticAnalysis(SparseMatrix at)
			throws IOException {

		// BoundedRangeModelHodler.get().get(3).setMaximum(1);

		// +-----+
		// | SDD |
		// +-----+
		// 
		int TERMS = 100;
		double ACCR = Double.MAX_VALUE * (-1);
		// double ACCR = 0;
		double TOL = 0.1d;
		// double TOL = 0.001d;
		int ITS = 100;
		int YINIT = 1;
		// private static int BFLAG = 0;

		// int i; /* counter */
		int terms = TERMS; /* number of terms in SDD */
		double accr = ACCR; /* desired accuary of SDD */
		double tol = TOL; /* tolerance */
		int its = ITS; /* max its for inner iteration */
		int yinit = YINIT; /* choice for initialization */
		// private static int bflag = BFLAG; /* I/O in binary format? */
		int eterms = 0; /* no. of terms in existing sdd */

		terms = 128;
		SddConputer ssdcomp = new SddConputer();
		Sdd S = ssdcomp.compute_sdd(at, eterms, terms, accr, its, tol, yinit);

		BigDiagMatrix d = ssdcomp.write_dmatrix(S.D);
		// TwoBitMatrix Vt = ssdcomp.write_smatrix(S.X);
		// TwoBitMatrix UT = ssdcomp.write_smatrix(S.Y);
		CRSMatrix Vt = ssdcomp.write_smatrix(S.X);
		CRSMatrix UT = ssdcomp.write_smatrix(S.Y);
		// System.out.println("### Vt = \n" + Vt.toString());
		this.z_vt_k = d.multiply(Vt);
		System.out.println();
		this.ut_k = UT;
		// System.out.println("### Ut = \n" + ut_k.toString());

	}

	/**
	 * 解析された意味空間情報を各文章に分解して保存する。
	 * 
	 * @throws IOException
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws SolrServerException
	 */
	public void saveIndex() throws IOException, SolrServerException {

		// String baseDir = info.getServerDir();
		// String full_path = baseDir + "/" + PATH_AD;
		// File directory = new File(full_path);
		// IndexWriter writer = new IndexWriter(directory, null,
		// new MaxFieldLength(128));

		int docnum = ut_k.getColumnDimension();

		// BoundedRangeModelHodler.get().get(4).setMaximum(1);

		System.out.println("### docnum = " + docnum);
		// UpdateResponse res = null;
		// for (int i = 0; i < docnum; i++) {

		// SolrInputDocument doc = new SolrInputDocument();
		// doc.addField("id", "SVD_DOC");

		// double[] zvt = this.z_vt_k.getRow(i);
		System.out.println("zk_vkt_c = " + z_vt_k.getColumnDimension());
		System.out.println("zk_vkt_r = " + z_vt_k.getRowDimension());
		// doc.addField("SVD_ZVt", toBase64String(this.z_vt_k));

		this.saveMatrix(this.z_vt_k, "SVD_DOC_SVD_ZVt");

		// double[] ut = this.ut_k.getColumn(i);
		System.out.println("ut_k = " + ut_k.getColumnDimension());
		System.out.println("ut_k = " + ut_k.getRowDimension());
		// doc.addField("SVD_Ut", toBase64String(this.ut_k));

		this.saveMatrix(this.ut_k, "SVD_DOC_SVD_Ut");

		// res = this.server.add(doc);
		// BoundedRangeModelHodler.get().get(4).setValue(1);
		// }

		// res = this.server.commit();

		// 進捗にステップの完了を報告
		// BoundedRangeModelHodler.get().setJobid(
		// BoundedRangeModelHodler.get().getJobid() + 1);
	}

	/**
	 * @param mat
	 * @return
	 * @throws IOException
	 */
	// private String toBase64String(RealMatrix mat) throws IOException {
	//
	// String restr = null;
	// StringWriter sw = new StringWriter();
	// Base64OutputStream b64o = new Base64OutputStream(sw);
	// ObjectOutputStream oout = new ObjectOutputStream(b64o);
	// oout.writeObject(mat);
	// oout.close();
	// b64o.close();
	// restr = sw.toString();
	// sw.close();
	// return restr;
	// }
	/**
	 * @param mat
	 * @throws IOException
	 */
	private void saveMatrix(RealMatrix mat, String idf) throws IOException {
		String baseDir = this.core.getDataDir();
		File fmat = new File(baseDir + "../mat/" + idf);
		FileOutputStream outFile = new FileOutputStream(fmat);
		ObjectOutputStream oout = new ObjectOutputStream(outFile);
		oout.writeObject(mat);
		oout.close();
		outFile.close();
	}

	/**
	 * double[]をbyte[]に変換する。
	 * 
	 * @param dd
	 * @return
	 */
	// private byte[] changeToByteFromDoubles(double[] dd) {
	// byte[] bt = new byte[dd.length];
	// for (int i = 0; i < dd.length; i++) {
	// bt[i] = (byte) dd[i];
	// }
	//
	// return bt;
	// }
}
