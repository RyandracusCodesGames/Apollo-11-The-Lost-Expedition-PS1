
package com.ryancodesgames.apollo.gameobject;

import com.ryancodesgames.apollo.mathlib.Mesh;
import com.ryancodesgames.apollo.mathlib.Triangle;
import com.ryancodesgames.apollo.mathlib.Vec3D;
import java.awt.Color;
import java.util.Arrays;

public class Pyramid 
{
    public Mesh meshPyramid;
    
    public Pyramid(double x, double y, double z, float width, double height, double depth)
    {
        meshPyramid = new Mesh(Arrays.asList(new Triangle[]{
        //SOUTH
         new Triangle(new Vec3D(x, y, z), new Vec3D(x + (width/2), y + height, z + (depth/2)), new Vec3D(x + width, y, z)),
        //EAST   
         new Triangle(new Vec3D(x + width, y, z), new Vec3D(x + (width/2), y + height, z + (depth/2)), new Vec3D(x + width, y, z + depth)),
        //NORTH
         new Triangle(new Vec3D(x + width, y, z + depth), new Vec3D(x + (width/2), y + height, z + (depth)/2), new Vec3D(x, y, z + depth)),
        //WEST 
         new Triangle(new Vec3D(x, y, z + depth), new Vec3D(x + (width/2), y + height, z + (depth/2)), new Vec3D(x, y, z)),
        //BOTTOM
         new Triangle(new Vec3D(x + width, y, z + depth), new Vec3D(x, y, z + depth), new Vec3D(x, y, z)),
         new Triangle(new Vec3D(x + width, y, z + depth), new Vec3D(x, y, z), new Vec3D(x + width, y, z))
        }));
    }
}
