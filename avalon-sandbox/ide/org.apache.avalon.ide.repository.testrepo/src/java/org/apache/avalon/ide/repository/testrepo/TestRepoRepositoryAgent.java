/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.ide.repository.testrepo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.avalon.ide.repository.Compliance;
import org.apache.avalon.ide.repository.RepositoryAgent;
import org.apache.avalon.ide.repository.RepositoryAgentEvent;
import org.apache.avalon.ide.repository.RepositoryAgentListener;
import org.apache.avalon.ide.repository.ResourceInfo;
import org.apache.avalon.ide.repository.Version;
import org.apache.avalon.ide.repository.tools.common.ComplianceGroupImpl;
import org.apache.avalon.ide.repository.tools.common.NonVersion;
import org.apache.avalon.ide.repository.tools.common.ResourceGroupInfoImpl;
import org.apache.avalon.ide.repository.tools.compliance.EmptyCompliance;
import org.apache.avalon.ide.repository.tools.compliance.GenericCompliance;

/** The URLRepositoryAgent is a generic RepositoryAgent for HTTP, FTP, file and
 *  other valid URL based repositories.
 * 
 * <p>There is at the moment a requirement that the repository must have the following
 * structure.</p>
 * <pre>
 *  ./
 *    repository.meta            - Properties file containing the Repository metainfo
 *    component.list             - Properties file containing all components in the repository
 *    &lt;component-id&gt;/            - One directory for each repository.
 *    &lt;component-id&gt;/meta.data   - Properties file with meta data of the component  
 *    &lt;component-id&gt;/&lt;artifacts&gt; - Each of the artifacts in the repository.
 * </pre>
 * <p>
 * The component.list properties file contains a single line for each component in the
 * repository. The key is equal to the component ID, and the value contains a human
 * readable name. 
 * </p>
 * @author Niclas Hedhman, niclas@hedhman.org
 */
public class TestRepoRepositoryAgent implements RepositoryAgent, Runnable
{
    private ArrayList m_ContainerTypes;
    
    private ArrayList m_Listeners;
    
    private boolean m_RefreshInProgress;
    private boolean m_Available;
    private String m_Reason;
    private Locale m_Locale;

    private URL m_RepositoryURL;
    private HashMap m_ResourceIndex;

    private String m_Name;
    private String m_Description;

    private Thread m_LoadingThread;

    private ArrayList m_LoadQueue;

    /** Constructor of the URL based Repository Agent.
     * 
     */
    public TestRepoRepositoryAgent(String url, Locale locale)
        throws MalformedURLException, IOException
    {
        super();
        m_Locale = locale;
        if (!url.endsWith("/"))
            url = url + "/";
        m_RepositoryURL = new URL(url);
        m_RefreshInProgress = false;
        m_Available = false;
        m_LoadQueue = new ArrayList();
        m_Listeners = new ArrayList();
        m_ContainerTypes = new ArrayList();
        
        // TODO some better way to populate the types;
        m_ContainerTypes.add( ResourceInfo.MIMETYPE_FOLDER );
        
        m_LoadingThread = new Thread(this, "PlainURLRepository Loading");
        m_LoadingThread.start();
    }

    /** Checks if the Repository is accessible and operational.
     * 
     * @return true if the Repository is expected to work.
     */
    public boolean isRepositoryAvailable()
    {
        return m_Available;
    }

    public String getReasonNotAvailable()
    {
        return m_Reason;
    }

    /** Returns the Repository Name.
     * 
     * @return The Name of the Repository.
     */
    public String getName()
    {
        return m_Name;
    }

    /** Returns a human-readable description of the Repository.
     * 
     * @return A localized and human-readable description of the Repository.
     */
    public String getDescription()
    {
        return m_Description;
    }

    /** Returns the physical location of the Repository.
     * 
     * @return A URL or other unique identifier of where the repository is located. 
     */
    public String getLocation()
    {
        return m_RepositoryURL.toExternalForm();
    }

    /** Returns the Locale that this RepositoryAgent is initialized to.
     * 
     * @return The Locale that this RepositoryAgent is initialized to.
     */
    public Locale getLocale()
    {
        return m_Locale;
    }

    /** Loads the ResourceInfo from the Repository.
     * 
     * @param resourceIdentification is the identification of the resource. If the parameter
     * is an empty string, the root resource is requested. The identification must be
     * repository relative.
     */
    public void loadResourceInfo(String resourceIdentification)
    {
        synchronized (m_LoadQueue)
        {
            m_LoadQueue.add(resourceIdentification);
        }
    }

    /** Opens the InputStream to the actual resource.
     * 
     * @param resource The ResourceInfo for which to open the InputStream to its actual object.
     * 
     * @return An opened InputStream to the actual resource object. It is expected that the 
     * implementation uses buffered I/O, so clients don't need to optimize access.
     * 
     */
    public InputStream openInputStream(ResourceInfo resource)
    {
        return null;
    }

