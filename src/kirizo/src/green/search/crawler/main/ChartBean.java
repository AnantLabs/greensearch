package green.search.crawler.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.myfaces.trinidad.model.ChartModel;

public class ChartBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final ChartModel _pageChartModel = new PageChartModel();

	private final ChartModel _timeChartModel = new TimeChartModel();

	public ChartModel getPageValue() {
		return _pageChartModel;
	}

	public ChartModel getTimeValue() {
		return _timeChartModel;
	}

	private class TimeChartModel extends ChartModel {

		private final List<String> _seriesLabels = Arrays
				.asList(new String[] { "time" });

		@Override
		public List<String> getGroupLabels() {
			return ReportHolder.getLABEL_LIST();
		}

		@Override
		public List<String> getSeriesLabels() {
			return _seriesLabels;
		}

		@Override
		public List<List<Double>> getYValues() {
			return ReportHolder.getTIME_LIST();
		}

	}

	private class PageChartModel extends ChartModel {

		private final List<String> _seriesLabels = Arrays
				.asList(new String[] { "pages" });

		@Override
		public List<String> getGroupLabels() {
			return ReportHolder.getLABEL_LIST();
		}

		@Override
		public List<String> getSeriesLabels() {
			return _seriesLabels;
		}

		@Override
		public List<List<Double>> getYValues() {
			return ReportHolder.getPAGE_LIST();
		}
	}

}
