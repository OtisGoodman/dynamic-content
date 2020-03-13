package sybyline.vinyarion.dynamiccontent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

public class DynamicResource {

	DynamicResource (DynamicResourceContext context, String id) {
		this.context = context;
		this.id = id;
		this.path = context.getResourcePathFromID(id);
		this.hash = new UUID(context.internal_hash, (((long)id.hashCode()) << 32));
		this.file = new File(context.root, hash.toString());
	}

	public final DynamicResourceContext context;
	public final String id;
	public final String path;
	public final UUID hash;
	public final File file;

	private static final int BUFFER_SIZE = 1024;

	public void download() {
		if (context.verbose) {
			System.out.println("Downloading resource: " + id + " -> " + path);
		}
		try (
			BufferedInputStream in = new BufferedInputStream(new URL(path).openStream());
			FileOutputStream out = new FileOutputStream(file);
		) {
		    byte data[] = new byte[BUFFER_SIZE];
		    int byteContent;
		    while ((byteContent = in.read(data, 0, BUFFER_SIZE)) != -1) {
		        out.write(data, 0, byteContent);
		    }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean existsAsDownload() {
		return path != null;
	}

	public boolean existsAsFile() {
		return file.exists() && file.isFile() && file.canRead();
	}

	public InputStream retrieve() {
		synchronized(this) {
			if (!existsAsFile()) {
				this.download();
			}
		}
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
