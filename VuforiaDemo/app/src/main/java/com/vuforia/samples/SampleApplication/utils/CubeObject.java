/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.vuforia.samples.SampleApplication.utils;

import java.nio.Buffer;


public class CubeObject extends MeshObject
{
    // Data for drawing the 3D plane as overlay
    private static final double cubeVertices[]  = { 
            -1.00f, -1.00f, 1.00f, // front
            1.00f, -1.00f, 1.00f, 
            1.00f, 1.00f, 1.00f,
            -1.00f, 1.00f, 1.00f,
                
            -1.00f, -1.00f, -1.00f, // back
            1.00f, -1.00f, -1.00f,
            1.00f, 1.00f, -1.00f,
            -1.00f, 1.00f, -1.00f,
            
            -1.00f, -1.00f, -1.00f, // left
            -1.00f, -1.00f, 1.00f,
            -1.00f, 1.00f, 1.00f,
            -1.00f, 1.00f, -1.00f,
            
            1.00f, -1.00f, -1.00f, // right
            1.00f, -1.00f, 1.00f,
            1.00f, 1.00f, 1.00f,
            1.00f, 1.00f, -1.00f,
            
            -1.00f, 1.00f, 1.00f, // top
            1.00f, 1.00f, 1.00f,
            1.00f, 1.00f, -1.00f,
            -1.00f, 1.00f, -1.00f,
            
            -1.00f, -1.00f, 1.00f, // bottom
            1.00f, -1.00f, 1.00f,
            1.00f, -1.00f, -1.00f,
            -1.00f, -1.00f, -1.00f };
    
    
    private static final double cubeTexcoords[] = { 
            0, 0, 1, 0, 1, 1, 0, 1,
                                                
            1, 0, 0, 0, 0, 1, 1, 1,
                                                
            0, 0, 1, 0, 1, 1, 0, 1,
                                                
            1, 0, 0, 0, 0, 1, 1, 1,
                                                
            0, 0, 1, 0, 1, 1, 0, 1,
                                                
            1, 0, 0, 0, 0, 1, 1, 1 };
    
    
    private static final double cubeNormals[]   = { 
            0, 0, 1,  0, 0, 1,  0, 0, 1,  0, 0, 1,
            
            0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1,
            
            -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0,
            
            1, 0, 0,  1, 0, 0,  1, 0, 0,  1, 0, 0,
            
            0, 1, 0,  0, 1, 0,  0, 1, 0,  0, 1, 0,
            
            0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0,
            };
    
    private static final short  cubeIndices[]   = { 
            0, 1, 2, 0, 2, 3, // front
            4, 6, 5, 4, 7, 6, // back
            8, 9, 10, 8, 10, 11, // left
            12, 14, 13, 12, 15, 14, // right
            16, 17, 18, 16, 18, 19, // top
            20, 22, 21, 20, 23, 22  // bottom
                                                };
    
    private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;
    private Buffer mIndBuff;
    
    
    public CubeObject()
    {
        mVertBuff = fillBuffer(cubeVertices);
        mTexCoordBuff = fillBuffer(cubeTexcoords);
        mNormBuff = fillBuffer(cubeNormals);
        mIndBuff = fillBuffer(cubeIndices);
    }
    
    
    @Override
    public Buffer getBuffer(BUFFER_TYPE bufferType)
    {
        Buffer result = null;
        switch (bufferType)
        {
            case BUFFER_TYPE_VERTEX:
                result = mVertBuff;
                break;
            case BUFFER_TYPE_TEXTURE_COORD:
                result = mTexCoordBuff;
                break;
            case BUFFER_TYPE_INDICES:
                result = mIndBuff;
                break;
            case BUFFER_TYPE_NORMALS:
                result = mNormBuff;
            default:
                break;
        }
        return result;
    }
    
    
    @Override
    public int getNumObjectVertex()
    {
        return cubeVertices.length / 3;
    }
    
    
    @Override
    public int getNumObjectIndex()
    {
        return cubeIndices.length;
    }
}
