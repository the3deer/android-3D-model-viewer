/*****************************************************************************
 * STLFileReader.java
 * Java Source
 *
 * This source is licensed under the GNU LGPL v2.1.
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information.
 *
 * Copyright (c) 2001, 2002 Dipl. Ing. P. Szawlowski
 * University of Vienna, Dept. of Medical Computer Sciences
 ****************************************************************************/

package org.andresoviedo.app.model3D.services.stl;

// External Imports
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

// Local imports

/**
 * Class to read STL (Stereolithography) files.<p>
 * Usage: First create a <code>STLFileReader</code> object. To obtain the number
 * of objects, name of objects and number of facets for each object use the
 * appropriate methods. Then use the {@link #getNextFacet} method repetitively
 * to obtain the geometric data for each facet. Call {@link #close} to free the
 * resources.<p>
 * In case that the file uses the binary STL format, no check can be done to
 * assure that the file is in STL format. A wrong format will only be
 * recognized if an invalid amount of data is contained in the file.<p>
 *
 * @author  Dipl. Ing. Paul Szawlowski -
 *          University of Vienna, Dept. of Medical Computer Sciences
 * @version $Revision: 1.3 $
 */
public class STLFileReader
{
    private STLParser itsParser;

    /**
     * Creates a <code>STLFileReader</code> object to read a STL file from a
     * file. The data may be in ASCII or binary format.
     * @param file <code>File</code> object of STL file to read.
     * @throws IllegalArgumentException The file was structurally incorrect
     */
    public STLFileReader(File file)
        throws IllegalArgumentException, IOException
    {
        this(file.toURL());
    }

    /**
     * Creates a <code>STLFileReader</code> object to read a STL file from a
     * file. The data may be in ASCII or binary format.
     * @param fileName Name of STL file to read.
     * @throws IllegalArgumentException The file was structurally incorrect
     */
    public STLFileReader(String fileName)
        throws IllegalArgumentException, IOException
    {
        this(new URL(fileName));
    }

    /**
     * Creates a <code>STLFileReader</code> object to read a STL file from a
     * file. The data may be in ASCII or binary format.
     * @param fileName Name of STL file to read.
     * @param strict Attempt to deal with crappy data or short downloads.
     * Will try to return any useable geometry.
     * @throws IllegalArgumentException The file was structurally incorrect
     */
    public STLFileReader(String fileName, boolean strict)
        throws IllegalArgumentException, IOException
    {
        this(new URL(fileName), strict);
    }

    /**
     * Creates a <code>STLFileReader</code> object to read a STL file from an
     * URL. The data may be in ASCII or binary format.
     * @param url URL of STL file to read.
     * @throws IllegalArgumentException The file was structurally incorrect
     */
    public STLFileReader(URL url)
        throws IllegalArgumentException, IOException
    {
        final STLASCIIParser asciiParser = new STLASCIIParser();

        if(asciiParser.parse(url))
        {
            itsParser = asciiParser;
        }
        else
        {
            final STLBinaryParser binParser = new STLBinaryParser();
            binParser.parse(url);
            itsParser = binParser;
        }
    }

    /**
     * Creates a <code>STLFileReader</code> object to read a STL file from an
     * URL. The data may be in ASCII or binary format.
     * @param url URL of STL file to read.
     * @param strict Attempt to deal with crappy data or short downloads.
     * Will try to return any useable geometry.
     * @throws IllegalArgumentException The file was structurally incorrect
     */
    public STLFileReader(URL url, boolean strict)
        throws IllegalArgumentException, IOException
    {

        final STLParser asciiParser = new STLASCIIParser(strict);

        if(asciiParser.parse(url))
        {
            itsParser = asciiParser;
        }
        else
        {
            final STLBinaryParser binParser = new STLBinaryParser(strict);
            binParser.parse(url);
            itsParser = binParser;
        }
    }


    /**
     * Creates a <code>STLFileReader</code> object to read a STL file from an
     * URL. The data may be in ASCII or binary format. A progress monitor will
     * show the progress during reading.
     * @param url URL of STL file to read.
     * @param parentComponent Parent <code>Component</code> of progress monitor.
     *      Use <code>null</code> if there is no parent.
     * @throws IllegalArgumentException The file was structurally incorrect
     */
    public STLFileReader(URL url, Component parentComponent)
        throws IllegalArgumentException, IOException
    {
        final STLASCIIParser asciiParser = new STLASCIIParser();
        if(asciiParser.parse(url, parentComponent))
        {
            itsParser = asciiParser;
        }
        else
        {
            final STLBinaryParser binParser = new STLBinaryParser();
            binParser.parse(url, parentComponent);
            itsParser = binParser;
        }
    }

    /**
     * Creates a <code>STLFileReader</code> object to read a STL file from an
     * URL. The data may be in ASCII or binary format. A progress monitor will
     * show the progress during reading.
     * @param url URL of STL file to read.
     * @param parentComponent Parent <code>Component</code> of progress monitor.
     *      Use <code>null</code> if there is no parent.
     * @param strict Attempt to deal with crappy data or short downloads.
     * Will try to return any useable geometry.
     * @throws IllegalArgumentException The file was structurally incorrect
     */
    public STLFileReader(URL url, Component parentComponent, boolean strict)
        throws IllegalArgumentException, IOException
    {
        final STLASCIIParser asciiParser = new STLASCIIParser(strict);
        if(asciiParser.parse(url, parentComponent))
        {
            itsParser = asciiParser;
        }
        else
        {
            final STLBinaryParser binParser = new STLBinaryParser(strict);
            binParser.parse(url, parentComponent);
            itsParser = binParser;
        }
    }

