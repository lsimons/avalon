package org.apache.avalon.tools.tasks;

import java.io.File;
import java.io.FileFilter;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.XSLTProcess;
import org.apache.tools.ant.taskdefs.Mkdir;

import org.apache.avalon.tools.home.Context;
import org.apache.avalon.tools.home.Home;

public class XdocTask extends Task
{
    //public static final String BUILD_DOCS_KEY = "project.target.docs.name";
    //public static final String BUILD_DOCS_VALUE = "docs";

    public static final String XDOC_TEMP_KEY = "project.target.temp.xdocs";
    public static final String XDOC_TEMP_VALUE = "xdocs";

    public static final String XDOC_SRC_KEY = "project.xdocs.src";
    public static final String XDOC_SRC_VALUE = "xdocs";

    public static final String XDOC_RESOURCES_KEY = "project.xdocs.resources";
    public static final String XDOC_RESOURCES_VALUE = "resources";

    public static final String XDOC_THEME_KEY = "project.xdoc.theme";
    public static final String XDOC_THEME_VALUE = "avalon2";

    public static final String XDOC_FORMAT_KEY = "project.xdoc.output.format";
    public static final String XDOC_FORMAT_VALUE = "html";

    public static final String ORG_NAME_KEY = "project.organization.name";
    public static final String ORG_NAME_VALUE = "The Apache Software Foundation";

    public static final String XDOC_LOGO_RIGHT_FILE_KEY = "project.xdoc.logo.right.file";
    public static final String XDOC_LOGO_RIGHT_FILE_VALUE = "";

    public static final String XDOC_LOGO_RIGHT_URL_KEY = "project.xdoc.logo.right.url";
    public static final String XDOC_LOGO_RIGHT_URL_VALUE = "";

    public static final String XDOC_LOGO_LEFT_FILE_KEY = "project.xdoc.logo.left.file";
    public static final String XDOC_LOGO_LEFT_FILE_VALUE = "";

    public static final String XDOC_LOGO_LEFT_URL_KEY = "project.xdoc.logo.left.url";
    public static final String XDOC_LOGO_LEFT_URL_VALUE = "";

    public static final String XDOC_LOGO_MIDDLE_FILE_KEY = "project.xdoc.logo.middle.file";
    public static final String XDOC_LOGO_MIDDLE_FILE_VALUE = "";

    public static final String XDOC_LOGO_MIDDLE_URL_KEY = "project.xdoc.logo.middle.url";
    public static final String XDOC_LOGO_MIDDLE_URL_VALUE = "";

    public static final String XDOC_BRAND_NAME_KEY = "project.xdoc.brand.name";
    public static final String XDOC_BRAND_NAME_VALUE = "Avalon";

    public static final String XDOC_ANCHOR_URL_KEY = "project.xdoc.anchor.url";


    private boolean m_init = false;
    private Context m_context;
    private Home m_home;
    private String m_theme;

    private File m_BaseToDir;    
    private File m_BaseSrcDir;    

    public void setTheme( String theme )
    {
        m_theme = theme;
    }

   /**
    * Set the home ref id.
    * @param id a home id
    */
    public void setRefid( String id )
    {
        Object object = getProject().getReference( id );
        if( null == object )
        {
            final String error = 
              "Unknown ref id '" + id + "'.";
            throw new BuildException( error );
        }
        if( object instanceof Home )
        {
            m_home = (Home) object;
        }
        else
        {
            final String error = 
              "Supplied id '" + id + "' does not refer to a Home.";
            throw new BuildException( error );
        }
    }

    public void init() throws BuildException 
    {
        if( !m_init )
        {
            Project project = getProject();
            m_context = Context.getContext( project );
            project.setNewProperty( ORG_NAME_KEY, ORG_NAME_VALUE );
            project.setNewProperty( XDOC_SRC_KEY, XDOC_SRC_VALUE );
            project.setNewProperty( XDOC_RESOURCES_KEY, XDOC_RESOURCES_VALUE );
            project.setNewProperty( XDOC_THEME_KEY, XDOC_THEME_VALUE );
            project.setNewProperty( XDOC_FORMAT_KEY, XDOC_FORMAT_VALUE );
            project.setNewProperty( XDOC_TEMP_KEY, XDOC_TEMP_VALUE );
            project.setNewProperty( XDOC_LOGO_RIGHT_FILE_KEY, XDOC_LOGO_RIGHT_FILE_VALUE );
            project.setNewProperty( XDOC_LOGO_RIGHT_URL_KEY, XDOC_LOGO_RIGHT_URL_VALUE );
            project.setNewProperty( XDOC_LOGO_LEFT_FILE_KEY, XDOC_LOGO_LEFT_FILE_VALUE );
            project.setNewProperty( XDOC_LOGO_LEFT_URL_KEY, XDOC_LOGO_LEFT_URL_VALUE );
            project.setNewProperty( XDOC_LOGO_MIDDLE_FILE_KEY, XDOC_LOGO_MIDDLE_FILE_VALUE );
            project.setNewProperty( XDOC_LOGO_MIDDLE_URL_KEY, XDOC_LOGO_MIDDLE_URL_VALUE );
            project.setNewProperty( XDOC_BRAND_NAME_KEY, XDOC_BRAND_NAME_VALUE );

            m_init = true;
        }
    }

