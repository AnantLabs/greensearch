package green.search.crawler.main;

import green.search.crawler.fs.FsCrawler; // import green.search.crawler.lsi.matrix.SparseMatrix;
// import green.search.lsi.LsaFacade;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class WorkerThread extends Thread implements Serializable {

	BoundedRangeModelHodler holder;

	ConfigInfo info;

	/**
	 * @param holder
	 * @param info
	 */
	public WorkerThread(BoundedRangeModelHodler holder, ConfigInfo info) {
		this.holder = holder;
		this.info = info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		BoundedRangeModelHodler.set(this.holder);
		// SparseMatrix sm = null, tsm = null;
		try {
			// クローリングの開始
			holder.get(0).setValue(0);
			FsCrawler crawler = new FsCrawler();
			crawler.initSolrClient(info.getSolrUrl());
			File startDir = new File(info.getDirectoryStr());
			crawler.removeAll();
			crawler.crawl(startDir);
			crawler.commit();
			crawler.optimize();

			// // 意味インデックスの作成
			// holder.get(1).setValue(0);
			// LsaFacade lsafacade = new LsaFacade(info);
			// sm = lsafacade.createCrsMatrix("CRS");
			// traceMatrix(sm, false);
			// // 行列の転置
			// holder.get(2).setValue(0);
			// // tsm = sm.transpose(sm, "CCS", true);
			// // traceMatrix(tsm, true);
			//
			// // 意味空間の解析
			// holder.get(3).setValue(0);
			// lsafacade.executeLatentSemanticAnalysis(sm);
			// // 解析結果をLuceneに保存する。
			// holder.get(4).setValue(0);
			// lsafacade.saveIndex();
			//
			// sm.close();
			// // tsm.close();
			//
			// sm.relese();
			// // tsm.relese();

		} catch (Exception e) {
			// try {
			// if (sm != null)
			// sm.close();
			// if (tsm != null)
			// tsm.close();
			// } catch (IOException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param A
	 * @param CCR
	 * @throws IOException
	 */
	// private void traceMatrix(SparseMatrix A, boolean CCR) throws IOException
	// {
	// System.out.println("## cols = " + A.getCols());
	// System.out.println("## rows = " + A.getRows());
	// System.out.println("## vals = " + A.getVals());
	// // for (int i = 0; i < (CCR ? A.getCols() : A.getRows()) + 1; i++) {
	// // System.out.print("\t" + A.getPointer(i));
	// // }
	// // System.out.println();
	// // for (int i = 0; i < A.getVals(); i++) {
	// // System.out.print("\t" + A.getIndex(i));
	// // }
	// // System.out.println();
	// // for (int i = 0; i < A.getVals(); i++) {
	// // System.out.print("\t" + A.getValue(i));
	// // }
	// // System.out.println();
	// }
	/**
	 * @param a
	 * @param b
	 * @return
	 */
	int imax(int a, int b) {
		return (a > b) ? a : b;
	}

}
