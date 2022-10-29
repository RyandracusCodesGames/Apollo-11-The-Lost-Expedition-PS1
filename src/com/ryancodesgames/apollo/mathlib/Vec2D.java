
package com.ryancodesgames.apollo.mathlib;


public class Vec2D 
{
    public double u, v;
    
    public Vec2D(double u, double v)
    {
        this.u = u;
        this.v = v;
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
