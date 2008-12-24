package green.search.crawler.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ReportHolder {

	private static List<List<Double>> PAGE_LIST = new ArrayList<List<Double>>();

	private static List<List<Double>> TIME_LIST = new ArrayList<List<Double>>();

	private static List<String> LABEL_LIST = new ArrayList<String>();

	public static void addReport(ReportInfo info) {

		PAGE_LIST.add(Arrays.asList(new Double[] { info.getPage() }));
		TIME_LIST.add(Arrays.asList(new Double[] { info.getTime() }));
		Date now = new Date();
		SimpleDateFormat df = new SimpleDateFormat("MM/dd");
		String fmstr = df.format(now);
		LABEL_LIST.add(fmstr);

		// delete old data
		if(PAGE_LIST.size() > 7){
			PAGE_LIST.remove(0);
			TIME_LIST.remove(0);
			LABEL_LIST.remove(0);
		}
	}

	public static List<List<Double>> getPAGE_LIST() {
		return PAGE_LIST;
	}

	public static List<List<Double>> getTIME_LIST() {
		return TIME_LIST;
	}

	public static List<String> getLABEL_LIST() {
		return LABEL_LIST;
	}

}