    /** Updates the RepositoryAgent.
     * 
     * For remote repositories, it is desireable that the RepositoryAgent caches
     * the meta content locally, on file or in-memory. This method explicitly
     * tells the RepositoryAgent to drop the cache. The method should return quickly
     * so if a pre-fetch algorithm is used, it must be done in seperate thread.
     */
    public void refresh()
    {
        synchronized (this)
        {
            m_Available = false;
            m_ResourceIndex = null;
            loadResourceInfo("/");
        }
    }

    public void run()
    {
        try
        {
            while (true)
            {
                String resource = null;
                try
                {
                    resource = fetchResourceToLoad();
                    if (resource != null && !"".equals(resource))
                        loadResource(resource);
                } catch (FileNotFoundException e)
                {
                    fireResourceNotFoundEvent("File not found:" + resource);
                } catch (MalformedURLException e)
                {
                    fireResourceNotFoundEvent("Malformed:" + resource);
                } catch (IOException e)
                {
                    // TODO send unavailable event
                    if (m_Available)
                        fireUnavailableEvent(e.getMessage());
                }
            }
        } catch (InterruptedException e)
        {
            // Do nothing, shutting down...
        }
    }

    private String fetchResourceToLoad()
        throws MalformedURLException, IOException, InterruptedException
    {
        synchronized (m_LoadQueue)
        {
            while (m_LoadQueue.size() == 0)
                m_LoadQueue.wait(100);
            String resource = (String) m_LoadQueue.get(0);
            m_LoadQueue.remove(0);
            System.out.println("Request to load:" + resource);
            return resource;
        }
    }

    private ResourceGroupInfoImpl loadRootResource() throws MalformedURLException, IOException
    {
        synchronized (this)
        {
            if (m_ResourceIndex != null)
                return (ResourceGroupInfoImpl) m_ResourceIndex.get("/");

            Properties p = loadPropertiesFile("members.meta");

            m_Name = p.getProperty("repository.name");
            m_Description = p.getProperty("repository.description");
            Version version = new NonVersion();
            Compliance compliance = new EmptyCompliance();
            String type = ResourceInfo.MIMETYPE_FOLDER;
            ResourceGroupInfoImpl info =
                new ResourceGroupInfoImpl("/", m_Name, m_Description, type, version, compliance, null);
            m_ResourceIndex = new HashMap();
            m_ResourceIndex.put("/", info);
            if (!m_Available)
                fireAvailableEvent(info);
            m_Available = true;
            fireResourceLoadedEvent(info);
            parseMembers(p, info);
            return info;
        }
    }

    private ResourceGroupInfoImpl loadResource(String resource)
        throws MalformedURLException, IOException
    {
        if (resource.equals("/"))
            return loadRootResource();

        // Check cache
        ResourceGroupInfoImpl info = (ResourceGroupInfoImpl) m_ResourceIndex.get(resource);
        if (info == null)
        {
            // Must load parent...
            String parent = getParentIdentification(resource);
            info = loadResource(parent);
            info = (ResourceGroupInfoImpl) m_ResourceIndex.get(resource);
        } else
        {
            if (info.isChildrenLoaded())
                return info;
        }

        if (!resource.endsWith("/"))
            resource = resource + "/";
        if( m_ContainerTypes.contains( info.getType() ))
        {
            Properties p = loadPropertiesFile(resource + "members.meta");
            parseMembers(p, info);
        }
        return info;
    }

    private void parseMembers(Properties p, ResourceGroupInfoImpl group)
    {
        Iterator list = p.entrySet().iterator();
        while (list.hasNext())
        {
            Map.Entry entry = (Map.Entry) list.next();
            String key = (String) entry.getKey();
            if (key.startsWith("member.") && key.endsWith(".identification"))
            {
                parseMember(p, group, key);
            }
        }
    }

    private void parseMember(Properties p, ResourceGroupInfoImpl group, String key)
    {
        int pos = key.indexOf('.', 7);
        String prefix = key.substring(0, pos);
        String ident = p.getProperty(prefix + ".identification");
        if (m_ResourceIndex.containsKey(ident))
            return;
        String name = p.getProperty(prefix + ".name");
        String descr = p.getProperty(prefix + ".description");
        String verString = p.getProperty(prefix + ".version", "1.0");
        String verClass =
            p.getProperty(
                prefix + ".versionclass",
                "org.apache.avalon.repository.tools.common.ConventionalVersion");
        Version version = createVersion(verString, verClass);
        String typeString = p.getProperty(prefix + ".type", ResourceInfo.MIMETYPE_FOLDER);
        String complianceString = p.getProperty(prefix + ".compliance");
        Compliance compliance = createCompliance(complianceString, typeString);
        String parentIdent = group.getIdentification();
        if (parentIdent.equals("/"))
            ident = "/" + ident;
        else
            ident = parentIdent + "/" + ident;
            
        String type = ResourceInfo.MIMETYPE_FOLDER;
        ResourceGroupInfoImpl info =
            new ResourceGroupInfoImpl(ident, name, descr, type, version, compliance, null);
        m_ResourceIndex.put(ident, info);
        group.addMember(info);
        fireResourceLoadedEvent(info);
    }

