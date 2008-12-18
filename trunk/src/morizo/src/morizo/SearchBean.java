package morizo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

public class SearchBean implements Serializable {

	private static final String CAT = "content";

	private String queryString = "";

	private String sort = "title";

	private static final String SORT_LSA = "lsa";
	private static final String SORT_TITLE = "title";
	private static final String SORT_DATE = "date";

	private List<ListElement> result = new ArrayList<ListElement>();

	private QueryResponse rsp = null;

	transient private SolrServer server = null;

	private UIComponent _searchResultComponent;

	private int num = 0;

	public SearchBean() {
		initSolrClient();
	}

	public void setComponent(UIComponent component) {
		_searchResultComponent = component;
	}

	public UIComponent getComponent() {
		return _searchResultComponent;
	}

	public int getNum() {
		return this.num;
	}

	public String getQuery() {
		return queryString;
	}

	public void setQuery(String queryString) {
		this.queryString = queryString;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String parseQuery(String q) {
		if (q == null || q.indexOf(CAT) != -1)
			return q;
		// 全角空白文字を置き換え
		StringBuffer stbf = new StringBuffer();
		String retq = q.replace("　", " ");
		StringTokenizer st = new StringTokenizer(retq, " ");
		while (st.hasMoreElements()) {
			String sts = st.nextToken();
			if (sts.trim().length() == 0)
				continue;
			if (SORT_TITLE.equals(sort)) {
				stbf.append("+title");
				stbf.append(":");
				stbf.append(sts);
				stbf.append(" ");
			} else if (SORT_LSA.equals(sort)) {
				stbf.append("+" + CAT);
				stbf.append(":");
				stbf.append(sts);
				stbf.append(" ");
			}
		}

		return stbf.toString();
	}

	public List<ListElement> getResult() {
		return this.result;
	}

	private void initSolrClient() {
		String url = "http://localhost:8080/morry";
		try {
			CommonsHttpSolrServer server = new CommonsHttpSolrServer(url);
			server.setConnectionTimeout(100);
			server.setDefaultMaxConnectionsPerHost(100);
			server.setMaxTotalConnections(10);

			this.server = server;

			System.out.println("### server is " + this.server);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String search() {
		long start = System.currentTimeMillis();
		try {
			System.out.println("### query = " + queryString);
			SolrQuery query = new SolrQuery();
			query.setQuery(parseQuery(queryString));
			query.setRows(500);
			query.setHighlight(true);
			query.addHighlightField(CAT);
			query.setHighlightFragsize(200);

			if (sort.equals(SORT_DATE)) {
				query.setSortField("document_dt", ORDER.desc);
			}
			if (this.server == null) {
				initSolrClient();
			}

			QueryResponse rsp = this.server.query(query);

			Map<String, Map<String, List<String>>> hmap = rsp.getHighlighting();
			// System.out.println(hmap);

			this.result.clear();
			for (Object obj : rsp.getResults()) {
				SolrDocument sd = (SolrDocument) obj;
				String id = (String) sd.getFieldValue("id");
				String title = (String) sd.getFieldValue("title");
				Date date = (Date) sd.getFieldValue("document_dt");

				Map<String, List<String>> m = hmap.get(id);
				List<String> l = m.get("content");
				String content = null;
				if (l == null) {
					content = (String) sd.getFieldValue("content");
				} else {
					content = l.toString();
				}
				this.result.add(new ListElement(id, title, content, date));
			}

			this.num = rsp.getResults().size();
		} catch (Exception e) {
			e.printStackTrace();
		}

		long end = System.currentTimeMillis();
		System.out.println("### " + (end - start) / 1000 + "秒");

		return "success";
	}

	/**
	 * 
	 */
	public String toDownload() {
		System.out.println("### toDownload = ");

		return "download";
	}

	/**
	 * 画面表示用構造体
	 * 
	 * @author haruyosi
	 */
	public class ListElement {

		private String id = null;
		private String title = null;
		private String shortContent = null;
		private Date docDate = null;

		public ListElement(String id, String title, String shortContent,
				Date date) {
			this.id = id;
			this.title = title;
			this.shortContent = shortContent;
			this.docDate = date;
		}

		public String getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public String getFileName() {
			String restr = title;
			try {
				restr = new String(restr.getBytes(), "ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return restr;
		}

		public String getShortContent() {
			return shortContent;
		}

		public void sendDocument(FacesContext context, OutputStream outputStream)
				throws IOException {

			System.out.println("## this.id = " + this.id);

			File file = new File(this.id);
			FileInputStream fin = new FileInputStream(file);
			int contents;
			while ((contents = fin.read()) != -1) {
				outputStream.write(contents);
			}
			outputStream.flush();
		}

		public String doLaunch() {
			System.out.println("## doLaunch.id = " + this.id);
			return "download";
		}

		public void retuenLsn() {
			System.out.println("## ret.id = " + this.id);
		}

		public Date getDocDate() {
			return docDate;
		}
	}
}
