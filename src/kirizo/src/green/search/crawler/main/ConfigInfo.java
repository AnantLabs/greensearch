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

	private static final long serialVersionUID = 1L;

	protected static BoundedRangeModelHodler HOLDER = new BoundedRangeModelHodler();

	static {
		initPocess();
	}

	private String directoryStr;

	private String solrUrl = "http://localhost:8080/morry/";

	private String execstatus = "week";

	private boolean optimaze = true;
	
	private boolean delete = true;

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public boolean getOptimaze() {
		return optimaze;
	}

	public void setOptimaze(boolean optimaze) {
		this.optimaze = optimaze;
	}

	public String getExecstatus() {
		return execstatus;
	}

	public void setExecstatus(String execstatus) {
		this.execstatus = execstatus;
	}

	public String saveInfo() {
		WorkerThread.saveInfo(HOLDER, this);
		return "index";
	}

	public String startCrowl() {

		this.saveInfo();

		initPocess();
		WorkerThread.startImmediate();

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
