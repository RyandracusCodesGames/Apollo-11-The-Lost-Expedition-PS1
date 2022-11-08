
package com.ryancodesgames.apollo.mathlib;


public class Movement 
{
    private double velocity;
    private double velocityAngleX;
    private double velocityAngleY;
    private double velocityAngleZ;
    
    private Vec3D velocityVector;

    public void setVelocity(double f)
    {
        this.velocity = f;
    }
    
    public Vec3D setVelocity(Vec3D direction)
    {
        Vec3D out = new Vec3D(0,0,0);
        
        velocityVector = out.multiplyVector(direction, velocity);
        
        return velocityVector;
    }
    
    public boolean isMoving()
    {
        return velocity == 0.0;
    }
    
    public double getDistance(Vec3D pos, Vec3D target)
    {
        Vec3D i = new Vec3D(0,0,0);
        
        Vec3D distance = i.subtractVector(target, pos);
        
        double f = i.vectorLength(distance);
        
        return f;
    }
    
    public Vec3D getDirection(Vec3D target, Vec3D pos)
    {
        Vec3D n = new Vec3D(0,0,0);
        
        Vec3D direction = n.subtractVector(target,pos);
        direction = n.normalize(direction);
        
        return direction;
    }
    
    public double getVelocity()
    {
        return velocity;
    }
    
    public Vec3D getVelocityVector()
    {
        return velocityVector;
    }
}
