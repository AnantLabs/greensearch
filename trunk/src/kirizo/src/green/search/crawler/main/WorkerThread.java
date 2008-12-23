package green.search.crawler.main;

import green.search.crawler.CrawlerException;
import green.search.crawler.fs.FsCrawler;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.apache.solr.client.solrj.SolrServerException;

public class WorkerThread extends Thread implements Serializable {

	static BoundedRangeModelHodler holder = null;

	static ConfigInfo info = null;

	static boolean START = false;

	static boolean IMMEDIATE = false;

	static WorkerThread kirizo = null;

	static {
		if (kirizo == null || !kirizo.isAlive()) {
			kirizo = new WorkerThread();
			kirizo.setName("kirizo");
			kirizo.start();
		}
		System.out.println("### kirizo �X���b�h���J�n���܂��B");
	}

	public WorkerThread() {
	}

	public static void startImmediate() {

		IMMEDIATE = true;
	}

	public static void saveInfo(BoundedRangeModelHodler holder, ConfigInfo info) {
		WorkerThread.holder = holder;
		WorkerThread.info = info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		try {
			while (true) {
				Thread.sleep(1000 * 1);
				checkStartStatus();
				if ((info != null && START) || IMMEDIATE) {
					crawl();
					START = false;
					IMMEDIATE = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkStartStatus() {
		Calendar now = Calendar.getInstance();
		if (info.getExecstatus().equals("week")) {
			int d = now.get(Calendar.DAY_OF_WEEK);
			int h = now.get(Calendar.HOUR_OF_DAY);
			int m = now.get(Calendar.MINUTE);
			START = (d == Calendar.SUNDAY && h == 0 && m == 0);
			// System.out.println("### kirizo week " + d + " : " + h + " : " +
			// m);
		} else if (info.getExecstatus().equals("day")) {
			int h = now.get(Calendar.HOUR_OF_DAY);
			int m = now.get(Calendar.MINUTE);
			START = (h == 0 && m == 0);
			// System.out.println("### kirizo day " + h + " : " + m);
		}
	}

	private void crawl() throws SolrServerException, IOException,
			CrawlerException {
		BoundedRangeModelHodler.set(holder);
		// �N���[�����O�̊J�n
		holder.get(0).setValue(0);
		FsCrawler crawler = new FsCrawler();
		crawler.initSolrClient(info.getSolrUrl());
		File startDir = new File(info.getDirectoryStr());
		if (info.isDelete()) {
			crawler.removeAll();
		}
		crawler.crawl(startDir);
		crawler.commit();
		if (info.getOptimaze()) {
			crawler.optimize();
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
}