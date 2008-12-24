package green.search.crawler.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.myfaces.trinidad.model.DefaultBoundedRangeModel;

public final class BoundedRangeModelHodler {

	private static int jobid = -1;

	private static ThreadLocal<BoundedRangeModelHodler> thlocal = new ThreadLocal<BoundedRangeModelHodler>();

	private static List<DefaultBoundedRangeModel> list = new ArrayList<DefaultBoundedRangeModel>();

	public BoundedRangeModelHodler() {
		thlocal.set(this);
		jobid = -1;
	}

	public int size() {
		return list.size();
	}

	public static BoundedRangeModelHodler get() {
		return thlocal.get();
	}

	public static void set(BoundedRangeModelHodler h) {
		thlocal.set(h);
	}

	public void addBoundedRangeModel(DefaultBoundedRangeModel model) {
		list.add(model);
	}

	public DefaultBoundedRangeModel get(int i) {
		return list.get(i);
	}

	public List<DefaultBoundedRangeModel> getBoundedRangeModels() {
		return list;
	}

	public void set(int index, DefaultBoundedRangeModel element) {
		list.set(index, element);
	}

	public int getJobid() {
		return jobid;
	}

	public void setJobid(int jobid) {
		BoundedRangeModelHodler.jobid = jobid;
	}
}
