/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
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

package de.javagl.jgltf.model;

/**
 * Some common OpenGL constants that are used in glTF
 */
public class GltfConstants
{
    /**
     * The GL_BGR constant (32992)
     */
    public static final int GL_BGR = 32992;
    
    /**
     * The GL_RGB constant (6407)
     */
    public static final int GL_RGB = 6407;
    
    /**
     * The GL_RGBA constant (6408)
     */
    public static final int GL_RGBA = 6408;
    
    /**
     * The GL_BGRA constant (32993)
     */
    public static final int GL_BGRA = 32993;
    
    
    
    /**
     * The GL_BYTE constant (5120)
     */
    public static final int GL_BYTE = 5120;
    
    /**
     * The GL_UNSIGNED_BYTE constant (5121)
     */
    public static final int GL_UNSIGNED_BYTE = 5121;

    /**
     * The GL_SHORT constant (5122)
     */
    public static final int GL_SHORT = 5122;
    
    /**
     * The GL_UNSIGNED_SHORT constant (5123)
     */
    public static final int GL_UNSIGNED_SHORT = 5123;
    
    /**
     * The GL_INT constant (5124)
     */
    public static final int GL_INT = 5124;

    /**
     * The GL_UNSIGNED_INT constant (5125)
     */
    public static final int GL_UNSIGNED_INT = 5125;
    
    /**
     * The GL_FLOAT constant (5126)
     */
    public static final int GL_FLOAT = 5126;
    

    
    /**
     * The GL_FLOAT_VEC2 constant (35664)
     */
    public static final int GL_FLOAT_VEC2 = 35664;

    /**
     * The GL_FLOAT_VEC3 constant (35665)
     */
    public static final int GL_FLOAT_VEC3 = 35665;

    /**
     * The GL_FLOAT_VEC4 constant (35666)
     */
    public static final int GL_FLOAT_VEC4 = 35666;

    /**
     * The GL_INT_VEC2 constant (35667)
     */
    public static final int GL_INT_VEC2 = 35667;

    /**
     * The GL_INT_VEC3 constant (35668)
     */
    public static final int GL_INT_VEC3 = 35668;

    /**
     * The GL_INT_VEC4 constant (35669)
     */
    public static final int GL_INT_VEC4 = 35669;

    /**
     * The GL_BOOL constant (35670)
     */
    public static final int GL_BOOL = 35670;

    /**
     * The GL_BOOL_VEC2 constant (35671)
     */
    public static final int GL_BOOL_VEC2 = 35671;

    /**
     * The GL_BOOL_VEC3 constant (35672)
     */
    public static final int GL_BOOL_VEC3 = 35672;

    /**
     * The GL_BOOL_VEC4 constant (35673)
     */
    public static final int GL_BOOL_VEC4 = 35673;

    /**
     * The GL_FLOAT_MAT2 constant (35674)
     */
    public static final int GL_FLOAT_MAT2 = 35674;

    /**
     * The GL_FLOAT_MAT3 constant (35675)
     */
    public static final int GL_FLOAT_MAT3 = 35675;

    /**
     * The GL_FLOAT_MAT4 constant (35676)
     */
    public static final int GL_FLOAT_MAT4 = 35676;

    /**
     * The GL_SAMPLER_2D constant (35678)
     */
    public static final int GL_SAMPLER_2D = 35678;    
    
    /**
     * The GL_SAMPLER_CUBE constant (35680)
     */
    public static final int GL_SAMPLER_CUBE = 35680;
    
    
    /**
     * The GL_POINTS constant (0)
     */
    public static final int GL_POINTS = 0;

    /**
     * The GL_LINES constant (1)
     */
    public static final int GL_LINES = 1;

    /**
     * The GL_LINE_LOOP constant (2)
     */
    public static final int GL_LINE_LOOP = 2;

    /**
     * The GL_LINE_STRIP constant (3)
     */
    public static final int GL_LINE_STRIP = 3;

    /**
     * The GL_TRIANGLES constant (4)
     */
    public static final int GL_TRIANGLES = 4;    

    /**
     * The GL_TRIANGLE_STRIP constant (5)
     */
    public static final int GL_TRIANGLE_STRIP = 5;

    /**
     * The GL_TRIANGLE_FAN constant (6)
     */
    public static final int GL_TRIANGLE_FAN = 6;
    
    
    
    /**
     * The GL_VERTEX_SHADER constant (35633)
     */
    public static final int GL_VERTEX_SHADER = 35633; 
    
    /**
     * The GL_VERTEX_SHADER constant (35632)
     */
    public static final int GL_FRAGMENT_SHADER = 35632; 
    
    
    
    /**
     * The GL_TEXTURE_2D constant (3553)
     */
    public static final int GL_TEXTURE_2D = 3553;
    
    
    
