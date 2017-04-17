/*****************************************************************************
 * STLASCIIParser.java
 * Java Source
 *
 * This source is licensed under the GNU LGPL v2.1.
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information.
 *
 * Copyright (c) 2002 Dipl. Ing. P. Szawlowski
 * University of Vienna, Dept. of Medical Computer Sciences
 ****************************************************************************/

package org.andresoviedo.app.model3D.services.stl;

// External imports
import android.content.res.Resources;

import org.andresoviedo.app.util.io.ProgressMonitorInputStream;

import java.io.*;
import java.util.*;

import java.net.URL;
import java.net.URLConnection;

// Internal imports

/**
 * Class to parse STL (stereolithography) files in ASCII format.<p>
 *
 * <p>
 * <b>Internationalisation Resource Names</b>
 * <p>
 * <ul>
 * <li>invalidKeywordMsg: Unknown keyword encountered. </li>
 * <li>emptyFileMsg: File contained the header but no content. </li>
 * <li>invalidDataMsg: Some strange data was encountered. </li>
 * <li>unexpectedEofMsg: We hit an EOF before we were expecting to.</li>
 * </ul>
 *
 * @see STLFileReader
 * @see STLLoader
 * @author  Dipl. Ing. Paul Szawlowski -
 *          University of Vienna, Dept of Medical Computer Sciences
 * @version $Revision: 2.0 $
 */
class STLASCIIParser extends STLParser
{
    /** Error message of a keyword that we don't recognise */
    private static final String UNKNOWN_KEYWORD_MSG_PROP =
        "org.j3d.loaders.stl.STLASCIIParser.invalidKeywordMsg";

    /**
     * Error message when the solid header is found, but there is no
     * geometry after it. Basically an empty file.
     */
    private static final String EMPTY_FILE_MSG_PROP =
        "org.j3d.loaders.stl.STLASCIIParser.emptyFileMsg";

    /** Unexpected data is encountered during parsing */
    private static final String INVALID_NORMAL_DATA_MSG_PROP =
        "org.j3d.loaders.stl.STLASCIIParser.invalidNormalDataMsg";

    /** Unexpected data is encountered during parsing */
    private static final String INVALID_VERTEX_DATA_MSG_PROP =
        "org.j3d.loaders.stl.STLASCIIParser.invalidVertexDataMsg";

    /** Unexpected EOF is encountered during parsing */
    private static final String EOF_WTF_MSG_PROP =
        "org.j3d.loaders.stl.STLASCIIParser.unexpectedEofMsg";

    /** Reader for the main stream */
    private BufferedReader  itsReader;

    /** The line number that we're at in the file */
    private int lineCount;

    /**
     * Create a new default parser instance.
     */
    public STLASCIIParser()
    {
    }


    /**
     * Create a new default parser instance.
     */
    public STLASCIIParser(boolean strict)
    {
        super(strict);

    }

    /**
     * Finish the parsing off now.
     */
    public void close() throws IOException
    {
        if(itsReader != null)
            itsReader.close();
    }

