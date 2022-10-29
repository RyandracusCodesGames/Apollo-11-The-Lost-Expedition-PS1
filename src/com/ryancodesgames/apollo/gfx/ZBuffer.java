
package com.ryancodesgames.apollo.gfx;


public class ZBuffer 
{
    private int width;
    private int height;
    private double[] zBuffer;
    
    public ZBuffer(int width, int height)
    {
        this.width = width;
        this.height = height;
        
        zBuffer = new double[width * height];
    }
    
    public void resetBuffer()
    {
        for(int x = 0; x < width * height; x++)
        {
            zBuffer[x] = 0.0;
        }
    }
    
    public void setDepthValue(int x, int y, double value)
    {
        zBuffer[x + y * width] = value;
    }
    
    public double[] getZBuffer()
    {
        return zBuffer;
    }
}
