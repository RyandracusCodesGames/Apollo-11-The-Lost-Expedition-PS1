
package com.ryancodesgames.apollo.mathlib;


public class Vec2D 
{
    public double u, v, w;
    
    public Vec2D(double u, double v)
    {
        this.u = u;
        this.v = v;
        this.w = 1;
    }
    /*
     CREATE EXACT COPY - CLONE - OF THIS CLASS WITH ALL DATA MEMBERS OF
     ORIGINAL INSTANCE USING JAVA'S CLONABLE INTERFACE.
    */
    @Override
    public Object clone()
    {
        return new Vec2D(u, v);
    }
}
