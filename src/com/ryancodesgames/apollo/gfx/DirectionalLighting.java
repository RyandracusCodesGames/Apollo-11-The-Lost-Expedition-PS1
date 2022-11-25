
package com.ryancodesgames.apollo.gfx;

import com.ryancodesgames.apollo.mathlib.Vec3D;


public class DirectionalLighting 
{
    private Vec3D light_direction;
    private Vec3D normalized_direction;
    private double dp;
    
    public DirectionalLighting(double x, double y, double z)
    {
        Vec3D out = new Vec3D(0,0,0);
        
        this.light_direction.x = x;
        this.light_direction.y = y;
        this.light_direction.z = z;
        
        this.normalized_direction = out.normalize(light_direction);
    }
    
    public double getDotProduct(Vec3D normal)
    {
        this.dp = Math.max(0.1, normal.dotProduct(light_direction, normal));
        
        return dp;
    }
    
    public void setLightDirection(double x, double y, double z)
    {
        this.light_direction.x = x;
        this.light_direction.y = y;
        this.light_direction.z = z;
        
        this.normalized_direction = light_direction.normalize(normalized_direction);
    }
    
    public Vec3D getLightDirection()
    {
        return light_direction;
    }
    
    public Vec3D getNormalizedLightDirection()
    {
        return normalized_direction;
    }
}
