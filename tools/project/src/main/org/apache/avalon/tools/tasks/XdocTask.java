/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.tools.tasks;

import java.io.File;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.XSLTProcess;

public class XdocTask extends HomeTask
{
    public static final String XDOC_TARGET_SRC_KEY = "avalon.target.src.xdoc";
    public static final String XDOC_TARGET_SRC_VALUE = 
       PrepareTask.TARGET_SRC + "/xdocs";

    public static final String XDOC_TARGET_DOCS_KEY = "avalon.target.docs";
    public static final String XDOC_TARGET_DOCS_VALUE = 
       PrepareTask.TARGET + "/docs";

    public static final String XDOC_THEME_KEY = "xdoc.theme.name";
    public static final String XDOC_THEME_VALUE = "avalon2";

    public static final String XDOC_OUTPUT_FORMAT_KEY = "xdoc.output.format";
    public static final String XDOC_OUTPUT_FORMAT_VALUE = "html";

    public void init()
    {
        super.init();
        setProjectProperty( XDOC_TARGET_SRC_KEY, XDOC_TARGET_SRC_VALUE );
        setProjectProperty( XDOC_TARGET_DOCS_KEY, XDOC_TARGET_DOCS_VALUE );
        setProjectProperty( XDOC_THEME_KEY, XDOC_THEME_VALUE );
        setProjectProperty( XDOC_OUTPUT_FORMAT_KEY, XDOC_OUTPUT_FORMAT_VALUE );
    }

    private File getThemesDirectory()
    {
        return new File( getHome().getHomeDirectory(), "themes" );
    }

    private File getTargetSrcXdocDirectory()
    {
        File basedir = getProject().getBaseDir();
        return new File( basedir, getProject().getProperty( XDOC_TARGET_SRC_KEY ) );
    }

    private File getTargetDocsDirectory()
    {
        File basedir = getProject().getBaseDir();
        return new File( basedir, getProject().getProperty( XDOC_TARGET_DOCS_KEY ) );
    }
    
    private File getTargetBuildXdocDirectory()
    {
        File target = PrepareTask.getTargetDirectory( getProject() );
        return new File( target, "temp/xdoc-build" );
    }
    
    private String getTheme()
    {
        return getProject().getProperty( XDOC_THEME_KEY );
    }

    private String getOutputFormat()
    {
        return getProject().getProperty( XDOC_OUTPUT_FORMAT_KEY );
    }

    public void execute()
    {        
        File srcDir = getTargetSrcXdocDirectory();
        if( !srcDir.exists() ) return;

        File destDir = getTargetBuildXdocDirectory();
        createDirectory( destDir );

        String theme = getTheme();
        String output = getOutputFormat();
        File themeRoot = getThemesDirectory();
        File themeDir = new File( themeRoot, theme + "/" + output );
        
        log( "   Source: " + srcDir.getAbsolutePath() );
        log( "     Dest: " + destDir.getAbsolutePath() );
        log( "Theme Dir: " + themeDir );
        
        try
        {
            transformNavigation( themeDir, srcDir, destDir );
            copySources( srcDir, destDir );
            transformXdocs( themeDir, destDir );
            copyResources( themeDir );
        } 
        catch( Throwable e )
        {
            log( "XSLT execution failed: " + e.getMessage() );
            throw new BuildException( e );
        }
    }
    
    private void transformNavigation( File themeDir, File source, File dest )
    {
        File xslFile = new File( themeDir,  "nav-aggregate.xsl" );
        transform( source, dest, xslFile, "**/navigation.xml", "", ".xml", "xml" );
    }
    
    private void copySources( File source, File dest )
    {
        copy( source, dest, "**/*", "**/navigation.xml" );
    }
    
    private void transformXdocs( File themeDir, File build )
    {
        File xslFile = new File( themeDir,  "transform.xsl" );
        String output = getOutputFormat();
        File docs = getTargetDocsDirectory();
        transform( build, docs, xslFile, "**/*.xml", "**/navigation.xml", "." + output, "html" );
    }
    
    private void copyResources( File themeDir )
    {
        File destDir = getTargetDocsDirectory();
        File toDir = new File( destDir, "resources" );
        File fromDir = new File( themeDir, "resources" );
        copy( fromDir, toDir, "**/*", "" );
    }
    
    private void copy( File fromDir, File toDir, String includes, String excludes )
    {
        FileSet from = new FileSet();
        from.setDir( fromDir );
        from.setIncludes( includes );
        from.setExcludes( excludes + ",**/_svn/**" );
        toDir.mkdirs();  /* ensure that the directory exists. */
        
        Copy copy = (Copy) getProject().createTask( "copy" );
        copy.setTodir( toDir );
        copy.addFileset( from );
        copy.execute();
    }
    
    private void transform( File srcDir, File toDir, File xslFile, 
                            String includes, String excludes, String extension,
                            String method )
    {
        XSLTProcess xslt = (XSLTProcess) getProject().createTask( "xslt" );
        xslt.setDestdir( toDir );
        xslt.setStyle( xslFile.getAbsolutePath() );
        xslt.setBasedir( srcDir );
        xslt.setIncludes( includes );
        xslt.setForce( true );
        xslt.setExtension( extension );
        xslt.setExcludes( excludes );
        
        XSLTProcess.OutputProperty prop1 = xslt.createOutputProperty();
        prop1.setName( "method" );
        prop1.setValue( method );
        
        XSLTProcess.OutputProperty prop2 = xslt.createOutputProperty();
        prop2.setName( "standalone" );
        prop2.setValue( "yes" );
        
        XSLTProcess.OutputProperty prop3 = xslt.createOutputProperty();
        prop3.setName( "encoding" );
        prop3.setValue( "iso8859_1" );
        
        XSLTProcess.OutputProperty prop4 = xslt.createOutputProperty();
        prop4.setName( "indent" );
        prop4.setValue( "yes" );
          
        xslt.init();
        xslt.execute();
    }
} 
