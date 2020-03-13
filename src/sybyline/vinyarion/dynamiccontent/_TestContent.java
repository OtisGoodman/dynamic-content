package sybyline.vinyarion.dynamiccontent;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class _TestContent {

	public static final File TEST_ROOT = new File(".");
	public static final String TEST_DOMAIN = "https://raw.githubusercontent.com/VinyarionHyarmendacil/dynamic-content";
	public static final String TEST_PROJECT = "dynamic-content";
	public static final String TEST_VERION = "0.0.0";

	public static void main(String[] args) {
		
		// Create the context -- this is a permenant object
		DynamicResourceContext context = new DynamicResourceContext(TEST_ROOT, TEST_DOMAIN, TEST_PROJECT, TEST_VERION);
			context.setVerbose(true);
		// Initialize the context -- only called once
		context.initialize();
		
		// Get a resource
		DynamicResource resource = context.getResource("test");
		// Retrieve downloads the file if it does not exist locally
		InputStream stream = resource.retrieve();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			reader.lines().forEach(System.err::println);
			
		DynamicResource bad = context.getResource("nonexistant");
			System.out.println("Does a nonexistant resource exist: " + bad.existsAsDownload());
		
		// TODO : Check for version information
		
	}

}