    /**
     * Fetch a single face from the stream
     *
     * @param normal Array length 3 to copy the normals in to
     * @param vertices A [3][3] array for each vertex
     * @throws IllegalArgumentException The file was structurally incorrect
     * @throws IOException Something happened during the reading
     */
    public boolean getNextFacet(double[] normal, double[][] vertices)
        throws IOException
    {
        // format of a triangle is:
        //
        // facet normal number number number
        //   outer loop
        //     vertex number number number
        //     vertex number number number
        //     vertex number number number
        //   end loop
        // endfacet

        // First line with normals
        String input_line = readLine();

        if (input_line == null) {
            return false;
        }

        StringTokenizer strtok = new StringTokenizer(input_line);
        String token = strtok.nextToken();

        // are we the first line of the file? If so, skip it
        if(token.equals("solid"))
        {
            input_line = readLine();
            strtok = new StringTokenizer(input_line);
            token = strtok.nextToken();
            lineCount = 1;
        }

        // Have we reached the end of file?
        // We've encountered a lot of broken files where they use two words
        // "end solid" rather than the spec-required "endsolid".
        if(token.equals("endsolid") || input_line.contains("end solid")) {
            // Skip line and read next
            try {
                return getNextFacet(normal, vertices);
            } catch(IOException ioe) {
                // gone past end of file
                return false;
            }
        }

        if(!token.equals("facet"))
        {
            close();

            I18nManager intl_mgr = I18nManager.getManager();

            String msg = intl_mgr.getString(UNKNOWN_KEYWORD_MSG_PROP) + ": "
                                            + lineCount + " word: " + token;
            throw new IllegalArgumentException(msg);
        }

        token = strtok.nextToken();
        if(!token.equals("normal"))
        {
            close();

            I18nManager intl_mgr = I18nManager.getManager();

            String msg = intl_mgr.getString(UNKNOWN_KEYWORD_MSG_PROP) + ": "
                                            + lineCount;
            throw new IllegalArgumentException(msg);
        }

        readNormal(strtok, normal);

        // Skip the outer loop line
        input_line = readLine();

        if (input_line == null) {
            return false;
        }

        strtok = new StringTokenizer(input_line);
        token = strtok.nextToken();
        lineCount++;

        if(!token.equals("outer"))
        {
            close();

            I18nManager intl_mgr = I18nManager.getManager();

            String msg = intl_mgr.getString(UNKNOWN_KEYWORD_MSG_PROP) + ": "
                                            + lineCount;
            throw new IllegalArgumentException(msg);
        }

        token = strtok.nextToken();
        if(!token.equals("loop"))
        {
            close();

            I18nManager intl_mgr = I18nManager.getManager();

            String msg = intl_mgr.getString(UNKNOWN_KEYWORD_MSG_PROP) + ": "
                                            + lineCount;
            throw new IllegalArgumentException(msg);
        }

        // Next 3x vertex reads
        for(int i = 0; i < 3; i++) {
            input_line = readLine();
            strtok = new StringTokenizer(input_line);
            lineCount++;

            token = strtok.nextToken();

            if(!token.equals("vertex"))
            {
                close();

                I18nManager intl_mgr = I18nManager.getManager();

                String msg = intl_mgr.getString(UNKNOWN_KEYWORD_MSG_PROP) + ": "
                                                + lineCount;
                throw new IllegalArgumentException(msg);
            }

            readCoordinate(strtok, vertices[i]);
        }

        // Read and skip the endloop && endfacet lines

        input_line = readLine();
        if (input_line == null) {
            return false;
        }

        strtok = new StringTokenizer(input_line);
        token = strtok.nextToken();
        lineCount++;

        if(!token.equals("endloop"))
        {
            close();

            I18nManager intl_mgr = I18nManager.getManager();

            String msg = intl_mgr.getString(UNKNOWN_KEYWORD_MSG_PROP) + ": "
                                            + lineCount;
            throw new IllegalArgumentException(msg);
        }

        input_line = readLine();
        if (input_line == null) {
            return false;
        }

        strtok = new StringTokenizer(input_line);
        token = strtok.nextToken();
        lineCount++;

        if(!token.equals("endfacet"))
        {
            close();

            I18nManager intl_mgr = I18nManager.getManager();

            String msg = intl_mgr.getString(UNKNOWN_KEYWORD_MSG_PROP) + ": "
                                            + lineCount;
            throw new IllegalArgumentException(msg);
        }

        return true;
    }

    /**
     * @throws IllegalArgumentException The file was structurally incorrect
     */
    public boolean parse(URL url, Component parentComponent)
        throws InterruptedIOException, IOException
    {
        InputStream stream = null;
        try
        {
            stream = url.openStream();
        }
        catch(IOException e)
        {
            if(stream != null)
                stream.close();

            throw e;
        }

        stream = new ProgressMonitorInputStream(
            parentComponent, "analyzing " + url.toString(), stream);

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(stream));

        boolean isAscii = false;

        try
        {
            isAscii = parse(reader);
        }
        finally
        {
            reader.close();
        }

        if(!isAscii)
            return false;

        try
        {
            stream = url.openStream();
        }
        catch(IOException e)
        {
            stream.close();
            throw e;
        }

        stream = new ProgressMonitorInputStream (
            parentComponent,
            "parsing " + url.toString(),
            stream);

        reader = new BufferedReader(new InputStreamReader(stream));
        itsReader = reader;

