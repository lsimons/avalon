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
package org.apache.metro.studio.eclipse.core.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.metro.studio.eclipse.core.MetroStudioCore;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public class SystemTool
{

    /**
	 *  
	 */
    public SystemTool()
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
            MetroStudioCore.log(e, "Can't find file \"" + in.getName() + "\".");
            return;
        }

        /*
		 * If the output file alrady exists and the -f option was not
		 * specified,
		 */

        File file = out;
        if (file.exists() && force == false)
        {
            MetroStudioCore.log(null, "Output file exists.  Use the -f option to replace it.");
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
            MetroStudioCore.log(
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
            MetroStudioCore.log(e, "");
        } catch (IOException e)
        {
            MetroStudioCore.log(e, "");
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
