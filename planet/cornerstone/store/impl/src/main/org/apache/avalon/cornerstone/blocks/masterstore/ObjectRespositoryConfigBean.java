package org.apache.avalon.cornerstone.blocks.masterstore;

import java.io.File;

/**
 * @author Paul Hammant
 * @version $Revision: 1.8 $
 */
public class ObjectRespositoryConfigBean implements ObjectRespositoryConfig {

    private String url;
    private File baseDirectory;


    public void setUrl(String url) {
        this.url = url;
    }

    public String getURL() {
        return url;
    }

    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public File getBaseDirectory() {
        return baseDirectory;
    }
}
