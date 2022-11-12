
package com.ryancodesgames.apollo;

import static com.ryancodesgames.apollo.ApolloPS1.getFrameHeight;
import static com.ryancodesgames.apollo.ApolloPS1.getFrameWidth;
import com.ryancodesgames.apollo.camera.Camera;
import com.ryancodesgames.apollo.gameobject.Cargo;
import com.ryancodesgames.apollo.gameobject.Terrain;
import static com.ryancodesgames.apollo.gfx.ColorUtils.BLACK;
import com.ryancodesgames.apollo.gfx.GraphicsContext;
import com.ryancodesgames.apollo.gfx.ZBuffer;
import com.ryancodesgames.apollo.input.KeyHandler;
import com.ryancodesgames.apollo.mathlib.Matrix;
import com.ryancodesgames.apollo.mathlib.Mesh;
import com.ryancodesgames.apollo.mathlib.PolygonGroup;
import com.ryancodesgames.apollo.mathlib.Transformation;
import com.ryancodesgames.apollo.mathlib.Triangle;
import com.ryancodesgames.apollo.mathlib.Vec2D;
import com.ryancodesgames.apollo.mathlib.Vec3D;
import com.ryancodesgames.apollo.renderer.Rasterizer;
import com.ryancodesgames.apollo.sound.Sound;
import java.awt.Color;
import java.awt.Font;
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
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class GamePanel extends JPanel implements Runnable
{
    Thread gameThread;
    //FRAMES PER SECOND
    double fps = 180;
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
    Mesh meshEarth;
    Mesh meshPS1;
    PolygonGroup polygon = new PolygonGroup();
    //TERRAIN
    Terrain moonTerrain = new Terrain();
    //CARGO
    Cargo meshCargo, meshCargo2, meshCargo3, meshCargo4;
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
    
    BufferedImage img, img2, img3, img4, img5, img6;
    
    double moveSpeed = 0.50;
    
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
        Vec2D u = new Vec2D(0,0);
        Vec3D v = new Vec3D(0,0,0);
        
        getRGB();
        //LOCAL CACHE OF VERTICES  
        List<Triangle> tris = new ArrayList<>();
        List<Triangle> tris2 = new ArrayList<>();
        List<Triangle> tris3 = new ArrayList<>();
        
        tris = mesh.ReadOBJFile("earth.txt", true);
        tris2 = mesh.ReadOBJFile("terrain.txt", true);
        
        //SCALE TRANSFORMATION TO VERTICES BY A SET FACTOR
        double scale = 45.100;
        
        for(Triangle t: tris2)
        {
            u.scale(t.vec2d, scale);
            u.scale(t.vec2d2, scale);
            u.scale(t.vec2d3, scale);
        }
        
        for(Triangle t: tris)
        {
            v.scale(t.vec3d, scale*4, true);
            v.scale(t.vec3d2, scale*4, true);
            v.scale(t.vec3d3, scale*4, true);
        }
      
        meshCube = new Mesh(tris2, img);
        meshEarth = new Mesh(tris, img2);
        meshCargo = new Cargo(1852, -50, 2660, 50, 50, 150,img3);
        meshCargo2 = new Cargo(2002, -50, 2660, 50, 50, 150,img3);
        
        moonTerrain.setTerain(meshCube);

        polygon.addMesh(moonTerrain.getTerrain());
        polygon.addMesh(meshEarth);
        polygon.addMesh(meshCargo.getCargo());
        polygon.addMesh(meshCargo2.getCargo());
        
        //SET TRANSFORMATION DATA
        meshCube.transform.setRotAngleZ(0);
        meshCube.transform.setRotAngleX(0);
        meshCube.transform.setTranslationMatrix(0, 0, 8);

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
            img = ImageIO.read(getClass().getResource("/com/ryancodesgames/apollo/gfx/moon.png"));
            img2 = ImageIO.read(getClass().getResource("/com/ryancodesgames/apollo/gfx/earthtex.png"));
            img3 = ImageIO.read(getClass().getResource("/com/ryancodesgames/apollo/gfx/cargoatlas.png"));
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
            vCamera.addCameraX(moveSpeed);
        }
        
        if(keyH.leftPressed)
        {
            vCamera.subtractCameraX(moveSpeed);
        }
        
        if(keyH.downPressed)
        {
            vCamera.addCameraY(moveSpeed);
        }
        
        if(keyH.upPressed)
        {
            vCamera.subtractY(moveSpeed);
        }
        
        Vec3D vFoward = new Vec3D(0,0,0);
        vFoward = vFoward.multiplyVector(vLookDir, 3);
        
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
        
        fTheta += 0.015;

        meshEarth.transform.setRotAngleZ(fTheta * 0.5);
        meshEarth.transform.setRotAngleX(fTheta);
        meshEarth.transform.setTranslationMatrix(0, -1400, 24000);
        meshEarth.transform.setRotAngleY(fTheta);

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
        
        Rasterizer renderer = new Rasterizer(polygon, vCamera, matProj, vLookDir, zBuffer, g2, pixels);
        renderer.draw();

        // ask ImageProducer to update image
         mImageProducer.newPixels();            
        // draw it on panel     
        g2.drawImage(this.imageBuffer, 0, 0, this);
        
        g2.setColor(Color.green);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Coordinates:", 10, 50);
        g2.drawString("X:"+" "+String.valueOf(vCamera.getCamera().x), 10, 70);
        g2.drawString("Y:"+" "+String.valueOf(vCamera.getCamera().y), 10, 90);
        g2.drawString("Z:"+" "+String.valueOf(vCamera.getCamera().z), 10, 110);
        g2.drawString("Triangles:"+" "+String.valueOf(renderer.getTriangleCount()), 10, 150);
        g2.drawString("Textured = TRUE", 10, 180);
        g2.drawString("Yaw:"+" "+String.format("%.4f", fYaw), 10, 210);

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
