package sybyline.vinyarion.dynamiccontent;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DynamicResourceContext {

	public static final String INDEX_JSON = "index.json";
	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
	public static final JsonParser PARSE = new JsonParser();

	public DynamicResourceContext(File allContextsLocation, String domain, String project, String version) {
		String versionedproject = project + '/' + version + '/';
		this.root = new File(allContextsLocation, versionedproject);
		this.domain = domain;
		this.project = project;
		this.version = version;
		this.internal_cache = new HashMap<String, DynamicResource>();
		this.internal_hash = ((((long)domain.hashCode()) << 32) ^ (((long)project.hashCode()) << 32)) | (((long)version.hashCode()) << 00);
		this.verbose = false;
		this.internal_branch_url_stub = domain + '/' + versionedproject;
		this.internal_json_index = null;
		if (root.exists()) {
			if (!root.isDirectory()) {
				throw new RuntimeException("File already exists as a non-directory!\nPath is: " + root.getAbsolutePath());
			}
		} else {
			if (!root.mkdirs()) {
				throw new RuntimeException("Folder could not be created!\nPath is: " + root.getAbsolutePath());
			}
		}
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void initialize() {
		try {
			String path = internal_branch_url_stub + INDEX_JSON;
			if (verbose) System.out.println("Dynamic content: Atemptint to retrieve index: " + path);
			URL indexJson = new URL(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(indexJson.openStream()));
			JsonObject json = PARSE.parse(reader).getAsJsonObject();
			this.internal_json_index = json;
			if (verbose) System.out.println("Dynamic content: retrieved index: " + json.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public final File root;
	public final String domain;
	public final String project;
	public final String version;

	private final Map<String, DynamicResource> internal_cache;
	final long internal_hash;
	boolean verbose;
	private String internal_branch_url_stub;
	private JsonObject internal_json_index;

	//@Nullable
	public String getResourcePathFromID(String id) {
		if (internal_json_index == null)
			throw new NullPointerException("index");
		JsonObject entries = internal_json_index.getAsJsonObject("entries");
		if (entries == null)
			throw new NullPointerException("entries");
		JsonElement element = entries.get(id);
		if (element == null)
			return null;
		return internal_branch_url_stub + element.getAsString();
	}

	//@Nonnull
	public DynamicResource getResource(String id) {
		return internal_cache.computeIfAbsent(id, _id -> new DynamicResource(this, _id));
	}

}