    /**
     * The GL_ARRAY_BUFFER constant (34962)
     */
    public static final int GL_ARRAY_BUFFER =  34962;
    
    /**
     * The GL_ELEMENT_ARRAY_BUFFER constant (34963)
     */
    public static final int GL_ELEMENT_ARRAY_BUFFER = 34963;
    
    
    // glEnable for technique.states
    
    /**
     * The GL_BLEND constant (3042)
     */
    public static final int GL_BLEND = 3042;
    
    /**
     * The GL_CULL_FACE constant (2884)
     */
    public static final int GL_CULL_FACE = 2884;
    
    /**
     * The GL_DEPTH_TEST constant (2929)
     */
    public static final int GL_DEPTH_TEST = 2929;
    
    /**
     * The GL_POLYGON_OFFSET_FILL constant (32823)
     */
    public static final int GL_POLYGON_OFFSET_FILL = 32823;
    
    /**
     * The GL_SAMPLE_ALPHA_TO_COVERAGE constant (32926)
     */
    public static final int GL_SAMPLE_ALPHA_TO_COVERAGE = 32926;
    
    /**
     * The GL_SCISSOR_TEST constant (3089)
     */
    public static final int GL_SCISSOR_TEST = 3089;

    // glBlendEquationSeparate
    
    /**
     * The GL_FUNC_ADD constant (32774)
     */
    public static final int GL_FUNC_ADD = 32774;
    
    /**
     * The GL_FUNC_SUBTRACT constant (32778)
     */
    public static final int GL_FUNC_SUBTRACT = 32778;
    
    /**
     * The GL_FUNC_REVERSE_SUBTRACT constant (32779)
     */
    public static final int GL_FUNC_REVERSE_SUBTRACT = 32779;

    // glBlendFuncSeparate
    
    /**
     * The GL_ZERO constant (0)
     */
    public static final int GL_ZERO = 0;
    
    /**
     * The GL_ONE constant (1)
     */
    public static final int GL_ONE = 1;
    
    /**
     * The GL_SRC_COLOR constant (768)
     */
    public static final int GL_SRC_COLOR = 768;
    
    /**
     * The GL_ONE_MINUS_SRC_COLOR constant (769)
     */
    public static final int GL_ONE_MINUS_SRC_COLOR = 769;
    
    /**
     * The GL_DST_COLOR constant (774)
     */
    public static final int GL_DST_COLOR = 774;
    
    /**
     * The GL_ONE_MINUS_DST_COLOR constant (775)
     */
    public static final int GL_ONE_MINUS_DST_COLOR = 775;
    
    /**
     * The GL_SRC_ALPHA constant (770)
     */
    public static final int GL_SRC_ALPHA = 770;
    
    /**
     * The GL_ONE_MINUS_SRC_ALPHA constant (771)
     */
    public static final int GL_ONE_MINUS_SRC_ALPHA = 771;
    
    /**
     * The GL_DST_ALPHA constant (772)
     */
    public static final int GL_DST_ALPHA = 772;
    
    /**
     * The GL_ONE_MINUS_DST_ALPHA constant (773)
     */
    public static final int GL_ONE_MINUS_DST_ALPHA = 773;
    
    /**
     * The GL_CONSTANT_COLOR constant (32769)
     */
    public static final int GL_CONSTANT_COLOR = 32769;
    
    /**
     * The GL_ONE_MINUS_CONSTANT_COLOR constant (32770)
     */
    public static final int GL_ONE_MINUS_CONSTANT_COLOR = 32770;
    
    /**
     * The GL_CONSTANT_ALPHA constant (32771)
     */
    public static final int GL_CONSTANT_ALPHA = 32771;
    
    /**
     * The GL_ONE_MINUS_CONSTANT_ALPHA constant (32772)
     */
    public static final int GL_ONE_MINUS_CONSTANT_ALPHA = 32772;
    
    /**
     * The GL_SRC_ALPHA_SATURATE constant (776)
     */
    public static final int GL_SRC_ALPHA_SATURATE = 776;

    // glCullFace
    
    /**
     * The GL_FRONT constant (1028)
     */
    public static final int GL_FRONT = 1028;
    
    /**
     * The GL_BACK constant (1029)
     */
    public static final int GL_BACK = 1029;
    
    /**
     * The GL_FRONT_AND_BACK constant (1032)
     */
    public static final int GL_FRONT_AND_BACK = 1032;

    // glDepthFunc
    
    /**
     * The GL_NEVER constant (512)
     */
    public static final int GL_NEVER = 512;
    
    /**
     * The GL_LESS constant (513)
     */
    public static final int GL_LESS = 513;
    
    /**
     * The GL_LEQUAL constant (515)
     */
    public static final int GL_LEQUAL = 515;
    
    /**
     * The GL_EQUAL constant (514)
     */
    public static final int GL_EQUAL = 514;
    