        return true;
    }

    /**
     * @throws IllegalArgumentException The file was structurally incorrect
     */
    public boolean parse(URL url)
        throws IOException
    {
        InputStream stream = null;
        try {
            stream = url.openStream();
        }
        catch(IOException e)
        {
            if(stream != null)
                stream.close();

            throw e;
        }

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(stream));
        boolean isAscii = false;

        try
        {
            isAscii = parse(reader);
        }
        catch(InterruptedIOException e)
        {
            // should never happen
            e.printStackTrace();
        }
        finally
        {
            reader.close();
        }

        if(!isAscii)
            return false;

        try
        {
            stream = url.openStream();
        }
        catch(IOException e)
        {
            stream.close();
            throw e;
        }

        reader = new BufferedReader(new InputStreamReader(stream));
        itsReader = reader;

        return true;
    }

    /**
     * Parse the stream now from the given reader.
     *
     * @param reader The reader to source the file from
     * @return true if this is a ASCII format file, false if not
     * @throws IllegalArgumentException The file was structurally incorrect
     * @throws IOException Something happened during the reading
     */
    private boolean parse(BufferedReader reader)
        throws IOException, IllegalArgumentException
    {
        int numOfObjects = 0;
        int numOfFacets = 0;
        ArrayList<Integer> facetsPerObject = new ArrayList<Integer>(10);
        ArrayList<String> names = new ArrayList<String>(10);
        boolean isAscii = true;


        itsReader = reader;
        String line = readLine();
        int line_count = 1;

        line = line.trim();  // "Spec" says whitespace maybe anywhere except within numbers or words.  Great design!

        // check if ASCII format
        if(!line.startsWith("solid"))
        {
            return false;
        }
        else
        {
            if(line.length() > 6)
                names.add(line.substring(6));
            else
                names.add(null);
        }

        line = readLine();

        if(line == null)
        {
            I18nManager intl_mgr = I18nManager.getManager();

            String msg = intl_mgr.getString(EMPTY_FILE_MSG_PROP);
            throw new IllegalArgumentException(msg);
        }

        while(line != null)
        {
            line_count++;

            if(line.indexOf("facet") >= 0)
            {
                numOfFacets ++;
                // skip next 6 lines:
                // outer loop, 3 * vertex, endloop, endfacet
                for(int i = 0; i < 6; i ++)
                {
                    readLine();
                }

                line_count += 6;
            }

            // watch order of if: solid contained also in endsolid
            // JC: We have found a lot of badly formatted STL files generated
            // from some program that incorrectly end a solid object with a
            // space between end and solid. Deal with that here.
            else if((line.indexOf("endsolid") >= 0) ||
                    (line.indexOf("end solid") >= 0))
            {
                facetsPerObject.add(new Integer(numOfFacets));
                numOfFacets = 0;
                numOfObjects++;
            }
            else if(line.indexOf("solid") >= 0)
            {
                line = line.trim();

                if(line.length() > 6)
                    names.add(line.substring(6));
            }
            else
            {
                line = line.trim();
                if(line.length() != 0) {
                    I18nManager intl_mgr = I18nManager.getManager();

                    String msg = intl_mgr.getString(UNKNOWN_KEYWORD_MSG_PROP) +
                                 ": " + lineCount;

                    throw new IllegalArgumentException(msg);
                }
            }

            line = readLine();
        }

        if (numOfFacets > 0 && numOfObjects == 0) {
            numOfObjects = 1;
            facetsPerObject.add(new Integer(numOfFacets));
        }

        itsNumOfObjects = numOfObjects;
        itsNumOfFacets = new int[numOfObjects];
        itsNames = new String[numOfObjects];

        for(int i = 0; i < numOfObjects; i ++)
        {
            Integer num = (Integer)facetsPerObject.get(i);
            itsNumOfFacets[i] = num.intValue();

            itsNames[i] = (String)names.get(i);
        }

        return true;
    }

    /**
     * Read three numbers from the tokeniser and place them in the double value
     * returned.
     */
    private void readNormal(StringTokenizer strtok, double[] vector)
        throws IOException
    {
        boolean error_found = false;

        for(int i = 0; i < 3; i ++)
        {
            String num_str = strtok.nextToken();

            try
            {
                vector[i] = Double.parseDouble(num_str);
            }
            catch(NumberFormatException e)
            {
                if (!strictParsing)
                {
                    error_found = true;
                    continue;
                }

                I18nManager intl_mgr = I18nManager.getManager();

                String msg = intl_mgr.getString(INVALID_NORMAL_DATA_MSG_PROP) +
                    num_str;
                throw new IllegalArgumentException(msg);
            }

        }

        if (error_found) {
            // STL spec says use 0 0 0 for autocalc
            vector[0] = 0;
            vector[1] = 0;
            vector[2] = 0;
        }
    }

    /**
     * Read three numbers from the tokeniser and place them in the double value
     * returned.
     */
    private void readCoordinate(StringTokenizer strtok, double[] vector)
        throws IOException
    {
        for(int i = 0; i < 3; i ++)
        {
            String num_str = strtok.nextToken();

            boolean error_found = false;

            try
            {
                vector[i] = Double.parseDouble(num_str);
            }
            catch(NumberFormatException e)
            {
                if (strictParsing)
                {
                    I18nManager intl_mgr = I18nManager.getManager();

                    String msg = intl_mgr.getString(INVALID_VERTEX_DATA_MSG_PROP) +
                       ": Cannot parse vertex: " + num_str;
                    throw new IllegalArgumentException(msg);
                } else {
                    // Common error is to use commas instead of . in Europe
                    String new_str = num_str.replace(",",".");

                    try
                    {
                        vector[i] = Double.parseDouble(new_str);
                    }
                    catch(NumberFormatException e2)
                    {

                        I18nManager intl_mgr = I18nManager.getManager();

                        String msg = intl_mgr.getString(INVALID_VERTEX_DATA_MSG_PROP) +
                           ": Cannot parse vertex: " + num_str;
                        throw new IllegalArgumentException(msg);
                    }
                }
            }
        }
    }

    /**
     * Read a line from the input.  Ignore whitespace.
     */
    private String readLine() throws IOException {
        String input_line = "";

        while(input_line.length() == 0) {
            input_line = itsReader.readLine();

            if (input_line == null) {
                break;
            }

            if (input_line.length() > 0 && Character.isWhitespace(input_line.charAt(0))) {
                input_line = input_line.trim();
            }
        }

        return input_line;
    }

}