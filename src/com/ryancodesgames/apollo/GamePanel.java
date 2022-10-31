
package com.ryancodesgames.apollo;

import static com.ryancodesgames.apollo.ApolloPS1.getFrameHeight;
import static com.ryancodesgames.apollo.ApolloPS1.getFrameWidth;
import com.ryancodesgames.apollo.camera.Camera;
import static com.ryancodesgames.apollo.gfx.ColorUtils.BLACK;
import static com.ryancodesgames.apollo.gfx.DrawUtils.TexturedTriangle;
import com.ryancodesgames.apollo.gfx.GraphicsContext;
import com.ryancodesgames.apollo.gfx.ZBuffer;
import com.ryancodesgames.apollo.input.KeyHandler;
import com.ryancodesgames.apollo.mathlib.Matrix;
import com.ryancodesgames.apollo.mathlib.Mesh;
import com.ryancodesgames.apollo.mathlib.Transformation;
import com.ryancodesgames.apollo.mathlib.Triangle;
import com.ryancodesgames.apollo.mathlib.Vec2D;
import com.ryancodesgames.apollo.mathlib.Vec3D;
import com.ryancodesgames.apollo.sound.Sound;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class GamePanel extends JPanel implements Runnable
{
    Thread gameThread;
    //FRAMES PER SECOND
    double fps = 100;
    //CLASS THAT HANDLES KEYBOARD USER INPUT
    KeyHandler keyH = new KeyHandler();
    //SIZE OF WINDOW
    int frameWidth = getFrameWidth();
    int frameHeight = getFrameHeight();
    //PROJECTION MATRIX DATA
    double a = (double)frameHeight/(double)frameWidth;
    double fov = 90.00;
    double fNear = 0.1;
    double fFar = 1000.00;
    //PROJECTION MATRIX
    Matrix m = new Matrix();
    Matrix matProj = m.projectionMatrix(fov, a, fNear, fFar);
    //COLLECTION OF TRIANGLES THAT DEFINE AN OBJECT IN 3D SPACE
    Mesh mesh = new Mesh();
    Mesh meshCube;
    //CLASS THAT HANDLES SOUND
    Sound sound = new Sound();
    //ANGLE TO ROTATE OBJECTS AROUND
    double fTheta;
    //ROTATION AROUND Y-AXIS FOR CAMERA
    double fYaw;
    Vec3D vLookDir = new Vec3D(0,0,1);
    //CLASS THAT HOLDS TRANSFORMATION MATRICES
    Transformation t = new Transformation();
    //CAMERA
    Camera vCamera = new Camera(0,0,0); 
    //DEPTH BUFFER
    ZBuffer zBuffer = new ZBuffer(frameWidth, frameHeight);
    //GRAPHICS DATA
    private int[] pixels;
    private ColorModel cm;
    private Image imageBuffer;
    private MemoryImageSource mImageProducer;
    
    BufferedImage img;
    
    GraphicsContext gc = new GraphicsContext(pixels, cm,  imageBuffer, mImageProducer,
    getFrameWidth(), getFrameHeight()); 
    
    public GamePanel()
    {   
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.setDoubleBuffered(true);
        initializeMesh();
        init();
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
    
    protected static ColorModel getCompatibleColorModel(){        
        GraphicsConfiguration gfx_config = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();        
        return gfx_config.getColorModel();
    }
   
    
    public void initializeMesh()
    {
        getRGB();
        //LOCAL CACHE OF VERTICES  
        List<Triangle> tris = new ArrayList<>();
        List<Triangle> tris2 = new ArrayList<>();
        
        tris = mesh.ReadOBJFile("yes.txt", true);
        tris2 = mesh.ReadOBJFile("lead.txt", true);

        meshCube = new Mesh(tris2, img);
       
//         meshCube = new Mesh(Arrays.asList(
//        new Triangle[]{
//            //SOUTH
//            new Triangle(new Vec3D(0,0,0), new Vec3D(0,1,0), new Vec3D(1,1,0), new Vec2D(0,0), new Vec2D(0,1), new Vec2D(1,1)),
//            new Triangle(new Vec3D(0,0,0), new Vec3D(1,1,0), new Vec3D(1,0,0), new Vec2D(0,0), new Vec2D(1,1), new Vec2D(1,0)),
//            //EAST
//            new Triangle(new Vec3D(1,0,0), new Vec3D(1,1,0), new Vec3D(1,1,1), new Vec2D(0,0), new Vec2D(0,1), new Vec2D(1,1)),
//            new Triangle(new Vec3D(1,0,0), new Vec3D(1,1,1), new Vec3D(1,0,1), new Vec2D(0,0), new Vec2D(1,1), new Vec2D(1,0)),
//            //NORTH
//            new Triangle(new Vec3D(1,0,1), new Vec3D(1,1,1), new Vec3D(0,1,1), new Vec2D(0,0), new Vec2D(0,1), new Vec2D(1,1)),
//            new Triangle(new Vec3D(1,0,1), new Vec3D(0,1,1), new Vec3D(0,0,1), new Vec2D(0,0), new Vec2D(1,1), new Vec2D(1,0)),
//            //WEST
//            new Triangle(new Vec3D(0,0,1), new Vec3D(0,1,1), new Vec3D(0,1,0), new Vec2D(0,0), new Vec2D(0,1), new Vec2D(1,1)),
//            new Triangle(new Vec3D(0,0,1), new Vec3D(0,1,0), new Vec3D(0,0,0), new Vec2D(0,0), new Vec2D(1,1), new Vec2D(1,0)),
//            //TOP
//            new Triangle(new Vec3D(0,1,0), new Vec3D(0,1,1), new Vec3D(1,1,1), new Vec2D(0,0), new Vec2D(0,1), new Vec2D(1,1)),
//            new Triangle(new Vec3D(0,1,0), new Vec3D(1,1,1), new Vec3D(1,1,0), new Vec2D(0,0), new Vec2D(1,1), new Vec2D(1,0)),
//            //BOTTOM
//            new Triangle(new Vec3D(1,0,1), new Vec3D(0,0,1), new Vec3D(0,0,0), new Vec2D(0,0), new Vec2D(0,1), new Vec2D(1,1)),
//            new Triangle(new Vec3D(1,0,1), new Vec3D(0,0,0), new Vec3D(1,0,0), new Vec2D(0,0), new Vec2D(1,1), new Vec2D(1,0))
//        }));

    }
    
    public void init()
    {
        cm = getCompatibleColorModel();
        
        int width = 800;
        int height = 600;
        
        int screenSize = width * height;
        
        if(pixels == null || pixels.length < screenSize)
        {
             pixels = new int[screenSize];
        }
        // This class is an implementation of the ImageProducer interface which uses an array 
        // to produce pixel values for an Image.
        mImageProducer =  new MemoryImageSource(width, height, cm, pixels,0, width);
        mImageProducer.setAnimated(true);
        mImageProducer.setFullBufferUpdates(true);  
        imageBuffer = Toolkit.getDefaultToolkit().createImage(mImageProducer); 
    }

    public void getRGB()
     {
        try
        {
            img = ImageIO.read(getClass().getResource("/com/ryancodesgames/apollo/gfx/hh.png"));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
   
    }
    
    public void update()
    {
        if(keyH.rightPressed)
        {
            vCamera.addCameraX(0.25);
        }
        
        if(keyH.leftPressed)
        {
            vCamera.subtractCameraX(0.25);
        }
        
        if(keyH.downPressed)
        {
            vCamera.addCameraY(0.25);
        }
        
        if(keyH.upPressed)
        {
            vCamera.subtractY(0.25);
        }
        
        Vec3D vFoward = new Vec3D(0,0,0);
        vFoward = vFoward.multiplyVector(vLookDir, 1);
        
        if(keyH.frontPressed)
        {
           vCamera.setForwardDirection(vFoward);
        }
        
        if(keyH.backPressed)
        {
           vCamera.setForwardDirectionBack(vFoward);
        }
        
        if(keyH.rightTurn)
        {
            fYaw -= 0.008;
        }
        
        if(keyH.leftTurn)
        {
            fYaw += 0.008;
        }
    }
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        int[] pi = pixels; // this avoid crash when resizing
        //a=h/w
        final int h= 600;
        if(pi.length != 800 * 600) return;        
        for (int x=0;x<800;x++) {
            for (int y=0;y<600;y++) {
                boolean found=false;
                if (!found) {
                    pi[x + y * frameWidth] = BLACK;
                }
            }
        }   
        
        Graphics2D g2 = (Graphics2D)g;

//        //FILL SCREEN BLACK
//        g.setColor(Color.black);
//        g.fillRect(0, 0, frameWidth, frameHeight);
        
        //fTheta += 0.02;
        
        //SET TRANSFORMATION DATA
        t.setRotAngleZ(fTheta * 0.5);
        t.setRotAngleX(fTheta);
        t.setTranslationMatrix(0, 0, 8);

        Vec3D vUp = new Vec3D(0,1,0);
        Vec3D vTarget = new Vec3D(0,0,1);
        Matrix matCameraRotated = new Matrix(new double[][]{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}});
        matCameraRotated = m.rotationMatrixY(fYaw);
        vLookDir = m.multiplyMatrixVector(vTarget, matCameraRotated);
        vTarget = vTarget.addVector(vCamera.getCamera(), vLookDir);
        
        //USING THE INFORMATION PROVIDED ABOVE TO DEFIEN A CAMERA MATRIX
        Matrix matCamera = new Matrix(new double[][]{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}});
        matCamera = matCamera.pointAtMatrix(vCamera.getCamera(), vTarget, vUp);
        
        Matrix matView = new Matrix(new double[][]{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}});
        matView = matView.inverseMatrix(matCamera);

        List<Triangle> vecTrianglesToRaster = new ArrayList<>();
        
        Matrix matWorld = t.getWorldMatrix();
         
        for(Triangle tri: meshCube.triangles)
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
            
            Vec3D vCameraRay = line1.subtractVector(triTrans.vec3d, vCamera.getCamera());
            
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
                    triProjected.tex = meshCube.tex;

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
                    distFromCamera = v.subtractVector(tt.vec3d, vCamera.getCamera());

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
            
         // ask ImageProducer to update image
            mImageProducer.newPixels();            
        // draw it on panel     
           g2.drawImage(this.imageBuffer, 0, 0, this);
            
        g.dispose();
    }
    
     @Override
    public boolean imageUpdate(Image image, int a, int b, int c, int d, int e) {
        return true;
    }
    
    //THIS FUNCTION HANDLES THEME MUSIC MEANT TO LOOP OVER A PERIOD OF TIME
    public void setSound(int i)
    {
        sound.setFile(i);
        sound.play();
        sound.loop();
    }
    
    //FUNCTION USED TO PLAY A SHORT SOUND
    public void play()
    {
        sound.play();
    }
    
    public void stop()
    {
        sound.stop();
    }

}