    /**
     * The GL_GREATER constant (516)
     */
    public static final int GL_GREATER = 516;
    
    /**
     * The GL_NOTEQUAL constant (517)
     */
    public static final int GL_NOTEQUAL = 517;
    
    /**
     * The GL_GEQUAL constant (518)
     */
    public static final int GL_GEQUAL = 518;
    
    /**
     * The GL_ALWAYS constant (519)
     */
    public static final int GL_ALWAYS = 519;

    // glFrontFace
    
    /**
     * The GL_CW constant (2304)
     */
    public static final int GL_CW = 2304;
    
    /**
     * The GL_CCW constant (2305)
     */
    public static final int GL_CCW = 2305;    
    
    
    // glTexParameter
    
    /**
     * The GL_NEAREST constant (9728)
     */
    public static final int GL_NEAREST = 9728;

    /**
     * The GL_LINEAR constant (9729)
     */
    public static final int GL_LINEAR = 9729;

    /**
     * The GL_NEAREST_MIPMAP_NEAREST constant (9984)
     */
    public static final int GL_NEAREST_MIPMAP_NEAREST = 9984;

    /**
     * The GL_LINEAR_MIPMAP_NEAREST constant (9985)
     */
    public static final int GL_LINEAR_MIPMAP_NEAREST = 9985;

    /**
     * The GL_NEAREST_MIPMAP_LINEAR constant (9986)
     */
    public static final int GL_NEAREST_MIPMAP_LINEAR = 9986;

    /**
     * The GL_LINEAR_MIPMAP_LINEAR constant (9987)
     */
    public static final int GL_LINEAR_MIPMAP_LINEAR = 9987;

    // glSamplerParameter 
    
    /**
     * The GL_REPEAT constant (10497)
     */
    public static final int GL_REPEAT = 10497;

    /**
     * The GL_MIRRORED_REPEAT constant (33648)
     */
    public static final int GL_MIRRORED_REPEAT = 33648;

    /**
     * The GL_CLAMP_TO_EDGE constant (33071)
     */
    public static final int GL_CLAMP_TO_EDGE = 33071;

    /**
     * The GL_CLAMP_TO_BORDER constant (33069)
     */
    public static final int GL_CLAMP_TO_BORDER = 33069;

    
    
