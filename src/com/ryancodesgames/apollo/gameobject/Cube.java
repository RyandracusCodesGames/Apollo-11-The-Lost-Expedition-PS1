
package com.ryancodesgames.apollo.gameobject;

import com.ryancodesgames.apollo.mathlib.Mesh;
import com.ryancodesgames.apollo.mathlib.Triangle;
import com.ryancodesgames.apollo.mathlib.Vec2D;
import com.ryancodesgames.apollo.mathlib.Vec3D;
import java.util.Arrays;


public class Cube extends GameObject
{
    public Vec3D origin;
    
    public Cube(Vec3D origin, Mesh meshCube, double x, double y, double z, double width, double height, double depth)
    {
                
        super(origin, meshCube);
        
        this.origin = origin;
        
        meshCube = new Mesh(Arrays.asList(new Triangle[]{
        //SOUTH
        new Triangle(new Vec3D(x, y, z), new Vec3D(x, y + height, z), new Vec3D(x + width, y + height, z), new Vec2D(0,0), new Vec2D(0,1), new Vec2D(1,1)),
        new Triangle(new Vec3D(x, y, z), new Vec3D(x + width, y + height, z), new Vec3D(x + width, y, z), new Vec2D(0,0), new Vec2D(1, 1), new Vec2D(1,0)),
        //EAST
        new Triangle(new Vec3D(x + width, y, z), new Vec3D(x + width, y + height, z), new Vec3D(x + width, y + height, z + depth), new Vec2D(0,0), new Vec2D(0, 1), new Vec2D(1, 1)),
        new Triangle(new Vec3D(x + width, y, z), new Vec3D(x + width, y + height, z + depth), new Vec3D(x + width, y, z + depth),  new Vec2D(0,0), new Vec2D(1, 1), new Vec2D(1, 0)),
        //NORTH
        new Triangle(new Vec3D(x + width, y, z  + depth), new Vec3D(x  + width, y + height, z + depth), new Vec3D(x, y + height, z + depth), new Vec2D(0,0), new Vec2D(0, 1), new Vec2D(1,1)),
        new Triangle(new Vec3D(x + width, y, z + depth), new Vec3D(x, y + height, z + depth), new Vec3D(x, y, z + depth), new Vec2D(0,0), new Vec2D(1, 1), new Vec2D(1, 0)),
        //WEST
        new Triangle(new Vec3D(x, y, z + depth), new Vec3D(x, y + height, z + depth), new Vec3D(x, y + height, z), new Vec2D(0,0), new Vec2D(0,1), new Vec2D(1,1)),
        new Triangle(new Vec3D(x, y, z + depth), new Vec3D(x, y + height, z), new Vec3D(x, y, z),new Vec2D(0,0), new Vec2D(1,1), new Vec2D(1,0)),
        //TOP
        new Triangle(new Vec3D(x, y + height, z), new Vec3D(x, y + height, z + depth), new Vec3D(x + width, y + height, z + depth), new Vec2D(0,0), new Vec2D(0, 1), new Vec2D(1, 1)),
        new Triangle(new Vec3D(x, y + height, z), new Vec3D(x + width, y + height, z + depth), new Vec3D(x + width, y + height, z), new Vec2D(0,0), new Vec2D(1, 1), new Vec2D(1, 0)),
        //BOTTOM
        new Triangle(new Vec3D(x + width, y, z + depth), new Vec3D(x, y, z + depth), new Vec3D(x, y, z), new Vec2D(0,0), new Vec2D(0, 1), new Vec2D(1,1)),
        new Triangle(new Vec3D(x + width, y, z + depth), new Vec3D(x, y, z), new Vec3D(x + width, y, z), new Vec2D(0,0), new Vec2D(1, 1), new Vec2D(1,0))
        }));

    }
}
