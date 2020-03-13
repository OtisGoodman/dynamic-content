package sybyline.vinyarion.dynamiccontent.api;

import java.io.InputStream;

public interface Resource {

	public void download();

	public void setDownloaded(boolean downloaded);

	public boolean isDownloaded();

	public boolean existsAsDownload();

	public boolean existsAsFile();

	public InputStream retrieve();

}
