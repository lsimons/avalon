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

    private File m_BaseToDir;    
    private File m_BaseSrcDir;    
    private String m_theme;

    public void setTheme( String theme )
    {
        m_theme = theme;
    }

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
        if( m_theme != null ) return m_theme;
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
        
        log( "Source: " + srcDir.getAbsolutePath() );
        log( "Theme: " + themeDir );
        
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
        log( "Transforming navigation." );
        transformTrax( 
          source, dest, xslFile, 
          "^.*/navigation.xml$", "", ".xml", "xml" );
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
        log( "Transforming content." );
        transformTrax( 
          build, docs, xslFile, 
          "^.*\\.xml$", "^.*/navigation.xml$", "." + output, "html" );
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
        from.setExcludes( excludes );
        toDir.mkdirs();  /* ensure that the directory exists. */
        
        Copy copy = (Copy) getProject().createTask( "copy" );
        copy.setTodir( toDir );
        copy.addFileset( from );
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
                String svnRoot = getProject().getProperty( "xdoc.svn.root.xdocs" );
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
                transformer.setParameter( 
                  "copyright", getProject().getProperty( "xdoc.footer.copyright"  ) );
                transformer.setParameter( "svn-location", svnSource );
                
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
}