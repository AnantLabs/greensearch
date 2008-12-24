package green.search.crawler.main;

public class ReportInfo {

	private double page;

	private double time;

	public ReportInfo(double page, double time) {
		this.page = page;
		this.time = time;
	}

	public double getPage() {
		return page;
	}

	public void setPage(double page) {
		this.page = page;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

}
