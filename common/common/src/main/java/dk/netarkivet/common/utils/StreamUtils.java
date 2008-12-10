/* File:        $Id$
 * Revision:    $Revision$
 * Author:      $Author$
 * Date:        $Date$
 *
 * The Netarchive Suite - Software to harvest and preserve websites
 * Copyright 2004-2007 Det Kongelige Bibliotek and Statsbiblioteket, Denmark
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package dk.netarkivet.common.utils;

import javax.servlet.jsp.JspWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import dk.netarkivet.common.Constants;
import dk.netarkivet.common.exceptions.ArgumentNotValid;
import dk.netarkivet.common.exceptions.IOFailure;

/**
 * Utilities for handling streams.
 */
public class StreamUtils {
    
    /**
     * Will copy everything from input stream to jsp writer, closing input
     * stream afterwards. Charset UTF-8 is assumed.
     *
     * @param in  Inputstream to copy from
     * @param out JspWriter to copy to
     * @throws ArgumentNotValid if either parameter is null
     * @throws IOFailure if a read or write error happens during copy
     */
    public static void copyInputStreamToJspWriter(InputStream in, JspWriter out) {
        ArgumentNotValid.checkNotNull(in, "InputStream in");
        ArgumentNotValid.checkNotNull(out, "JspWriter out");

        byte[] buf = new byte[Constants.IO_BUFFER_SIZE];
        int read = 0;
        try {
            try {
                while ((read = in.read(buf)) != -1) {
                    out.write(new String(buf, "UTF-8"), 0, read);
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new IOFailure("Trouble copying inputstream " + in
                    + " to JspWriter " + out, e);
        }
    }
   
    
    /**
     * Will copy everything from input stream to output stream, closing input
     * stream afterwards.
     *
     * @param in  Inputstream to copy from
     * @param out Outputstream to copy to
     * @throws ArgumentNotValid if either parameter is null
     * @throws IOFailure if a read or write error happens during copy
     */
    public static void copyInputStreamToOutputStream(InputStream in,
                                                     OutputStream out) {
        ArgumentNotValid.checkNotNull(in, "InputStream in");
        ArgumentNotValid.checkNotNull(out, "OutputStream out");

        try {
            try {
                if (in instanceof FileInputStream
                    && out instanceof FileOutputStream) {
                    FileChannel inChannel
                            = ((FileInputStream) in).getChannel();
                    FileChannel outChannel
                            = ((FileOutputStream) out).getChannel();
                    long transferred = 0;
                    final long fileLength = inChannel.size();
                    do {
                        transferred += inChannel.transferTo(
                                transferred,
                                Math.min(Constants.IO_CHUNK_SIZE,
                                         fileLength - transferred),
                                outChannel);
                    } while (transferred < fileLength);
                } else {
                    byte[] buf = new byte[Constants.IO_BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = in.read(buf)) != -1) {
                        out.write(buf, 0, bytesRead);
                    }
                }
                out.flush();
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new IOFailure("Trouble copying inputstream " + in
                                + " to outputstream " + out, e);
        }
    }

    /**
     * Write document tree to stream. Note, the stream is flushed, but not
     * closed.
     *
     * @param doc the document tree to save.
     * @param os the stream to write xml to
     * @throws IOFailure On trouble writing XML to stream.
     */
    public static void writeXmlToStream(Document doc,
                                        OutputStream os) {
        XMLWriter xwriter = null;
        try {
            try {
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setEncoding("UTF-8");
                xwriter = new XMLWriter(os, format);
                xwriter.write(doc);
            } finally {
                if (xwriter != null) {
                    xwriter.close();
                }
                os.flush();
            }
        } catch (IOException e) {
            throw new IOFailure("Unable to write XML to stream", e);
        }
    }
}
