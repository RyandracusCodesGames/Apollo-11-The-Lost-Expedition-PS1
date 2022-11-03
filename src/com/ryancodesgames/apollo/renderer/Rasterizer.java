
package com.ryancodesgames.apollo.renderer;

import static com.ryancodesgames.apollo.ApolloPS1.getFrameHeight;
import static com.ryancodesgames.apollo.ApolloPS1.getFrameWidth;
import com.ryancodesgames.apollo.camera.Camera;
import static com.ryancodesgames.apollo.gfx.DrawUtils.TexturedTriangle;
import com.ryancodesgames.apollo.gfx.GraphicsContext;
import com.ryancodesgames.apollo.gfx.ZBuffer;
import com.ryancodesgames.apollo.mathlib.Matrix;
import com.ryancodesgames.apollo.mathlib.Mesh;
import com.ryancodesgames.apollo.mathlib.PolygonGroup;
import com.ryancodesgames.apollo.mathlib.Triangle;
import com.ryancodesgames.apollo.mathlib.Vec2D;
import com.ryancodesgames.apollo.mathlib.Vec3D;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


public class Rasterizer 
{
    private PolygonGroup poly = new PolygonGroup();
    private Camera camera;
    private Vec3D vLookDir;
    private Matrix matProj;
    private double visibility;
    private Graphics2D g2;
    private int[] pixels;
    private ZBuffer zBuffer;
    private GraphicsContext gc;
    
    public Rasterizer(PolygonGroup poly, Camera camera, Matrix matProj,Vec3D vLookDir, ZBuffer zBuffer, Graphics2D g2, int[] pixels)
    {
        this.poly = poly;
        this.camera = camera;
        this.matProj = matProj;
        this.vLookDir = vLookDir;
        this.zBuffer = zBuffer;
        this.g2 = g2;
        this.pixels = pixels;
    }
 
    public void draw()
    {
       Matrix m = new Matrix();
        
       for(Mesh mesh: poly.getPolygonGroup())
       {

           Vec3D vUp = new Vec3D(0,1,0);
           Vec3D vTarget = new Vec3D(0,0,1);
           Matrix matCameraRotated = new Matrix(new double[][]{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}});
           matCameraRotated = m.rotationMatrixY(mesh.transform.getRotAngleY());
           vLookDir = m.multiplyMatrixVector(vTarget, matCameraRotated);
           vTarget = vTarget.addVector(camera.getCamera(), vLookDir);

           //USING THE INFORMATION PROVIDED ABOVE TO DEFIEN A CAMERA MATRIX
           Matrix matCamera = new Matrix(new double[][]{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}});
           matCamera = matCamera.pointAtMatrix(camera.getCamera(), vTarget, vUp);

