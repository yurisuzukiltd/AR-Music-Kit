/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.vuforia.samples.SampleApplication.utils;

public class LineShaders
{
    
    public static final String LINE_VERTEX_SHADER = " \n"
        + "attribute vec4 vertexPosition; \n"
        + "uniform mat4 modelViewProjectionMatrix; \n" + " \n"
        + "void main() \n" + "{ \n"
        + "   gl_Position = modelViewProjectionMatrix * vertexPosition; \n"
        + "} \n";
    
    public static final String LINE_FRAGMENT_SHADER = " \n" + " \n"
        + "precision mediump float; \n" + "uniform float opacity; \n"
        + "uniform vec3 color; \n" + " \n" + "void main() \n" + "{ \n"
        + "   gl_FragColor = vec4(color.r, color.g, color.b, opacity); \n"
        + "} \n";
    
}
