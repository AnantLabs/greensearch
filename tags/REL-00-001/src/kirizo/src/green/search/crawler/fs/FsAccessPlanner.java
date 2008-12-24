package green.search.crawler.fs;

import green.search.crawler.CrawlerException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FsAccessPlanner {

	private List<File> accessplan = new ArrayList<File>();

	public FsAccessPlanner(File startDir) throws CrawlerException {
		if (!startDir.isDirectory()) {
			throw new CrawlerException("Parameter is not directory");
		}
		this.iterativeFind(startDir);
	}

	public List<File> getAccessList() {
		return this.accessplan;
	}

	private void iterativeFind(File directory) {
		File[] files = directory.listFiles(new FsCrawlerFilter());
		for (File f : files) {
			if (f.isDirectory()) {
				Logger.getAnonymousLogger().log(Level.INFO, f.toString());
				this.iterativeFind(f);
			} else {
				accessplan.add(f);
				Logger.getAnonymousLogger().log(Level.INFO, f.toString());
			}
		}
	}
}
