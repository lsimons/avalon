/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.xdoclet;

import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * This task used to invoke XDoclet.  It has bee deprecated for another taskdef called
 * MetaGernerate.
 *
 * This task patches it calls thru to MetaGenerate to provide legacy support.
 * A warning is issued.
 *
 *
 * @author <a href="mailto:vinay_chandran@users.sourceforge.net">Vinay Chandrasekharan</a>
 * @author Paul Hammant
 * @version $Revision: 1.12 $ $Date: 2002/10/02 19:39:07 $
 * @deprecated
 */
public class PhoenixXDoclet extends Task
{
    private String m_blockInfoSubTask;
    private String m_mxinfoSubTask;
    private ManifestSubTask m_manifestSubTask;
    private Class m_metaGenerateQDoxClass;
    private Object m_metaGenerateQDoxTask;
    private File m_destDir;

    private static boolean WARNING_SENT;


    /**
     * Construct a PhoenixXDoclet.
     */
    public PhoenixXDoclet()
    {
        issueWarningIfAppropriate();

        try
        {
            m_metaGenerateQDoxClass =
                Class.forName("org.apache.avalon.phoenix.tools.metagenerate.MetaGenerateQdoxTask");
            m_metaGenerateQDoxTask = m_metaGenerateQDoxClass.newInstance();
        }
        catch (Exception e)
        {
            throw new BuildException("Some problem Instantiating the MetaGenerate TaskDef."
                + " You will need phoenix-metagenerate.jar and qdox-1.0.jar in the classpath");
        }
    }

    /**
     * Issue warning if appropriate
     */
    private void issueWarningIfAppropriate()
    {
        String[] message = {
        "**************************************************************************************",
        "*                                                                                    *",
        "* WARNING - PhoenixXDoclet Ant Task has been deprecated in favor of Generate-Meta    *",
        "*                                                                                    *",
        "* The new style of the taks is like so...                                            *",
        "*                                                                                    *",
        "*   <generatemeta dest=\"build/metagenerate\">                                         *",
        "*     <fileset dir=\"src/java\">                                                       *",
        "*       <include name=\"**/*.java\"/>                                                  *",
        "*     </fileset>                                                                     *",
        "*   </generatemeta>                                                                  *",
        "*                                                                                    *",
        "* Defining the task like so...                                                       *",
        "*                                                                                    *",
        "*   <taskdef name=\"generatemeta\"                                                     *",
        "*     classname=\"org.apache.avalon.phoenix.tools.metagenerate.MetaGenerateQdoxTask\"> *",
        "*     <classpath refid=\"test.class.path\" />                                          *",
        "*   </taskdef>                                                                       *",
        "*                                                                                    *",
        "* You will NEED phoenix-metagenerate.jar and qdox-1.0.jar in the classpath           *",
        "*                                                                                    *",
        "**************************************************************************************" };

        if (!WARNING_SENT)
        {
            for (int i = 0; i < message.length; i++)
            {
                String s = message[i];
                System.out.println(s);
            }
            WARNING_SENT = true;
        }
    }

