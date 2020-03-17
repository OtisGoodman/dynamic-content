# dynamic-content

Provides dynamic content for other repositories and projects.
This uses a cache with version tracking to avoid downloading the same files repeatedly, as well as allowing for offline access of resources.
The only dependency is Google's Json library, GSON.
The tutorial will use a Github as the remote repository.

## Code Usage
You'll need an instance of `ResourceContext` in order to do anything.
Obtain one with by calling `ContextProvider.createGithub`.
You'll need the following parameters first:

<table>
    <thead>
        <tr align="center">
            <th colspan="3">Method parameters for ContextProvider.createGithub</th>
        </tr>
    </thead>
    <tbody>
        <tr align="center"><td>Name</td><td>Type</td><td>Description</td><td>Example</td></tr>
        <tr>
            <td>allContextsLocation</td>
            <td>File</td>
            <td>The root location you wish all versions of your application to cache resources</td>
            <td>new File("./stuff")</tr>
        </tr>
        <tr>
            <td>user</td>
            <td>String</td>
            <td>The Github username of the repository owner</td>
            <td>"VinyarionHyarmendacil"</td>
        </tr>
        <tr>
            <td>resourceRepository</td>
            <td>String</td>
            <td>The name of the repository which contains the remote resources</td>
            <td>"dynamic-content"</td>
        </tr>
        <tr>
            <td>projectName</td>
            <td>String</td>
            <td>The name of the project you are retrieving resources for. This allows one repository house resources for several related projects.</td>
            <td>"dynamic-content"</td>
        </tr>
        <tr>
            <td>version</td>
            <td>String</td>
            <td>The version of the project you are retrieving resources for.</td>
            <td>"0.0.0"</td>
        </tr>
    </tbody>
</table>

```java
package sybyline.vinyarion.dynamiccontent.test;
import java.io.*;
import sybyline.vinyarion.dynamiccontent.api.*;
public class ContentTest {
    public static final File TEST_ROOT = new File(".");
    public static final String TEST_USER = "VinyarionHyarmendacil";
    public static final String TEST_REPOSITORY = "dynamic-content";
    public static final String TEST_PROJECT = "dynamic-content";
    public static final String TEST_VERION = "0.0.0";
    public static void main(String[] args) {
        // Create the context -- this is a permanent object
        ResourceContext context = ContextProvider.createGithub(TEST_ROOT, TEST_USER, TEST_REPOSITORY, TEST_PROJECT, TEST_VERION);
        // Set whether the context logs info -- may call at any time
        context.setVerbose(true);
        // Initialize the context -- only called once
        context.initialize();
        // Get a resource
        Resource resource = context.getResource("test");
        // Retrieve downloads the file if it does not exist locally
        InputStream stream = resource.retrieve();
        new BufferedReader(new InputStreamReader(stream)).lines().forEach(System.out::println);
        Resource bad = context.getResource("nonexistant");
        System.out.println("Does a nonexistant resource exist: " + bad.existsAsDownload());
        try {
            bad.retrieve();
            System.out.println("Incorrect behavior: retrieved nonexistant resource");
        } catch (Exception e) {
            System.out.println("Correct behavior: failed to retrieve nonexistant resource");
        }
    }
}

```

Summary of the basics:
- Make a ResourceContext with `ContextProvider::createGithub` (or other create methods)
- Set up the context by calling `ResourceContext::initialize`
    - Calling initialize more than once is currently not recommended. In future versions, a refreshIndex method may be added to standardize this behavior.
- Get a resource by calling `ResourceContext::getResource`
    - Test whether this resource exists with a `Resource::exists` method
    - Get an `InputStream` of this resource with `Resource::retrieve`

## Repository Configuration

The current API version is `0.0.0` (./index.json: `"version-api": "0.0.0"`).

The very base of a resource repository is an `index.json`:

```javascript
{
    // The version of the current resources, different from the project's version.
    // Incrementing this will force a lazy redownload of resources in all distributed instances of your application.
    // Decrementing is not recommended, and results in undefined behavior.
    "version-resource": "0.0.0",
    // The version of dynamic-resources being run, currently unused.
    // Planned for compatibility, so newer versions can read old repositories if something changes drastically for some reason.
    // Behavior when an old version uses a newer format
    "version-api": "0.0.0",
    // The resources this repository makes available to your application.
    "entries": {
        // A resource entry. The key is the same as the string passed to ResourceContext::getResource.
        // The value must be a string denoting the path of the resource relative to the folder
        // in which this index.json resides. For example, if this index.json is at
        // <https://raw.githubusercontent.com/.../dynamic-content/0.0.0/index.json>
        // then the resource "test" must be located at
        // <https://raw.githubusercontent.com/.../dynamic-content/0.0.0/folder/test.txt>
        "test": "folder/test.txt"
    }
}
```

### Contact

If you have any questions, feel feel free to message me on Discord at `Vinyarion@0292`.
