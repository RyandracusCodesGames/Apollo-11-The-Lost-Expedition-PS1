
package com.ryancodesgames.apollo;

import static com.ryancodesgames.apollo.ApolloPS1.getFrameHeight;
import static com.ryancodesgames.apollo.ApolloPS1.getFrameWidth;
import com.ryancodesgames.apollo.camera.Camera;
import static com.ryancodesgames.apollo.gfx.DrawUtils.drawTriangle;
import com.ryancodesgames.apollo.gfx.GraphicsContext;
import com.ryancodesgames.apollo.input.KeyHandler;
import com.ryancodesgames.apollo.mathlib.Matrix;
import com.ryancodesgames.apollo.mathlib.Mesh;
import com.ryancodesgames.apollo.mathlib.Transformation;
import com.ryancodesgames.apollo.mathlib.Triangle;
import com.ryancodesgames.apollo.mathlib.Vec3D;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;


public class GamePanel extends JPanel implements Runnable
{
    Thread gameThread;
    //FRAMES PER SECOND
    int fps = 60;
    //CLASS THAT HANDLES KEYBOARD USER INPUT
    KeyHandler keyH = new KeyHandler();
    //PROJECTION MATRIX DATA
    double a = (double)getFrameHeight()/(double)getFrameWidth();
    double fov = 90.00;
    double fNear = 0.1;
    double fFar = 1000.00;
    //PROJECTION MATRIX
    Matrix m = new Matrix();
    Matrix matProj = m.projectionMatrix(fov, a, fNear, fFar);
    //COLLECTION OF TRIANGLES THAT DEFINE AN OBJECT IN 3D SPACE
    Mesh mesh = new Mesh();
    Mesh meshCube;
    //ANGLE TO ROTATE OBJECTS AROUND
    double fTheta;
    //CLASS THAT HOLDS TRANSFORMATION MATRICES
    Transformation t = new Transformation();
    //CAMERA
    Camera vCamera = new Camera(0,0,0);
    //GRAPHICS DATA
    private int[] pixels;
    private ColorModel cm;
    private Image imageBuffer;
    private MemoryImageSource mImageProducer;
    
    GraphicsContext gc = new GraphicsContext(pixels, cm, imageBuffer, mImageProducer,
    getFrameWidth(), getFrameHeight());
    
    public GamePanel()
    {   
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(keyH);
        initializeMesh();
    }
    
    public void startGameThread()
    {
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    @Override
    public void run()
    {
        double drawInterval = 1000000000/fps;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        int timer = 0;
        int drawCount = 0;
  
        while(gameThread != null)
        {
            currentTime = System.nanoTime();
            
            delta += (currentTime - lastTime)/drawInterval;
            timer += (currentTime - lastTime);
            
            lastTime = currentTime;
            
            if(delta >= 1)
            {
                update();
                repaint();
                delta--;
                drawCount++;
            }
            
            if(timer >= 1000000000)
            {
                drawCount = 0;
                timer = 0;
            }
        }
    }
    
    public void initializeMesh()
    {
        Mesh m = new Mesh();
        //LOCAL CACHE OF VERTICES  
        List<Triangle> tris2 = new ArrayList<>();
        tris2 = m.ReadOBJFile("lead.txt", true);

        meshCube = new Mesh(tris2);
        
//        meshCube = new Mesh(Arrays.asList(
//        new Triangle[]{
//         //SOUTH
//                    new Triangle(new Vec3D(0,0,0), new Vec3D(0,1,0), new Vec3D(1,1,0)),
//                    new Triangle(new Vec3D(0,0,0), new Vec3D(1,1,0), new Vec3D(1,0,0)),
//                    //EAST
//                    new Triangle(new Vec3D(1,0,0), new Vec3D(1,1,0), new Vec3D(1,1,1)),
//                    new Triangle(new Vec3D(1,0,0), new Vec3D(1,1,1), new Vec3D(1,0,1)),
//                    //NORTH
//                    new Triangle(new Vec3D(1,0,1), new Vec3D(1,1,1), new Vec3D(0,1,1)),
//                    new Triangle(new Vec3D(1,0,1), new Vec3D(0,1,1), new Vec3D(0,0,1)),
//                    //WEST
//                    new Triangle(new Vec3D(0,0,1), new Vec3D(0,1,1), new Vec3D(0,1,0)),
//                    new Triangle(new Vec3D(0,0,1), new Vec3D(0,1,0), new Vec3D(0,0,0)),
//                    //TOP
//                    new Triangle(new Vec3D(0,1,0), new Vec3D(0,1,1), new Vec3D(1,1,1)),
//                    new Triangle(new Vec3D(0,1,0), new Vec3D(1,1,1), new Vec3D(1,1,0)),
//                    //BOTTOM
//                    new Triangle(new Vec3D(1,0,1), new Vec3D(0,0,1), new Vec3D(0,0,0)),
//                    new Triangle(new Vec3D(1,0,1), new Vec3D(0,0,0), new Vec3D(1,0,0))
//        }));
    }
    
    public void update()
    {
        
    }
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;
        
        //FILL SCREEN BLACK
        g.setColor(new Color(0,0,0));
        g.fillRect(0, 0, getFrameWidth(), getFrameHeight());
        
        fTheta += 0.02;
        
        //SET TRANSFORMATION DATA
        t.setRotAngleZ(fTheta * 0.5);
        t.setRotAngleX(fTheta);
        t.setTranslationMatrix(0, 0, 8);
        
        List<Triangle> vecTrianglesToRaster = new ArrayList<>();
         
        for(Triangle tri: meshCube.triangles)
        {
            Triangle triProjected = new Triangle(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0));
            Triangle triTrans = new Triangle(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0));
            