    /**
     * Add a file set
     * @param set the fileset
     */
    public void addFileset(FileSet set)
    {
        try
        {
            Method addFileSet =
                    m_metaGenerateQDoxClass.getMethod("addFileset", new Class[] {FileSet.class});
            addFileSet.invoke(m_metaGenerateQDoxTask, new Object[] {set});

        }
        catch (InvocationTargetException ite)
        {
            if (ite.getTargetException() instanceof BuildException)
            {
                throw (BuildException) ite.getTargetException();
            }
            else
            {
                throw new BuildException("Exception during delegation:",ite);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new BuildException("Some problem delegating to MetaGenerate TaskDef: "
                    + e.getMessage());
        }
    }

    /**
     * Set a destination dir
     * @param dir the desination dir
     */
    public void setDestDir(File dir)
    {
        m_destDir = dir;
        try
        {
            Method setDir = m_metaGenerateQDoxClass.getMethod("setDest", new Class[] {File.class});
            setDir.invoke(m_metaGenerateQDoxTask, new Object[] {dir});

        }
        catch (InvocationTargetException ite)
        {
            if (ite.getTargetException() instanceof BuildException)
            {
                throw (BuildException) ite.getTargetException();
            }
            else
            {
                throw new BuildException("Exception during delegation:",ite);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new BuildException("Some problem delegating to MetaGenerate TaskDef: "
                    + e.getMessage());
        }

    }

    public void setVerbose(boolean verbose)
    {
    }

    /**
     * Backwards compatability for the blockinfo sub task
     * @return some dummy string
     */
    public String createBlockinfo()
    {
        m_blockInfoSubTask = "blockInfoSubTask";
        return m_blockInfoSubTask;
    }

    /**
     * Backwards compatability for the manifest sub task
     * @return A ManifestSubTask
     */
    public ManifestSubTask createManifest()
    {
        m_manifestSubTask = new ManifestSubTask();
        return m_manifestSubTask;
    }

    /**
     * Backwards compatability for the mxinfo sub task
     * @return some dummy string
     */
    public String createMxInfo()
    {
        m_mxinfoSubTask = "mxinfoSubTask";
        return m_mxinfoSubTask;
    }

    /**
     * Set the Classpath.  This was not even used in the XDoclet infrastructure.
     * @param path
     */
    public void setClasspathRef( final Path path )
    {
        // for compatibility.
    }

    /**
     * Execute
     * @throws BuildException if a problem
     */
    public void execute() throws BuildException
    {
        String manifest = null;
        if (m_manifestSubTask != null)
        {
            manifest = m_manifestSubTask.getManifestFile();
            ManifestWriter manifestWriter = new ManifestWriter();
            try
            {
                manifestWriter.write(m_destDir, manifest);
            }
            catch (IOException e)
            {
                throw new BuildException("Unable to write Manifest File: " + e.getMessage());
            }
        }

        // TODO create fake manifest.
        // an empty one will do.

        try
        {

            Method execute = m_metaGenerateQDoxClass.getMethod("execute", new Class[] {});
            execute.invoke(m_metaGenerateQDoxTask, new Object[] {});

        }
        catch (InvocationTargetException ite)
        {
            if (ite.getTargetException() instanceof BuildException)
            {
                throw (BuildException) ite.getTargetException();
            }
            else
            {
                throw new BuildException("Exception during delegation:",ite);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new BuildException("Some problem delegating to MetaGenerate TaskDef: "
                    + e.getMessage());
        }
    }

    /**
     * Set task name (Task interface)
     * @param s the task name
     */
    public void setTaskName(String s)
    {
        super.setTaskName(s);
        try
        {
            Method setTaskName = m_metaGenerateQDoxClass.getMethod("setTaskName",
                    new Class[] {String.class});
            setTaskName.invoke(m_metaGenerateQDoxTask, new Object[] {s});
        }
        catch (InvocationTargetException ite)
        {
            if (ite.getTargetException() instanceof BuildException)
            {
                throw (BuildException) ite.getTargetException();
            }
            else
            {
                throw new BuildException("Exception during delegation:",ite);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new BuildException("Some problem delegating to MetaGenerate TaskDef: "
                    + e.getMessage());
        }
    }

    /**
     * Init (Task interface)
     */
    public void init() throws BuildException
    {
        super.init();
        try
        {
            Method init = m_metaGenerateQDoxClass.getMethod("init", new Class[] {});
            init.invoke(m_metaGenerateQDoxTask, new Object[] {});

        }
        catch (InvocationTargetException ite)
        {
            if (ite.getTargetException() instanceof BuildException)
            {
                throw (BuildException) ite.getTargetException();
            }
            else
            {
                throw new BuildException("Exception during delegation:",ite);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new BuildException("Some problem delegating to MetaGenerate TaskDef: "
                    + e.getMessage());
        }

    }

    /**
     * Set up the project (Task interface)
     * @param project the project name
     */
    public void setProject(Project project)
    {
        super.setProject(project);
        try
        {
            Method setProject = m_metaGenerateQDoxClass.getMethod("setProject",
                    new Class[] {Project.class});
            setProject.invoke(m_metaGenerateQDoxTask, new Object[] {project});

        }
        catch (InvocationTargetException ite)
        {
            if (ite.getTargetException() instanceof BuildException)
            {
                throw (BuildException) ite.getTargetException();
            }
            else
            {
                throw new BuildException("Exception during delegation:",ite);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new BuildException("Some problem delegating to MetaGenerate TaskDef: "
                    + e.getMessage());
        }
    }
}