    /**
     * Returns the String representation of the given constant
     * 
     * @param constant The constant
     * @return The String for the constant
     */
    public static String stringFor(int constant)
    {
        switch (constant)
        {
            case GL_BGR : return "GL_BGR";
            case GL_RGB : return "GL_RGB";
            case GL_RGBA : return "GL_RGBA";
            case GL_BGRA : return "GL_BGRA";

            case GL_BYTE : return "GL_BYTE";
            case GL_UNSIGNED_BYTE : return "GL_UNSIGNED_BYTE";
            case GL_SHORT : return "GL_SHORT";
            case GL_UNSIGNED_SHORT : return "GL_UNSIGNED_SHORT";
            case GL_INT : return "GL_INT";
            case GL_UNSIGNED_INT : return "GL_UNSIGNED_INT";
            case GL_FLOAT : return "GL_FLOAT";
            
            case GL_FLOAT_VEC2 : return "GL_FLOAT_VEC2";
            case GL_FLOAT_VEC3 : return "GL_FLOAT_VEC3";
            case GL_FLOAT_VEC4 : return "GL_FLOAT_VEC4";
            case GL_INT_VEC2 : return "GL_INT_VEC2";
            case GL_INT_VEC3 : return "GL_INT_VEC3";
            case GL_INT_VEC4 : return "GL_INT_VEC4";
            case GL_BOOL : return "GL_BOOL";
            case GL_BOOL_VEC2 : return "GL_BOOL_VEC2";
            case GL_BOOL_VEC3 : return "GL_BOOL_VEC3";
            case GL_BOOL_VEC4 : return "GL_BOOL_VEC4";
            case GL_FLOAT_MAT2 : return "GL_FLOAT_MAT2";
            case GL_FLOAT_MAT3 : return "GL_FLOAT_MAT3";
            case GL_FLOAT_MAT4 : return "GL_FLOAT_MAT4";
            case GL_SAMPLER_2D : return "GL_SAMPLER_2D";
            
            case GL_POINTS: return "GL_ZERO or GL_POINTS";
            case GL_LINES: return "GL_ONE or GL_LINES";
            case GL_LINE_LOOP: return "GL_LINE_LOOP";
            case GL_LINE_STRIP: return "GL_LINE_STRIP";
            case GL_TRIANGLES: return "GL_TRIANGLES";
            case GL_TRIANGLE_STRIP: return "GL_TRIANGLE_STRIP";

            case GL_VERTEX_SHADER: return "GL_VERTEX_SHADER";
            case GL_FRAGMENT_SHADER: return "GL_FRAGMENT_SHADER";

            case GL_TEXTURE_2D: return "GL_TEXTURE_2D";

            case GL_ARRAY_BUFFER: return "GL_ARRAY_BUFFER";
            case GL_ELEMENT_ARRAY_BUFFER: return "GL_ELEMENT_ARRAY_BUFFER";
            
            // glEnable for technique.states
            case GL_BLEND: return "GL_BLEND";
            case GL_CULL_FACE: return "GL_CULL_FACE";
            case GL_DEPTH_TEST: return "GL_DEPTH_TEST";
            case GL_POLYGON_OFFSET_FILL: return "GL_POLYGON_OFFSET_FILL";
            case GL_SAMPLE_ALPHA_TO_COVERAGE: 
                return "GL_SAMPLE_ALPHA_TO_COVERAGE";
            case GL_SCISSOR_TEST: return "GL_SCISSOR_TEST";

            // glBlendEquationSeparate
            case GL_FUNC_ADD: return "GL_FUNC_ADD";
            case GL_FUNC_SUBTRACT: return "GL_FUNC_SUBTRACT";
            case GL_FUNC_REVERSE_SUBTRACT: return "GL_FUNC_REVERSE_SUBTRACT";

            // glBlendFuncSeparate
            //case GL_ZERO: return "GL_ZERO"; // see GL_POINTS
            //case GL_ONE: return "GL_ONE"; // see GL_LINES
            case GL_SRC_COLOR: return "GL_SRC_COLOR";
            case GL_ONE_MINUS_SRC_COLOR: return "GL_ONE_MINUS_SRC_COLOR";
            case GL_DST_COLOR: return "GL_DST_COLOR";
            case GL_ONE_MINUS_DST_COLOR: return "GL_ONE_MINUS_DST_COLOR";
            case GL_SRC_ALPHA: return "GL_SRC_ALPHA";
            case GL_ONE_MINUS_SRC_ALPHA: return "GL_ONE_MINUS_SRC_ALPHA";
            case GL_DST_ALPHA: return "GL_DST_ALPHA";
            case GL_ONE_MINUS_DST_ALPHA: return "GL_ONE_MINUS_DST_ALPHA";
            case GL_CONSTANT_COLOR: return "GL_CONSTANT_COLOR";
            case GL_ONE_MINUS_CONSTANT_COLOR: 
                return "GL_ONE_MINUS_CONSTANT_COLOR";
            case GL_CONSTANT_ALPHA: return "GL_CONSTANT_ALPHA";
            case GL_ONE_MINUS_CONSTANT_ALPHA: 
                return "GL_ONE_MINUS_CONSTANT_ALPHA";
            case GL_SRC_ALPHA_SATURATE: return "GL_SRC_ALPHA_SATURATE";

            // glCullFace
            case GL_FRONT: return "GL_FRONT";
            case GL_BACK: return "GL_BACK";
            case GL_FRONT_AND_BACK: return "GL_FRONT_AND_BACK";

            // glDepthFunc
            case GL_NEVER: return "GL_NEVER";
            case GL_LESS: return "GL_LESS";
            case GL_LEQUAL: return "GL_LEQUAL";
            case GL_EQUAL: return "GL_EQUAL";
            case GL_GREATER: return "GL_GREATER";
            case GL_NOTEQUAL: return "GL_NOTEQUAL";
            case GL_GEQUAL: return "GL_GEQUAL";
            case GL_ALWAYS: return "GL_ALWAYS";

            // glFrontFace
            case GL_CW: return "GL_CW";
            case GL_CCW: return "GL_CCW";
            
            // glTexParameter
            case GL_NEAREST : return "GL_NEAREST";
            case GL_LINEAR : return "GL_LINEAR";
            case GL_NEAREST_MIPMAP_NEAREST : return "GL_NEAREST_MIPMAP_NEAREST";
            case GL_LINEAR_MIPMAP_NEAREST : return "GL_LINEAR_MIPMAP_NEAREST";
            case GL_NEAREST_MIPMAP_LINEAR : return "GL_NEAREST_MIPMAP_LINEAR";
            case GL_LINEAR_MIPMAP_LINEAR : return "GL_LINEAR_MIPMAP_LINEAR";
            
            // glSamplerParameter
            case GL_REPEAT : return "GL_REPEAT";
            case GL_MIRRORED_REPEAT : return "GL_MIRRORED_REPEAT";
            case GL_CLAMP_TO_EDGE : return "GL_CLAMP_TO_EDGE";
            case GL_CLAMP_TO_BORDER : return "GL_CLAMP_TO_BORDER";
            
            default:
                return "UNKNOWN_GL_CONSTANT["+constant+"]";
        }
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private GltfConstants()
    {
        // Private constructor to prevent instantiation
    }
    
    
}
