package green.search.crawler.main;

public class ConfigInfoHolder {

	private static ThreadLocal<ConfigInfo> CONFIG_INFO_TL = new ThreadLocal<ConfigInfo>();

	public static void setConfigInfo(ConfigInfo info) {
		CONFIG_INFO_TL.set(info);
	}

	public static ConfigInfo getConfigInfo() {
		return CONFIG_INFO_TL.get();
	}
}