    private File getThemesDirectory()
    {
        File home = m_home.getHomeDirectory();
        return new File( home, "themes" );
    }
    
    private String getOutputFormat()
    {
        return getProject().getProperty( XDOC_FORMAT_KEY );
    }

    private String getTheme()
    {
        if( m_theme != null ) return m_theme;
        return getProject().getProperty( XDOC_THEME_KEY );
    }

    public void execute()
    {        
        log( "Executing" );

        if( null == m_home ) 
        {
            final String error = 
              "Required system home 'refid' attribute not set in the task definition ["
              + getTaskName() + "].";
            throw new BuildException( error );
        }

        Project project = getProject();
        File docs = m_context.getDocsDirectory();
        log( "Docs:" + docs );

        //
        // get the directory containing the filtered xdocs source files 
        // (normally target/src/xdocs)
        //

        File build = m_context.getBuildPath( PrepareTask.BUILD_SRC_KEY, false );

        if( null == build )
        {
            final String message =
              "Src directory does not exist: "
              + build;
            log( message );
            return;
        }

        String xdocsPath = project.getProperty( XDOC_SRC_KEY );
        if( null == xdocsPath )
        {
            final String message =
              "Cannot processed as xdoc src directory not defined.";
            log( message );
            return;
        }

        File srcDir = new File( build, xdocsPath );
        if( !srcDir.exists() ) return;
        log( "Filtered source: " + srcDir.getAbsolutePath() );

        //
        // create the temporary directory into which we generate the 
        // navigation structure (normally target/temp/xdocs)
        //

        File temp = m_context.getTempDirectory();
        String tempPath = project.getProperty( XDOC_TEMP_KEY );
        File destDir = new File( temp, tempPath );
        mkDir( destDir );

        //
        // get the theme, output formats, etc.
        //

        log( "Destination: " + docs.getAbsolutePath() );
        mkDir( docs );

        String theme = getTheme();
        String output = getOutputFormat();
        File themeRoot = getThemesDirectory();
        File themeDir = new File( themeRoot, theme + "/" + output );
        
        String resourcesPath = project.getProperty( XDOC_RESOURCES_KEY );
        File resources = new File( build, resourcesPath );

        log( "Year: " + getProject().getProperty( "magic.year" ) );
        log( "Theme: " + themeDir );
        
        //
        // initiate the transformation starting with the generation of 
        // the navigation structure based on the src directory content
        // into the temporary destingation directory, copy the content
        // sources to to the temp directory, transform the content and 
        // generated navigation in the temp dir using the selected them
        // into the final docs directory, and copy over resources to 
        // the final docs directory
        //

        try
        {
            transformNavigation( themeDir, srcDir, destDir );
            copySources( srcDir, destDir );
            transformXdocs( themeDir, destDir, docs );
            copyThemeResources( themeDir, docs );
            copySrcResources( resources, docs );
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
        log( "Transforming navigation." );
        transformTrax( 
          source, dest, xslFile, 
          "^.*/navigation.xml$", "", ".xml", "xml" );
    }
    
    private void copySources( File source, File dest )
    {
        copy( source, dest, "**/*", "**/navigation.xml" );
    }
    
    private void transformXdocs( File themeDir, File build, File docs )
    {
        File xslFile = new File( themeDir,  "transform.xsl" );
        String output = getOutputFormat();
        log( "Transforming content." );
        transformTrax( 
          build, docs, xslFile, 
          "^.*\\.xml$", "^.*/navigation.xml$", "." + output, "html" );
    }
    
    private void copySrcResources( File resources, File docs )
    {
        copy( resources, docs, "**/*", "" );
    }

    private void copyThemeResources( File themeDir, File docs )
    {
        File fromDir = new File( themeDir, "resources" );
        copy( fromDir, docs, "**/*", "" );
    }
    
    private void copy( File fromDir, File toDir, String includes, String excludes )
    {
        FileSet from = new FileSet();
        from.setDir( fromDir );
        from.setIncludes( includes );
        from.setExcludes( excludes );

        mkDir( toDir );
        
        Copy copy = (Copy) getProject().createTask( "copy" );
        copy.setTodir( toDir );
        copy.addFileset( from );
        copy.setPreserveLastModified( true );
        copy.execute();
    }
    

    private void transformTrax( File srcDir, File toDir, File xslFile, 
                            String includes, String excludes, String extension,
                            String method )
        throws BuildException
    {
        try
        {
            TransformerFactory factory = TransformerFactory.newInstance();
            StreamSource xsl = new StreamSource( xslFile );
            Transformer transformer = factory.newTransformer( xsl );

            RegexpFilter filter = new RegexpFilter( includes, excludes );

            m_BaseToDir = toDir;
            m_BaseSrcDir = srcDir.getAbsoluteFile();
            transform( transformer, m_BaseSrcDir, toDir, filter, extension );
        } 
        catch( TransformerException e )
        {
            throw new BuildException( e.getMessage(), e );
        }
    }

    private void transform( Transformer transformer, File srcDir, File toDir,
        FileFilter filter, String extension )
        throws BuildException
    {
        String year = getProject().getProperty( "magic.year" );
        String org = getProject().getProperty( ORG_NAME_KEY );
        String copyright = 
          "Copyright " + year + ", " + org + " All rights reserved.";


        File[] content = srcDir.listFiles( filter );
        for( int i = 0 ; i < content.length ; i++ )
        {
            String base = content[i].getName();
            if( content[i].isDirectory() )
            {
                File newDest = new File( toDir, base );
                newDest.mkdirs();
                transform( transformer, content[i], newDest, filter, extension );
            }
            if( content[i].isFile() )
            {
                String svnRoot = getProject().getProperty( XDOC_ANCHOR_URL_KEY );
                String svnSource = svnRoot + getRelSrcPath( srcDir ) + "/" + base;
                
                int pos = base.lastIndexOf( '.' );
                if( pos > 0 )
                    base = base.substring( 0, pos );
                base = base + extension;
                
                File newDest = new File( toDir, base );
                StreamSource xml = new StreamSource( content[i] );
                StreamResult out = new StreamResult( newDest );
                transformer.clearParameters();
                
                transformer.setParameter( "directory", getRelToPath( toDir ) );
                transformer.setParameter( "fullpath", getRelToPath( newDest ) );
                transformer.setParameter( "file", base );
                transformer.setParameter( "svn-location", svnSource );

                transformer.setParameter( "copyright", copyright );
                transformer.setParameter( 
                  "logoright_file", 
                  getProject().getProperty( XDOC_LOGO_RIGHT_FILE_KEY ).trim() );
                transformer.setParameter( 
                  "logoright_url", 
                  getProject().getProperty( XDOC_LOGO_RIGHT_URL_KEY).trim() );
                transformer.setParameter( 
                  "logoleft_file", 
                  getProject().getProperty( XDOC_LOGO_LEFT_FILE_KEY ).trim() );
                transformer.setParameter( 
                  "logoleft_url", 
                  getProject().getProperty( XDOC_LOGO_LEFT_URL_KEY ).trim() );
                transformer.setParameter( 
                  "logomiddle_file", 
                  getProject().getProperty( XDOC_LOGO_MIDDLE_FILE_KEY ).trim() );
                transformer.setParameter( 
                  "logomiddle_url", 
                  getProject().getProperty( XDOC_LOGO_MIDDLE_URL_KEY ).trim() );
                transformer.setParameter( 
                  "brand_name", 
                  getProject().getProperty( XDOC_BRAND_NAME_KEY ).trim() );

                try
                {
                    transformer.transform( xml, out );
                } 
                catch( Exception e )
                {
                    log( "ERROR: " + getRelToPath( newDest ) + " : " + e.getMessage() );
                    throw new BuildException( 
                        "Unable to transform document." );
                }
            }
        }
    }

    private String getRelToPath( File dir )
    {
        String basedir = m_BaseToDir.getAbsolutePath();
        String curdir = dir.getAbsolutePath();
        return curdir.substring( basedir.length() );
    }

    private String getRelSrcPath( File dir )
    {
        String basedir = m_BaseSrcDir.getAbsolutePath();
        String curdir = dir.getAbsolutePath();
        return curdir.substring( basedir.length() );
    }


    public class RegexpFilter
        implements FileFilter
    {
        private Pattern m_Includes;
        private Pattern m_Excludes;
        
        public RegexpFilter( String includes, String excludes )
        {
            m_Includes = Pattern.compile( includes );
            m_Excludes = Pattern.compile( excludes );
        }
        
        public boolean accept( File file )
        {
            String basename = file.getName();
        
            if( basename.equals( ".svn" ) )
                return false;
        
            if( basename.equals( "CVS" ) )
                return false;
        
            if( file.isDirectory() )
                return true;

            String fullpath = file.getAbsolutePath().replace( '\\', '/' );

            Matcher m = m_Includes.matcher( fullpath );
            if( ! m.matches() )
                return false;
        
            m = m_Excludes.matcher( fullpath );
            return ! m.matches() ;
        }
    } 

    private void mkDir( File dir )
    {
        Mkdir mkdir = (Mkdir) getProject().createTask( "mkdir" );
        mkdir.setDir( dir );
        mkdir.init();
        mkdir.execute();
    }
}