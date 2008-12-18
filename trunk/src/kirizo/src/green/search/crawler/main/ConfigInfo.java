package green.search.crawler.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;

import org.apache.myfaces.trinidad.component.core.CorePoll;
import org.apache.myfaces.trinidad.component.core.output.CoreProgressIndicator;
import org.apache.myfaces.trinidad.event.PollEvent;
import org.apache.myfaces.trinidad.event.PollListener;
import org.apache.myfaces.trinidad.model.BoundedRangeModel;
import org.apache.myfaces.trinidad.model.DefaultBoundedRangeModel;

public class ConfigInfo implements Serializable {

	protected static BoundedRangeModelHodler HOLDER = new BoundedRangeModelHodler();

	static {
		initPocess();
	}

	protected volatile transient WorkerThread _processThread = null;

	private String directoryStr;

	private String solrUrl = "http://localhost:8080/morry/";

	public String startCrowl() {
		if (_processThread == null || !_processThread.isAlive()) {
			initPocess();
			_processThread = new WorkerThread(HOLDER, this);
			_processThread.setName("kirizo");
			_processThread.start();

		}
		return "progressStart";
	}

	private static void initPocess() {
		if (HOLDER.size() != 0) {
			HOLDER = new BoundedRangeModelHodler();
			HOLDER.set(0, new DefaultBoundedRangeModel(-1, 1));
		} else {
			HOLDER.addBoundedRangeModel(new DefaultBoundedRangeModel(-1, 1));
		}
	}

	public BoundedRangeModelHodler getProgressModels() {
		return HOLDER;
	}

	protected void endProcess() {
		_processThread = null;
	}

	public String getDirectoryStr() {
		return directoryStr;
	}

	public void setDirectoryStr(String path) {
		directoryStr = path;
	}

	public String getSolrUrl() {
		return solrUrl;
	}

	public void setSolrUrl(String solrUrl) {
		this.solrUrl = solrUrl;
	}
}
