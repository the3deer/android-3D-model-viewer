/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2017 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.jgltf.model.io;

/**
 * Utility methods related to version strings
 */
public class VersionUtils
{
    /**
     * Compare the given semantic version numbers.
     * 
     * @param v0 The first version number
     * @param v1 The second version number
     * @return A value that is smaller than, equal to or greater than 0,
     * indicating whether the first version is smaller than, equal to
     * or greater than the second version.
     */
    public static int compareVersions(String v0, String v1)
    {
        int[] sv0 = computeMajorMinorPatch(v0);
        int[] sv1 = computeMajorMinorPatch(v1);
        for (int i = 0; i < 3; i++)
        {
            int c = Integer.compare(sv0[i], sv1[i]);
            if (c != 0)
            {
                return c;
            }
        }
        return 0;
    }
    
    /**
     * Compute the semantic version numbers from the given string. The
     * string is assumed to be a semantic version number, consisting
     * of integer values <i>major.minor.patch</i>. The <i>patch</i> part
     * is allowed to have a non-numeric suffix, which will be ignored
     * here. Any element of this pattern that is not present or cannot
     * be parsed will be assumed to be 0.
     * 
     * @param v The version string
     * @return An array containing 3 values: The major, minor and patch version
     */
    static int[] computeMajorMinorPatch(String v)
    {
        int result[] = new int[3];
        String tokens[] = v.split("\\.");
        int n = Math.min(tokens.length, 3);
        for (int i = 0; i < n; i++)
        {
            String token = tokens[i];
            result[i] = parseIntPrefix(token);
        }
        return result;
    }
    
    /**
     * Try to parse an integer value from the start of the given string.
     * For example, for the string <code>"23a-beta"</code>, this will
     * return <code>23</code>. If no valid number can be parsed, then
     * 0 is returned.
     * 
     * @param s The input string
     * @return The integer 
     */
    private static int parseIntPrefix(String s)
    {
        String number = "";
        for (int j = 0; j < s.length(); j++)
        {
            char c = s.charAt(j);
            if (Character.isDigit(c))
            {
                number += c;
            }
            else
            {
                break;
            }
        }
        try
        {
            return Integer.parseInt(number);
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }
    
    
//    public static void main(String[] args)
//    {
//        String s = "1.2.0";
//        String s1 = "1.12.1c-beta";
//        String s2 = "1";
//        String s3 = "1.2";
//        System.out.println(compareVersions(s, s1));
//        System.out.println(compareVersions(s, s2));
//        System.out.println(compareVersions(s, s3));
//    }
}
