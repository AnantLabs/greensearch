package green.search.crawler.fs;

import green.search.crawler.CrawlerException;
import green.search.crawler.converter.DocumentConverter;
import green.search.crawler.converter.DocumentConverterFactory;
import green.search.crawler.main.BoundedRangeModelHodler;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

public class FsCrawler {

	private static int JOBID = 0;

	private SolrServer server = null;

	private List<File> files;

	/**
	 * SolrClientを初期化する。
	 * 
	 * @param url
	 */
	public void initSolrClient(String url) {
		try {
			CommonsHttpSolrServer server = new CommonsHttpSolrServer(url);
			server.setConnectionTimeout(100);
			server.setDefaultMaxConnectionsPerHost(100);
			server.setMaxTotalConnections(10);

			this.server = server;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ファイルシステムをクロールする。
	 * 
	 * @param startDir
	 * @throws CrawlerException
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws SolrServerException
	 */
	public void crawl(File startDir) throws CrawlerException, IOException,
			SolrServerException {
		FsAccessPlanner planner = new FsAccessPlanner(startDir);
		files = planner.getAccessList();
		int num = files.size();
		// 進捗管理に最大数を登録。
		BoundedRangeModelHodler.get().get(JOBID).setMaximum(num);
		for (int i = 0; i < num; i++) {
			File file = files.get(i);
			// 読み込み可能でなければアクセスしない。
			if (!file.canRead())
				continue;
			Logger.getAnonymousLogger().log(Level.INFO,
					file + " [" + file.length() / 1024 / 1024 + "] MB");
			DocumentConverter cv = DocumentConverterFactory.getInstance(file);
			String text = null;
			try {
				text = cv.convert();

			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				Logger.getAnonymousLogger().log(Level.INFO, e.getMessage(), e);
			}
			// Logger.getAnonymousLogger().log(Level.INFO, text);

			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("id", file);
			doc.addField("title", file.getName());
			doc.addField("content", text);

			doc.addField("documentdt", new Date(file.lastModified()));

			try {
				this.server.add(doc);
			} catch (SolrServerException e) {
				e.printStackTrace();
				Logger.getAnonymousLogger().log(Level.INFO, e.getMessage(), e);
			}

			int par = i * 100 / num;
			String logstr = i + "/" + num + "(" + par + "%) " + file.getName()
					+ " is indexed.";
			Logger.getAnonymousLogger().log(Level.INFO, logstr);
			// 現在の進捗を報告
			BoundedRangeModelHodler.get().get(JOBID).setValue(i + 1);
		}

	}

	/**
	 * クロールしたファイル数を返す。
	 * 
	 * @return
	 */
	public int getFileNum() {
		return files.size();
	}

	/**
	 * コミットを行う。
	 * 
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public void commit() throws SolrServerException, IOException {
		UpdateResponse res = this.server.commit();
		System.out.println("## commit" + res);
	}

	/**
	 * 最適かを行う。
	 * 
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public void optimize() throws SolrServerException, IOException {
		UpdateResponse res = this.server.optimize();
		System.out.println("## optimize" + res);
	}

	/**
	 * 検索サーバに対して検索を行う。
	 * 
	 * @param qs
	 * @throws SolrServerException
	 * @throws UnsupportedEncodingException
	 */
	private void search(String qs) throws SolrServerException,
			UnsupportedEncodingException {
		SolrQuery query = new SolrQuery();
		query.setQuery(qs);
		query.setHighlight(true);
		query.addHighlightField("content");
		query.setHighlightFragsize(100);
		QueryResponse rsp = server.query(query);
		SolrDocumentList list = rsp.getResults();
		System.out.println(rsp.getResults().size());
		System.out.println(rsp.getHighlighting());
		for (SolrDocument doc : list) {
			System.out.println(list.getMaxScore() + " : "
					+ doc.getFieldValue("id"));
		}
	}

	/**
	 * インデックスの内容を全て削除する。
	 * 
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public void removeAll() throws SolrServerException, IOException {

		UpdateResponse res = this.server.deleteByQuery("*:*");
		res = this.server.commit();
	}
}
