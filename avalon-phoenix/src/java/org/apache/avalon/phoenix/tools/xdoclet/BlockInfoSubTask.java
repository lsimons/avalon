/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.xdoclet;

import java.io.File;
import java.net.URL;
import xdoclet.TemplateSubTask;
import xdoclet.XDocletException;
import xjavadoc.XClass;

/**
 * Generates BlockInfo 'xinfo' for Blocks
 *
 * @author <a href="mailto:vinay_chandran@users.sourceforge.net">Vinay Chandrasekharan</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2002/04/18 10:26:04 $
 */
public class BlockInfoSubTask
    extends TemplateSubTask
{
    public final static String SUBTASK_NAME = "blockinfo";

    private static final String GENERATED_FILE_NAME = "{0}.xinfo";
    private static final String DEFAULT_TEMPLATE_FILE =
        "/org/apache/avalon/phoenix/tools/xdoclet/blockinfo.j";

    private static String c_classPattern;

    private String m_templatePath;

    public BlockInfoSubTask()
    {
        setTemplateFile( new File( DEFAULT_TEMPLATE_FILE ) );
        setDestinationFile( GENERATED_FILE_NAME );
        setOfType( "org.apache.avalon.phoenix.Block" );

        final TemplateSubTask.ExtentTypes extent = new TemplateSubTask.ExtentTypes();
        extent.setValue( "hierarchy" );
        setExtent( extent );
    }

    public void setTemplatePath( final String templatePath )
    {
        m_templatePath = templatePath;
        setTemplateFile( new File( templatePath ) );
    }

    public static String getClassPattern()
    {
        return c_classPattern;
    }

    public String getSubTaskName()
    {
        return SUBTASK_NAME;
    }

    public void setPattern( final String classPattern )
    {
        c_classPattern = classPattern;
    }

    /**
     * Called to validate configuration parameters.
     */
    public void validateOptions()
        throws XDocletException
    {
        super.validateOptions();

        if( null == m_templatePath )
        {
            throw new XDocletException( "'templatePath' attribute is missing ." );
        }

        final URL template = getTemplateURL();
        if( null == template )
        {
            throw new XDocletException( "'template' is missing." );
        }

        if( null == getClassPattern() || getClassPattern().trim().equals( "" ) )
        {
            throw new XDocletException( "'pattern' parameter missing or empty." );
        }

        if( -1 == getClassPattern().indexOf( "{0}" ) )
        {
            throw new XDocletException( "'pattern' parameter does not have a " +
                                        "'{0}' in it. '{0}' is replaced by Block " +
                                        "name of the class under processing." );
        }
    }

    protected boolean matchesGenerationRules( final XClass clazz )
        throws XDocletException
    {
        if( !super.matchesGenerationRules( clazz ) )
        {
            return false;
        }
        else if( clazz.isAbstract() )
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
