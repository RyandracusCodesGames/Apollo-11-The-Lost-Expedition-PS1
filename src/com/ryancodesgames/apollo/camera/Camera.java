
package com.ryancodesgames.apollo.camera;

import com.ryancodesgames.apollo.mathlib.Matrix;
import com.ryancodesgames.apollo.mathlib.Vec3D;


public class Camera 
{
    private Vec3D cam;
    private Vec3D vLookDir;
    private Vec3D vUp;
    private Vec3D vTarget;  
    private Matrix matCameraRot;
    private Matrix viewMatrix;
    
    public Camera(double x, double y, double z)
    {
        cam = new Vec3D(0,0,0);
        
        cam.x = x;
        cam.y = y;
        cam.z = z;
    }
    
    public Camera()
    {
        cam = new Vec3D(0,0,0);
    }
 
    public Vec3D getCamera()
    {
        return cam;
    }
    
    public Matrix getViewMatrix()
    {
        return viewMatrix;
    }
    
    public void setForwardDirection(Vec3D vFoward)
    {
        cam = cam.addVector(cam, vFoward);
    }
    
    public void setForwardDirectionBack(Vec3D vFoward)
    {
        cam = cam.subtractVector(cam, vFoward);
    }
    
    public void addCameraX(double f)
    {
        cam.x += f;
    }
    
    public void addCameraY(double f)
    {
        cam.y += f;
    }
    
    public void addCameraZ(double f)
    {
        cam.z += f;
    }
    
    public void subtractCameraX(double f)
    {
        cam.x -= f;
    }
    
    public void subtractY(double f)
    {
        cam.y -= f;
    }
    
    public void subtractZ(double f)
    {
        cam.z -= f;
    }
}
