package configuration.browser;

/**
 * Created by kgr on 9/12/2016.
 */
public class WebDriverSettings {
    private String browserName;
    private String version;
    private String urlParam;
    private boolean useMobileEmulator;
    private boolean useLocalStorage;
    private boolean useProxy;
    private boolean useGrid;
    private boolean useCache;

    public WebDriverSettings(String browserName, String version, String urlParam, boolean useMobileEmulator, boolean useLocalStorage, boolean useProxy, boolean useGrid) {
        this.browserName = browserName;
        this.version = version;
        this.urlParam = urlParam;
        this.useMobileEmulator = useMobileEmulator;
        this.useLocalStorage = useLocalStorage;
        this.useProxy = useProxy;
        this.useGrid = useGrid;
    }

    public WebDriverSettings(String browserName, boolean useMobileEmulator, boolean useProxy) {
        this.browserName = browserName;
        this.useMobileEmulator = useMobileEmulator;
        this.useProxy = useProxy;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }

    public boolean isUseMobileEmulator() {
        return useMobileEmulator;
    }

    public void setUseMobileEmulator(boolean useMobileEmulator) {
        this.useMobileEmulator = useMobileEmulator;
    }

    public boolean isUseLocalStorage() {
        return useLocalStorage;
    }

    public void setUseLocalStorage(boolean useLocalStorage) {
        this.useLocalStorage = useLocalStorage;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    public boolean isUseGrid() {
        return useGrid;
    }

    public void setUseGrid(boolean useGrid) {
        this.useGrid = useGrid;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }
}
