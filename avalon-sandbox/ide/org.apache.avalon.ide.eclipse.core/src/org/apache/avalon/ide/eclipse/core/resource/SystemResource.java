/*
 * ============================================================================
 * The Apache Software License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgment: "This product includes software developed by the Apache
 * Software Foundation (http://www.apache.org/)." Alternately, this
 * acknowledgment may appear in the software itself, if and wherever such
 * third-party acknowledgments normally appear. 4. The names "Jakarta", "Apache
 * Avalon", "Avalon Framework" and "Apache Software Foundation" must not be
 * used to endorse or promote products derived from this software without prior
 * written permission. For written permission, please contact
 * apache@apache.org. 5. Products derived from this software may not be called
 * "Apache", nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/> .
 */
package org.apache.avalon.ide.eclipse.core.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.avalon.ide.eclipse.merlin.core.MerlinDeveloperCore;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public class SystemResource
{

    /**
	 *  
	 */
    public SystemResource()
    {
        super();
    }
    /*
	 * java 1.4 version to retain 1.3.1 compatibility (WSAD!) dont use it now
	 * 
	 * public static void copyFile(File in, File out) throws Exception {
	 * FileChannel sourceChannel = new FileInputStream(in).getChannel();
	 * FileChannel destinationChannel = new FileOutputStream(out).getChannel();
	 * sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
	 * sourceChannel.close(); destinationChannel.close(); }
	 */
    /*
	 * This method makes a copy of a file.
	 * 
	 * java CopyFile source.dat copy.dat
	 * 
	 * This command will fail if a file named copy.dat already exists. To force
	 * the command to succede, add the -f command line option:
	 * 
	 * java CopyFile -f source.dat copy.dat
	 * 
	 * Either command will fail if the source file does not exist.
	 */

    public static void copyFile(File in, File out) throws Exception
    {
        InputStream source; // Stream for reading from the source file.
        OutputStream copy; // Stream for writing the copy.
        boolean force = true; // This is set to true if the "-f" option is
							  // specified.
        int byteCount; // The number of bytes copied from the source file.

        /* Create the input stream. If an error occurs, end the program. */

        try
        {
            source = new FileInputStream(in);
        } catch (FileNotFoundException e)
        {
            MerlinDeveloperCore.log(e, "Can't find file \"" + in.getName() + "\".");
            return;
        }

        /*
		 * If the output file alrady exists and the -f option was not
		 * specified,
		 */

        File file = out;
        if (file.exists() && force == false)
        {
            MerlinDeveloperCore.log(null, "Output file exists.  Use the -f option to replace it.");
            return;
        }

        /* Create the output stream. If an error occurs, end the program. */

        try
        {
            copy = new FileOutputStream(out);
        } catch (IOException e)
        {
            System.out.println("Can't open output file \"" + out.getName() + "\".");
            return;
        }

        /*
		 * Copy one byte at a time from the input stream to the out put stream,
		 * ending when the read() method returns -1 (which is the signal that
		 * the end of the stream has been reached. If any error occurs, print
		 * an error message. Also print a message if the file has bee copied
		 */

        byteCount = 0;

        try
        {
            while (true)
            {
                int data = source.read();
                if (data < 0)
                    break;
                copy.write(data);
                byteCount++;
            }
            source.close();
            copy.close();

        } catch (Exception e)
        {
            MerlinDeveloperCore.log(
                e,
                "Error occured while copying.  " + byteCount + " bytes copied.");
        }

    } // end copyFile()

    public static String getFileContents(String fileName)
    {

        StringBuffer buf = new StringBuffer();
        try
        {
            FileInputStream in = new FileInputStream(fileName);
            InputStreamReader file = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(file);

            String line;
            while ((line = reader.readLine()) != null)
            {
                buf.append(line);
            }
            file.close();
        } catch (FileNotFoundException e)
        {
            MerlinDeveloperCore.log(e, "");
        } catch (IOException e)
        {
            MerlinDeveloperCore.log(e, "");
        }
        return buf.toString();
    }
    /**
     * @param pLine
     * @param pKey
     * @param pString
     * @return
     */
    public static String replaceAll(String source, String pKey, String pReplacement)
    {
        int start = 0;
        int next;
        StringBuffer in = new StringBuffer(source);
        StringBuffer out = new StringBuffer();
        while((next = in.indexOf(pKey, start)) != -1){
            out.append(source.substring(start, next));
            out.append(pReplacement);
            start = next + pKey.length();
        };
        out.append(source.substring(start, source.length()));
        
        return out.toString();
    }
}
