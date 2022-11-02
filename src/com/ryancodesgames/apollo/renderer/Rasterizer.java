
package com.ryancodesgames.apollo.renderer;

import com.ryancodesgames.apollo.camera.Camera;
import com.ryancodesgames.apollo.gfx.GraphicsContext;
import com.ryancodesgames.apollo.gfx.ZBuffer;
import com.ryancodesgames.apollo.mathlib.Matrix;
import com.ryancodesgames.apollo.mathlib.Mesh;
import com.ryancodesgames.apollo.mathlib.PolygonGroup;
import java.awt.Graphics2D;


public class Rasterizer 
{
    private PolygonGroup poly = new PolygonGroup();
    private Camera camera;
    private Matrix matWorld;
    private Matrix matView;
    private Matrix matProj;
    private double visibility;
    private int[] pixels;
    private ZBuffer zBuffer;
    private GraphicsContext gc;
    
    public Rasterizer(PolygonGroup poly, Camera camera, Matrix matWorld, Matrix matView, Matrix matProj, ZBuffer zBuffer, int[] pixels)
    {
        this.poly = poly;
        this.camera = camera;
        this.matWorld = matWorld;
        this.matView = matView;
        this.matProj = matProj;
        this.zBuffer = zBuffer;
        this.pixels = pixels;
    }
 
    public void draw(Graphics2D g2)
    {
       for(Mesh mesh: poly.getPolygonGroup())
       {
           
       }
    }
}