    /**
     * Creates a <code>STLFileReader</code> object to read a STL file from a
     * file. The data may be in ASCII or binary format. A progress monitor will
     * show the progress during reading.
     * @param file <code>File</code> object of STL file to read.
     * @param parentComponent Parent <code>Component</code> of progress monitor.
     *      Use <code>null</code> if there is no parent.
     * @throws IllegalArgumentException The file was structurally incorrect
     */
    public STLFileReader(File file, Component parentComponent)
        throws IllegalArgumentException, IOException
    {
        this(file.toURL(), parentComponent);
    }

    /**
     * Creates a <code>STLFileReader</code> object to read a STL file from a
     * file. The data may be in ASCII or binary format. A progress monitor will
     * show the progress during reading.
     * @param file <code>File</code> object of STL file to read.
     * @param parentComponent Parent <code>Component</code> of progress monitor.
     *      Use <code>null</code> if there is no parent.
     * @param strict Attempt to deal with crappy data or short downloads.
     * Will try to return any useable geometry.
     * @throws IllegalArgumentException The file was structurally incorrect
     */
    public STLFileReader(File file, Component parentComponent, boolean strict)
        throws IllegalArgumentException, IOException
    {
        this(file.toURL(), parentComponent, strict);
    }

    /**
     * Creates a <code>STLFileReader</code> object to read a STL file from a
     * file. The data may be in ASCII or binary format. A progress monitor will
     * show the progress during reading.
     * @param fileName Name of STL file to read.
     * @param parentComponent Parent <code>Component</code> of progress monitor.
     *      Use <code>null</code> if there is no parent.
     * @throws IllegalArgumentException The file was structurally incorrect
     */
    public STLFileReader (String fileName, Component parentComponent)
        throws IllegalArgumentException, IOException
    {
        this(new URL(fileName), parentComponent);
    }

    /**
     * Creates a <code>STLFileReader</code> object to read a STL file from a
     * file. The data may be in ASCII or binary format. A progress monitor will
     * show the progress during reading.
     * @param fileName Name of STL file to read.
     * @param parentComponent Parent <code>Component</code> of progress monitor.
     *      Use <code>null</code> if there is no parent.
     * @param strict Attempt to deal with crappy data or short downloads.
     * Will try to return any useable geometry.
     * @throws IllegalArgumentException The file was structurally incorrect
     */
    public STLFileReader (String fileName, Component parentComponent, boolean strict)
        throws IllegalArgumentException, IOException
    {
        this(new URL(fileName), parentComponent, strict);
    }

    /**
     * Returns the data for a facet. The orientation of the facets (which way
     * is out and which way is in) is specified redundantly. First, the
     * direction of the normal is outward. Second, the vertices are listed in
     * counterclockwise order when looking at the object from the outside
     * (right-hand rule).<p>
     * Call consecutively until all data is read.
     * @param normal array of size 3 to store the normal vector.
     * @param vertices array of size 3x3 to store the vertex data.
     *      <UL type=disk>
     *          <LI>first index: vertex
     *          <LI>second index:
     *          <UL>
     *              <LI>0: x coordinate
     *              <LI>1: y coordinate
     *              <LI>2: z coordinate
     *          </UL>
     *      </UL>
     * @return <code>True</code> if facet data is contained in
     *      <code>normal</code> and <code>vertices</code>. <code>False</code>
     *      if end of file is reached. Further calls of this method after
     *      the end of file is reached will lead to an IOException.
     * @throws IllegalArgumentException The file was structurally incorrect
     */
    public boolean getNextFacet(double[ ] normal, double[ ][ ] vertices)
        throws IllegalArgumentException, IOException
    {
        return itsParser.getNextFacet(normal, vertices);
    }

    /**
     * Get array with object names.
     * @return Array of strings with names of objects. Size of array = number
     * of objects in file. If name is not contained then the appropriate
     * string is <code>null</code>.
     */
    public String[] getObjectNames()
    {
        return itsParser.getObjectNames();
    }

    /**
     * Get number of facets per object.
     * @return Array with the number of facets per object. Size of array =
     *      number of objects in file.
     */
    public int[] getNumOfFacets()
    {
        return itsParser.getNumOfFacets();
    }

    /**
     * Get detailed messages on what was wrong when parsing.  Only can happen
     * when strictParsing is false.  Means things like getNumOfFacets might
     * be larger then reality.
     */
    public List<String> getParsingMessages()
    {
        return itsParser.getParsingMessages();
    }

    /**
     * Get number of objects in file.
     */
    public int getNumOfObjects()
    {
        return itsParser.getNumOfObjects();
    }

    /**
     * Releases used resources. Must be called after finishing reading.
     */
    public void close() throws IOException
    {
        if(itsParser != null)
        {
            itsParser.close();
        }
    }
}