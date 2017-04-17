/*****************************************************************************
 * STLParser.java
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
import java.net.URL;
import java.io.IOException;
import java.util.List;

// Local imports

/**
 * Abstract base class for parsing STL (stereolithography) files. Subclasses
 * of this class implement parsing the two formats of STL files: binary and
 * ASCII.<p>
 * @author  Dipl. Ing. Paul Szawlowski -
 *          University of Vienna, Dept of Medical Computer Sciences
 * @version $Revision: 1.3 $
 * Copyright (c) Dipl. Ing. Paul Szawlowski<p>
 */
abstract class STLParser
{
    protected int       itsNumOfObjects = 0;
    protected int[]    itsNumOfFacets = null;
    protected String[] itsNames = null;

    /** Do we strictly parse or try harder */
    protected boolean strictParsing;

    /** Detailed parsing messages or null if none */
    protected List<String> parsingMessages;

    public STLParser()
    {
        this(false);
    }

    /**
     * Constructor.
     *
     * @param strict Attempt to deal with crappy data or short downloads.
     * Will try to return any useable geometry.
     */
    public STLParser(boolean strict)
    {
        strictParsing = strict;
    }

    /**
     * Get array with object names. {@link #parse} must be called once before
     * calling this method.
     * @return Array of strings with names of objects. Size of array = number
     * of objects in file. If name is not contained then the appropriate
     * string is <code>null</code>.
     */
    String[] getObjectNames()
    {
        return itsNames;
    }

    /**
     * Get number of facets per object. {@link #parse} must be called once
     * before calling this method.
     * @return Array with the number of facets per object. Size of array =
     *      number of objects in file.
     */
    int[] getNumOfFacets()
    {
        return itsNumOfFacets;
    }

    /**
     * Get number of objects in file. {@link #parse} must be called once
     * before calling this method.
     */
    int getNumOfObjects()
    {
        return itsNumOfObjects;
    }

    /**
     * Get detailed messages on what was wrong when parsing.  Only can happen
     * when strictParsing is false.  Means things like getNumOfFacets might
     * be larger then reality.
     */
    public List<String> getParsingMessages()
    {
        return parsingMessages;
    }

    /**
     * Releases used resources. Must be called after finishing reading.
     */
    abstract void close() throws IOException;

    /**
     * Parses the file to obtain the number of objects, object names and number
     * of facets per object.
     * @param url URL to read from.
     * @return <code>true</code> if file is in ASCII format, <code>false</code>
     *      otherwise. Use the appropriate subclass for reading.
     */
    abstract boolean parse(URL url)
        throws IOException;

    /**
     * Parses the file to obtain the number of objects, object names and number
     * of facets per object. A progress monitor will show the progress during
     * parsing.
     * @param url URL to read from.
     * @param parentComponent Parent <code>Component</code> of progress monitor.
     *      Use <code>null</code> if there is no parent.
     * @return <code>true</code> if file is in ASCII format, <code>false</code>
     *      otherwise. Use the appropriate subclass for reading.
     */
    abstract boolean parse(URL url, Component parentComponent)
        throws IllegalArgumentException, IOException;

    /**
     * Returns the data for a facet. The orientation of the facets (which way
     * is out and which way is in) is specified redundantly. First, the
     * direction of the normal is outward. Second, the vertices are listed in
     * counterclockwise order when looking at the object from the outside
     * (right-hand rule).<p>
     * Call consecutively until all data is read. Call {@link #close} after
     * finishing reading or if an exception occurs.
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
     */
    abstract boolean getNextFacet(double[] normal, double[][] vertices)
        throws IllegalArgumentException, IOException;
}