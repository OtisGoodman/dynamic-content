package sybyline.vinyarion.dynamiccontent.api;

public interface ResourceContext {

	public void initialize();

	public void setVerbose(boolean verbose);

	public boolean isVerbose();

	public void setDownloadAll(boolean downloadAll);

	public void reDownloadAll();

	public boolean isDownloadAll();

	public String getResourcePathFromID(String id);

	public Resource getResource(String id);

	public long getRemoteVersion();

}