            //ACCUMULATE ALL TRANSFORMATIONS TO ONE MATRIX
            triTrans.vec3d = m.multiplyMatrixVector(tri.vec3d, t.getWorldMatrix());
            triTrans.vec3d2 = m.multiplyMatrixVector(tri.vec3d2, t.getWorldMatrix());
            triTrans.vec3d3 = m.multiplyMatrixVector(tri.vec3d3, t.getWorldMatrix());
            
            //DETERMINE SURFACE NORMALS OF THE MESH
            Vec3D normal = new Vec3D(0,0,0);
            Vec3D line1 = new Vec3D(0,0,0);
            Vec3D line2 = new Vec3D(0,0,0);
            
            line1 = line1.subtractVector(triTrans.vec3d2, triTrans.vec3d);
            line2 = line1.subtractVector(triTrans.vec3d3, triTrans.vec3d);
            
            normal = line1.crossProduct(line1, line2);
            normal = line1.normalize(normal);
            
            Vec3D vCameraRay = line1.subtractVector(triTrans.vec3d, vCamera.getCamera());
            
            //TAKES PROJECTION INTO ACCOUNT TO TEST SIMILARITY BETWEEN NORMAL AND CAMERA VECTOR
            if(line1.dotProduct(normal, vCameraRay) < 0.0)
            {
                //PROJECT 3D GEOMETRICAL DATA TO NORMALIZED 2D SCREEN
                triProjected.vec3d = m.multiplyMatrixVector(triTrans.vec3d, matProj);
                triProjected.vec3d2 = m.multiplyMatrixVector(triTrans.vec3d2, matProj);
                triProjected.vec3d3 = m.multiplyMatrixVector(triTrans.vec3d3, matProj);
                
                //DEFINE DIRECTION OF LIGHT SOURCE TO APPLY TO SURFACES
                Vec3D light_direction = new Vec3D(0,0,-1);
                light_direction = line1.normalize(light_direction);
                
                double dp = Math.max(0.1, line1.dotProduct(light_direction, normal));

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
                
                vecTrianglesToRaster.add(triProjected);
            }
            
            //IMPLEMENTATION OF THE PAINTER'S ALGORITHM (SORT TRIANGLES FROM BACK TO FRONT)
            Collections.sort((ArrayList<Triangle>)vecTrianglesToRaster, new Comparator<Triangle>(){
                @Override
                public int compare(Triangle t1, Triangle t2){
                    double z1 = t1.vec3d.z + t1.vec3d2.z + t1.vec3d3.z / 3.0;
                    double z2 = t2.vec3d.z + t2.vec3d2.z + t2.vec3d3.z / 3.0;
                    return (z1<z2)?1:(z1==z2)?0:-1;
                }
            });
            
            for(Triangle t: vecTrianglesToRaster)
            {
                
                g2.setColor(Color.black);

                drawTriangle(g2, t.vec3d.x, t.vec3d.y, t.vec3d2.x,
                t.vec3d2.y, t.vec3d3.x, t.vec3d3.y);
                
                Polygon triangle = new Polygon();
                triangle.addPoint((int)t.vec3d.x,(int)t.vec3d.y);
                triangle.addPoint((int)t.vec3d2.x,(int)t.vec3d2.y);
                triangle.addPoint((int)t.vec3d3.x,(int)t.vec3d3.y);
                
                g2.setColor(t.col);
                g2.fillPolygon(triangle);
            }

        }
        
        g.dispose();
    }
    
//     @Override
//    public boolean imageUpdate(Image image, int a, int b, int c, int d, int e) {
//        return true;
//    }

}