           Matrix matView = new Matrix(new double[][]{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}});
           matView = matView.inverseMatrix(matCamera);

           List<Triangle> vecTrianglesToRaster = new ArrayList<>();

           Matrix matWorld = mesh.transform.getWorldMatrix();
            
           for(Triangle tri: mesh.triangles)
           {
                Triangle triProjected = new Triangle(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0));
                Triangle triTrans = new Triangle(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0));
                Triangle triViewed = new Triangle(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0));

                //ACCUMULATE ALL TRANSFORMATIONS TO ONE MATRIX
                triTrans.vec3d = m.multiplyMatrixVector(tri.vec3d, matWorld);
                triTrans.vec3d2 = m.multiplyMatrixVector(tri.vec3d2, matWorld);
                triTrans.vec3d3 = m.multiplyMatrixVector(tri.vec3d3, matWorld);
                triTrans.vec2d = tri.vec2d;
                triTrans.vec2d2 = tri.vec2d2;
                triTrans.vec2d3 = tri.vec2d3;

                //DETERMINE SURFACE NORMALS OF THE MESH
                Vec3D normal = new Vec3D(0,0,0);
                Vec3D line1 = new Vec3D(0,0,0);
                Vec3D line2 = new Vec3D(0,0,0);

                line1 = line1.subtractVector(triTrans.vec3d2, triTrans.vec3d);
                line2 = line1.subtractVector(triTrans.vec3d3, triTrans.vec3d);

                normal = line1.crossProduct(line1, line2);
                normal = line1.normalize(normal);

                Vec3D vCameraRay = line1.subtractVector(triTrans.vec3d, camera.getCamera());

                //TAKES PROJECTION INTO ACCOUNT TO TEST SIMILARITY BETWEEN NORMAL AND CAMERA VECTOR
                if(line1.dotProduct(normal, vCameraRay) < 0.0)
                {
                    //DEFINE DIRECTION OF LIGHT SOURCE TO APPLY TO SURFACES
                    Vec3D light_direction = new Vec3D(0,0,-1);
                    light_direction = line1.normalize(light_direction);

                    double dp = Math.max(0.1, line1.dotProduct(light_direction, normal));

                    //WORLD SPACE TO VIEW SPACE
                    triViewed.vec3d = m.multiplyMatrixVector(triTrans.vec3d, matView);
                    triViewed.vec3d2 = m.multiplyMatrixVector(triTrans.vec3d2, matView);
                    triViewed.vec3d3 = m.multiplyMatrixVector(triTrans.vec3d3, matView);
                    triViewed.vec2d = triTrans.vec2d;
                    triViewed.vec2d2 = triTrans.vec2d2;
                    triViewed.vec2d3 = triTrans.vec2d3;

                    //PROJECT 3D GEOMETRICAL DATA TO NORMALIZED 2D SCREEN
                    triProjected.vec3d = m.multiplyMatrixVector(triViewed.vec3d, matProj);
                    triProjected.vec3d2 = m.multiplyMatrixVector(triViewed.vec3d2, matProj);
                    triProjected.vec3d3 = m.multiplyMatrixVector(triViewed.vec3d3, matProj);

                    //CLIP TRIANGLE AGAINST NEAR PLANE
                    int nClippedTriangles = 0;
                    Triangle[] clipped = new Triangle[]{new Triangle(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec2D(0,0), new Vec2D(0,0), new Vec2D(0,0))
                    ,new Triangle(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec2D(0,0), new Vec2D(0,0), new Vec2D(0,0))};

                    nClippedTriangles = line1.triangleClipAgainstPlane(new Vec3D(0,0,0.1), new Vec3D(0,0,1
                    ),triViewed, clipped);

                        for(int i = 0; i < nClippedTriangles; i++)
                        {
                            triProjected.vec3d = m.multiplyMatrixVector(clipped[i].vec3d, matProj);
                            triProjected.vec3d2 = m.multiplyMatrixVector(clipped[i].vec3d2, matProj);
                            triProjected.vec3d3 = m.multiplyMatrixVector(clipped[i].vec3d3, matProj);
                            triProjected.vec2d = (Vec2D)clipped[i].vec2d.clone();
                            triProjected.vec2d2 = (Vec2D)clipped[i].vec2d2.clone();
                            triProjected.vec2d3 = (Vec2D)clipped[i].vec2d3.clone();

                            triProjected.vec2d.u = triProjected.vec2d.u/triProjected.vec3d.w;
                            triProjected.vec2d2.u = triProjected.vec2d2.u/triProjected.vec3d2.w;
                            triProjected.vec2d3.u = triProjected.vec2d3.u/triProjected.vec3d3.w;
                            triProjected.vec2d.v = triProjected.vec2d.v/triProjected.vec3d.w;
                            triProjected.vec2d2.v = triProjected.vec2d2.v/triProjected.vec3d2.w;
                            triProjected.vec2d3.v = triProjected.vec2d3.v/triProjected.vec3d3.w;

                            triProjected.vec2d.w = 1.0/triProjected.vec3d.w;
                            triProjected.vec2d2.w = 1.0/triProjected.vec3d2.w;
                            triProjected.vec2d3.w = 1.0/triProjected.vec3d3.w;

                            triProjected.vec3d = line1.divideVector(triProjected.vec3d, triProjected.vec3d.w);
                            triProjected.vec3d2 = line1.divideVector(triProjected.vec3d2, triProjected.vec3d2.w);
                            triProjected.vec3d3 = line1.divideVector(triProjected.vec3d3, triProjected.vec3d3.w);


                            //SCALE INTO VIEW
                            triProjected.vec3d.x += 1.0;
                            triProjected.vec3d2.x += 1.0;
                            triProjected.vec3d3.x += 1.0;
                            triProjected.vec3d.y += 1.0;
                            triProjected.vec3d2.y += 1.0;
                            triProjected.vec3d3.y += 1.0;

                            triProjected.vec3d.x *= 0.5 * getFrameWidth();
                            triProjected.vec3d.y *= 0.5 * getFrameHeight();
                            triProjected.vec3d2.x *= 0.5 * getFrameWidth();
                            triProjected.vec3d2.y *= 0.5 * getFrameHeight();
                            triProjected.vec3d3.x *= 0.5 * getFrameWidth();
                            triProjected.vec3d3.y *= 0.5 * getFrameHeight();

                            triProjected.setColor((int)Math.abs(dp*255),(int)Math.abs(dp*255),(int)Math.abs(dp*255));
                            triProjected.tex = mesh.tex;

                            vecTrianglesToRaster.add(triProjected);
                    }
    
                }
           }
           
            //IMPLEMENTATION OF THE PAINTER'S ALGORITHM (SORT TRIANGLES FROM BACK TO FRONT)
            Collections.sort((ArrayList<Triangle>)vecTrianglesToRaster, new Comparator<Triangle>() {
                    @Override
                    public int compare(Triangle t1, Triangle t2) {
                        double z1=(t1.vec3d.z+t1.vec3d2.z+t1.vec3d3.z)/3.0;
                        double z2=(t2.vec3d.z+t2.vec3d2.z+t2.vec3d3.z)/3.0;
                        return (z1<z2)?1:(z1==z2)?0:-1;
                    }
                });
            
            zBuffer.resetBuffer();
            
            for(Triangle t: vecTrianglesToRaster)
            {
                Triangle[] clipped = new Triangle[]{new Triangle(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec2D(0,0), new Vec2D(0,0), new Vec2D(0,0)),
                    new Triangle(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec2D(0,0), new Vec2D(0,0), new Vec2D(0,0))};

                LinkedList<Triangle> listTriangles = new LinkedList<>();
                listTriangles.add(t);
                int nNewTriangles = 1;

                for(int p = 0; p < 4; p++)
                {
                    int trisToAdd = 0;

                    while(nNewTriangles > 0)
                    {
                        clipped = new Triangle[]{new Triangle(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec2D(0,0), new Vec2D(0,0), new Vec2D(0,0)),
                    new Triangle(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec2D(0,0), new Vec2D(0,0), new Vec2D(0,0))};
                        
                        Triangle test = new Triangle(new Vec3D(0,0,0),new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec2D(0,0), new Vec2D(0,0), new Vec2D(0,0));
                        test = listTriangles.peek();
                        listTriangles.pollFirst();
                        nNewTriangles--;

                        Vec3D vec = new Vec3D(0,0,0);  

                        switch(p)
                        {
                            case 0:{trisToAdd = vec.triangleClipAgainstPlane(new Vec3D(0,0,0),new Vec3D(0,1,0),test,clipped);}break;
                            case 1:{trisToAdd = vec.triangleClipAgainstPlane(new Vec3D(0,600-1,0),new Vec3D(0,-1,0),test,clipped);}break;
                            case 2:{trisToAdd = vec.triangleClipAgainstPlane(new Vec3D(0,0,0),new Vec3D(1,0,0),test,clipped);}break;
                            case 3:{trisToAdd = vec.triangleClipAgainstPlane(new Vec3D(800-1,0,0),new Vec3D(-1,0,0),test,clipped);}break;
                        }

                        for (int w = 0; w < trisToAdd; w++)
                        {
                            listTriangles.add(clipped[w]);
                        }
                    }
                     nNewTriangles = listTriangles.size();
                }

                for(Triangle tt: listTriangles)
                {
                    Vec3D v = new Vec3D(0,0,0);

                    double density = 0.0035;
                    double gradient = 2.0;

                    Vec3D distFromCamera = new Vec3D(0,0,0);
                    distFromCamera = v.subtractVector(tt.vec3d, camera.getCamera());

                    double distance = v.vectorLength(distFromCamera);

                    double visibility = Math.exp(-Math.pow(distance*density,gradient));
                    visibility = Math.min(Math.max(visibility, 0.0), 1.0);

                    //tt.col = blend(backgroundColor, tt.col, (float)visibility);


//                   texturedTriangle(g2, (int)tt.vec3d.x,(int)tt.vec3d.y, tt.vec2d.u, tt.vec2d.v,(int)tt.vec3d2.x,(int)tt.vec3d2.y,
//                   tt.vec2d2.u, tt.vec2d2.v,(int)tt.vec3d3.x,(int)tt.vec3d3.y, tt.vec2d3.u, tt.vec2d3.v,
//                    meshCube.img, visibility, false, gc.getPixels());
                        
                    TexturedTriangle(g2, (int)tt.vec3d.x,(int)tt.vec3d.y, tt.vec2d.u, tt.vec2d.v,tt.vec2d.w,
                            (int)tt.vec3d2.x,(int)tt.vec3d2.y, tt.vec2d2.u, tt.vec2d2.v, tt.vec2d2.w,
                            (int)tt.vec3d3.x,(int)tt.vec3d3.y, tt.vec2d3.u, tt.vec2d3.v, tt.vec2d3.w,
                    tt.tex,visibility, false, pixels, zBuffer.getZBuffer(), tt.tex.getTexArray());

                  // fillTriangle(pixels,(int)tt.vec3d.x,(int)tt.vec3d.y,(int)tt.vec3d2.x,(int)tt.vec3d2.y,
                  // (int)tt.vec3d3.x,(int)tt.vec3d3.y,tt.col);

//                    g2.setColor(Color.black); 
//                    drawTriangle(g2, tt.vec3d.x, tt.vec3d.y, tt.vec3d2.x,
//                    tt.vec3d2.y, tt.vec3d3.x, tt.vec3d3.y
//                   );
//    //                
//                    //TURN 3D VECTOR X AND Y COORDINATES INTO A POLYGON THAT WILL FILL EACH SURFACE
//                    Polygon triangle = new Polygon();
//                    triangle.addPoint((int)tt.vec3d.x,(int)tt.vec3d.y);
//                    triangle.addPoint((int)tt.vec3d2.x,(int)tt.vec3d2.y);
//                    triangle.addPoint((int)tt.vec3d3.x,(int)tt.vec3d3.y);
//                    
////    
//                    g.setColor(tt.col);
//                    g.fillPolygon(triangle);
                }
                
            }
       }
    }
}
