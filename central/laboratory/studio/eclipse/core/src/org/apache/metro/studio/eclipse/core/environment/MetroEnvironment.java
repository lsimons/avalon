/*

 Copyright 2004. The Apache Software Foundation.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License. 

 */
package org.apache.metro.studio.eclipse.core.environment;

import java.io.IOException;
import java.util.Properties;

import org.apache.avalon.util.defaults.DefaultsBuilder;
import org.apache.metro.studio.eclipse.core.MetroStudioCore;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Metro Development Team </a>
 *         19.08.2004 last change:
 *  
 */
public class MetroEnvironment
{
    private DefaultsBuilder merlinBuilder;

    private DefaultsBuilder avalonBuilder;

    /**
     *  
     */
    public MetroEnvironment()
    {
        super();

        try
        {
            merlinBuilder = new DefaultsBuilder("merlin", null);
            avalonBuilder = new DefaultsBuilder("avalon", null);
        } catch (Exception e)
        {
            MetroStudioCore
                    .log(e, "Error while reading the Avalon environment");
        }
    }

    /**
     * @return
     */
    public String getMerlinHome()
    {
        String path;
        try
        {
            Properties prop = merlinBuilder.getUserProperties();
            prop = merlinBuilder.getDirProperties();
            prop = merlinBuilder.getHomeProperties();

            path = merlinBuilder.getHomeDirectory().getCanonicalPath();
        } catch (IOException e)
        {
            MetroStudioCore.log(e,
                    "Error while reading the Merlin Home Directory");
            return null;
        }
        return path;
    }
}