
package com.ryancodesgames.apollo.mathlib;


public class Transformation 
{
    //ROTATION ANGLE TRANSFORMS
    private double rotAngleX;
    private double rotAngleY;
    private double rotAngleZ;
    //TRANSFORMATION MATRIX DATA
    private double transX;
    private double transY;
    private double transZ;
    //ORIGIN POINT OF MODEL TO UNIFORMELY APPLY ALL TRANSFORMATION TO
    private Vec3D origin;
    //ACCUMULATION OF ALL TRANSFORMATION
    private Matrix matWorld;
    
    //BAISC SETTER METHHODS
    public void setRotAngleX(double angle)
    {
        this.rotAngleX = angle;
    }
    
    public void setRotAngleZ(double angle)
    {
        this.rotAngleZ = angle;
    }
    
    public void setRotAngleY(double angle)
    {
        this.rotAngleY = angle;
    }
    
    public void setTranslationMatrix(double x, double y, double z)
    {
        this.transX = x;
        this.transY = y;
        this.transZ = z;
    }
    
    //BASIC GETTER METHODS
    public Matrix getWorldMatrix()
    {
        Matrix m = new Matrix();
        Matrix matZ = m.rotationMatrixZ(getRotAngleZ());
        Matrix matZX = m.rotationMatrixX(getRotAngleX());
        Matrix matYaw = m.rotationMatrixY(getRotAngleY());
        
        Matrix matWorld = new Matrix();
        matWorld = m.identityMatrix();
        matWorld = m.matrixMatrixMultiplication(matZ, matZX);
        matWorld = m.matrixMatrixMultiplication(matWorld, getTranslationMatrix());
        
        return matWorld;
    }
    
    public Matrix getTranslationMatrix()
    {
        Matrix m = new Matrix();
        Matrix translationMatrix = m.translationMatrix(transX, transY, transZ);
        
        return translationMatrix;
    }
    
    public double getRotAngleX()
    {
        return rotAngleX;
    }
    
    public double getRotAngleY()
    {
        return rotAngleY;
    }
    
    public double getRotAngleZ()
    {
        return rotAngleZ;
    }
    
    public double getTransX()
    {
        return transX;
    }
    
    public double getTransY()
    {
        return transY;
    }
    
    public double getTransZ()
    {
        return transZ;
    }
}