    private Compliance createCompliance(String usage, String type)
    {
        if (usage == null || "".equals(usage))
            return new EmptyCompliance();
        ComplianceGroupImpl group = new ComplianceGroupImpl();
        StringTokenizer st = new StringTokenizer(usage, " ,", false);
        while (st.hasMoreTokens())
        {
            Compliance c = new GenericCompliance(st.nextToken());
            group.addCompliance(c);
        }
        return group;
    }

    private Version createVersion(String ver, String classname)
    {
        try
        {
            Class cls = getClass().getClassLoader().loadClass(classname);
            Class[] params = new Class[] { String.class };
            Object[] args = new Object[] { ver };
            Constructor cons = cls.getConstructor(params);
            return (Version) cons.newInstance(args);
        } catch (Exception e)
        {
            System.err.println("Warning!! Problem with Version class:" + classname);
            return new NonVersion();
        }
    }

    private String getParentIdentification(String ident)
    {
        int pos = ident.lastIndexOf('/');
        return ident.substring(0, pos + 1);
    }

    /** Adds a RepositoryAgentListener.
     * 
     * <p>
     * If an identical listener already exists, the listener in this call will
     * not be added and no event generated.
     * </p>
     * @param listener The listener to add.
     */
    public void addRepositoryAgentListener(RepositoryAgentListener listener)
    {
        synchronized (this)
        {
            if (m_Listeners.contains(listener))
                return;

            ArrayList v;
            if (m_Listeners == null)
                v = new ArrayList();
            else
                v = (ArrayList) m_Listeners.clone();
            v.add(listener);
            m_Listeners = v;
        }
    }

    /** Removes a RepositoryAgentListener.
     * 
     * <p>
     * If the given listener does not exist, nothing will happen.
     * </p>
     * @param listener The listener to remove.
     */
    public void removeRepositoryAgentListener(RepositoryAgentListener listener)
    {
        synchronized (this)
        {
            if (m_Listeners == null)
                return;
            if (!m_Listeners.contains(listener))
                return;
            ArrayList v = (ArrayList) m_Listeners.clone();
            v.remove(listener);
            m_Listeners = v;
        }
    }

    private Properties loadPropertiesFile(String location)
        throws IOException, MalformedURLException
    {
        while (location.startsWith("/"))
            location = location.substring(1);
        URL url = new URL(m_RepositoryURL, location);
        Properties p = new Properties();
        InputStream in = null;
        try
        {
            in = url.openStream();
            p.load(in);
        } finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {} // Nothing can do...
            }
        }
        return p;
    }

    private void fireResourceLoadedEvent(ResourceInfo resource)
    {
        RepositoryAgentEvent event = new RepositoryAgentEvent(this, resource.getName(), resource);
        Iterator list = m_Listeners.iterator();
        while (list.hasNext())
        {
            try
            {
                RepositoryAgentListener listener = (RepositoryAgentListener) list.next();
                listener.resourceLoaded(event);
            } catch (Exception e)
            {
                // TODO error handling??
                e.printStackTrace();
            }
        }
    }

    private void fireResourceNotFoundEvent(String message)
    {
        RepositoryAgentEvent event = new RepositoryAgentEvent(this, message, null);
        Iterator list = m_Listeners.iterator();
        while (list.hasNext())
        {
            try
            {
                RepositoryAgentListener listener = (RepositoryAgentListener) list.next();
                listener.resourceNotFound(event);
            } catch (Exception e)
            {
                // TODO error handling??
                e.printStackTrace();
            }
        }
    }

    private void fireAvailableEvent(ResourceInfo resource)
    {
        RepositoryAgentEvent event = new RepositoryAgentEvent(this, null, resource);
        Iterator list = m_Listeners.iterator();
        while (list.hasNext())
        {
            try
            {
                RepositoryAgentListener listener = (RepositoryAgentListener) list.next();
                listener.available(event);
            } catch (Exception e)
            {
                // TODO error handling??
                e.printStackTrace();
            }
        }
    }

    private void fireUnavailableEvent(String message)
    {
        RepositoryAgentEvent event = new RepositoryAgentEvent(this, message, null);
        Iterator list = m_Listeners.iterator();
        while (list.hasNext())
        {
            try
            {
                RepositoryAgentListener listener = (RepositoryAgentListener) list.next();
                listener.unavailable(event);
            } catch (Exception e)
            {
                // TODO error handling??
                e.printStackTrace();
            }
        }
    }
}
