package green.search.crawler.main;

import green.search.crawler.CrawlerException;
import green.search.crawler.fs.FsCrawler;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.solr.client.solrj.SolrServerException;

public class WorkerThread extends Thread {

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
		System.out.println("### kirizo スレッドを開始します。");
	}

	public WorkerThread() {
	}

	public static void startImmediate() {
		START = true;
	}

	public static boolean isStart() {
		return START;
	}

	public static void saveInfo(BoundedRangeModelHodler holder, ConfigInfo info) {
		WorkerThread.holder = holder;
		WorkerThread.info = info;
		System.out.println("### config info = " + WorkerThread.info);
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
				if (info != null) {
					ConfigInfoHolder.setConfigInfo(info);
				}
				if (info != null && START) {
					crawl();
					START = false;
					IMMEDIATE = false;
				}
				checkStartStatus();
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
		// クローリングの開始
		holder.get(0).setValue(0);
		long startTime = System.currentTimeMillis();
		FsCrawler crawler = new FsCrawler();
		crawler.initSolrClient(info.getSolrUrl());
		File startDir = new File(info.getDirectoryStr());
		if (info.isDelete()) {
			crawler.removeAll();
		}
		crawler.crawl(startDir);
		crawler.commit();
		// 進捗にステップの完了を報告
		holder.get().setJobid(holder.get().getJobid() + 1);

		holder.get(1).setValue(0);
		holder.get(1).setMaximum(1);
		if (info.getOptimaze()) {
			crawler.optimize();
		}
		holder.get(1).setValue(1);
		// 進捗にステップの完了を報告
		holder.get().setJobid(holder.get().getJobid() + 1);

		long endTime = System.currentTimeMillis();
		ReportInfo rinfo = new ReportInfo(crawler.getFileNum(),
				(endTime - startTime) / 1000);
		ReportHolder.addReport(rinfo);
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
