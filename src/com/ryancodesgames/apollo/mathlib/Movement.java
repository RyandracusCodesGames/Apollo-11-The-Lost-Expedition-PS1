
package com.ryancodesgames.apollo.mathlib;

import java.util.List;


public class Movement 
{
    private double velocity;
    private double velocityAngleX;
    private double velocityAngleY;
    private double velocityAngleZ;
    
    private Vec3D velocityVector;
    private List<Vec3D> targets;
    private Vec3D origin;
    
    public Movement(Vec3D origin)
    {
        this.origin = origin;
    }

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
    
    public void setOrigin(Vec3D origin)
    {
        this.origin = origin;
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
    
    public double turnIdealYaw(float angleOffset)
    {
        return Math.atan2(-origin.z, origin.x) + ensureAngleWithinBounds(angleOffset);
    }
    
    protected float ensureAngleWithinBounds(float angle)
    {
        if (angle < -Math.PI || angle > Math.PI) 
        {
            // transform range to (0 to 1)
            double newAngle = (angle + Math.PI) / (2*Math.PI);
            // validate range
            newAngle = newAngle - Math.floor(newAngle);
            // transform back to (-pi to pi) range
            newAngle = Math.PI * (newAngle * 2 - 1);
            return (float)newAngle;
        }
        return angle;
    }

    
    public double getVelocity()
    {
        return velocity;
    }
    
    public Vec3D getVelocityVector()
    {
        return velocityVector;
    }
    
    public Vec3D getOrigin()
    {
        return origin;
    }
}
