package org.apache.avalon.apps.sevak;

import java.io.File;

public interface MultihostSevak {
    /** Role of the MultihostSevak Service*/
    public String ROLE = MultihostSevak.class.getName();

    /**
     * Deploy the given Web Application
     * @param context Context for the the webapp
     * @param pathToWebAppFolder path can be a war-archive or exploded directory
     * @throws SevakException Thrown when context already exists
     */
    void deploy(String host, String context, File pathToWebAppFolder) throws SevakException;

    /**
     * Undeploy the given WebApp
     * @param context Context for the the webapp
     * @throws SevakException Thrown if context does NOT exist
     */
    void undeploy(String host, String context) throws SevakException;
}
