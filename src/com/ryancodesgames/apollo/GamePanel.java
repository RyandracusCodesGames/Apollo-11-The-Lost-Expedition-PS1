
package com.ryancodesgames.apollo;

import static com.ryancodesgames.apollo.ApolloPS1.getFrameHeight;
import static com.ryancodesgames.apollo.ApolloPS1.getFrameWidth;
import com.ryancodesgames.apollo.camera.Camera;
import com.ryancodesgames.apollo.gameobject.Cargo;
import com.ryancodesgames.apollo.gameobject.Terrain;
import static com.ryancodesgames.apollo.gfx.ColorUtils.GRAY;
import static com.ryancodesgames.apollo.gfx.ColorUtils.WHITE;
import static com.ryancodesgames.apollo.gfx.ColorUtils.blend;
import static com.ryancodesgames.apollo.gfx.DrawUtils.blur;
import static com.ryancodesgames.apollo.gfx.DrawUtils.fill;
import static com.ryancodesgames.apollo.gfx.DrawUtils.toBufferedImage;
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
import com.ryancodesgames.apollo.ui.Command;
import com.ryancodesgames.apollo.ui.CommandHandler;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable
{
    Thread gameThread;
    //FRAMES PER SECOND
    double fps = 240;
    //CLASS THAT HANDLES KEYBOARD USER INPUT
    KeyHandler keyH = new KeyHandler();
    //CLASS THAT HANDLES DEV COMMANDS
    Command cmd = new Command();
    CommandHandler ch = new CommandHandler(this,cmd);
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
    Mesh meshTemple;
    Mesh meshBase;
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
    //STATES FOR THE GAME
    public final int commandState = 1;
    public final int gameState = 0;
    public int state = gameState;
    //STATES FOR THE DRAWING ROUTINES
    public final int WIREFRAME = 0;
    public final int SURFACE = 1;
    public final int TEXTURED = 2;
    public int drawState = 2;
    //RECTANGLES FOR COMMAND LINE GUI INTERFACE
    Rectangle rect = new Rectangle(0, 429, 400, 35);
    public Rectangle cursor = new Rectangle(12, 435, 7, 18);
    public boolean fog;
    public boolean directionalLighting;
    //FOG INTENSITY
    public double intense = 0.055;
    
    public GamePanel()
    {   
        this.addKeyListener(keyH);
        this.addKeyListener(ch);
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
        List<Triangle> tris4 = new ArrayList<>();
        List<Triangle> tris5 = new ArrayList<>();
        
        tris = mesh.ReadOBJFile("earth.txt", true);
        tris2 = mesh.ReadOBJFile("terrain.txt", true);
        tris3 = mesh.ReadOBJFile("temple.txt",true);
        tris4 = mesh.ReadOBJFile("base.txt",true);
        tris5 = mesh.ReadOBJFile("ps1logo2.txt",true);
        
        //SCALE TRANSFORMATION TO VERTICES BY A SET FACTOR
        double scale = 45.100;
        
        double scale2 = 10;
        
        for(Triangle t: tris2)
        {
            u.scale(t.vec2d, scale);
            u.scale(t.vec2d2, scale);
            u.scale(t.vec2d3, scale);
        }
        
        for(Triangle t: tris3)
        {
            u.scale(t.vec2d, scale);
            u.scale(t.vec2d2, scale);
            u.scale(t.vec2d3, scale);
        }
        
        for(Triangle t: tris4)
        {
            u.scale(t.vec2d, scale2);
            u.scale(t.vec2d2, scale2);
            u.scale(t.vec2d3, scale2);
        }
        
        for(Triangle t: tris)
        {
            v.scale(t.vec3d, scale*4, true);
            v.scale(t.vec3d2, scale*4, true);
            v.scale(t.vec3d3, scale*4, true);
        }
      
        meshCube = new Mesh(tris2, img);
        meshEarth = new Mesh(tris, img2);
        meshTemple = new Mesh(tris3, img4);
        meshPS1 = new Mesh(tris5, img6);
        meshCargo = new Cargo(0, 0, 0, 50, 50, 150,img3);
        meshCargo2 = new Cargo(0, 0, 0, 50, 50, 150,img3);
        meshBase = new Mesh(tris4, img5);
        
        moonTerrain.setTerain(meshCube);

        polygon.addMesh(moonTerrain.getTerrain());
        polygon.addMesh(meshEarth);
        polygon.addMesh(meshTemple);
        polygon.addMesh(meshBase);
        //polygon.addMesh(meshPS1);
        polygon.addMesh(meshCargo.getCargo());
        polygon.addMesh(meshCargo2.getCargo());
        
        //SET TRANSFORMATION DATA
        meshCube.transform.setTranslationMatrix(0, 0, 8);     
        meshTemple.transform.setTranslationMatrix(-1955, -140, 2479);
        meshBase.transform.setTranslationMatrix(2002, -85, 3314);
        meshCargo.getCargo().transform.setTranslationMatrix( 1852, -50, 2660);
        meshCargo2.getCargo().transform.setTranslationMatrix(2002, -50, 2660);

    }
    
    public void init()
    {
        cm = getCompatibleColorModel();
        
        int width = frameWidth;
        int height = frameHeight;
        
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
            img = ImageIO.read(new File("./res/moon.png"));
            img2 = ImageIO.read(new File("./res/earthtex.png"));
            img3 = ImageIO.read(new File("./res/cargoatlas.png"));
            img4 = ImageIO.read(new File("./res/temple.png"));
            img5 = ImageIO.read(new File("./res/yes.png"));
            img6 = ImageIO.read(new File("./res/ps1logo.png"));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
   
    }
    
    public void update()
    {
        if(state != commandState)
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
                fYaw -= 0.006;
            }

            if(keyH.leftTurn)
            {
                fYaw += 0.006;
            }
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        int[] pi = pixels; // this avoid crash when resizing
        if(pi.length != frameWidth * frameHeight) return;        
        fill(pixels, frameWidth, frameHeight, blend(GRAY, WHITE, 0.4f));
        
        Graphics2D g2 = (Graphics2D)g;       

//        //FILL SCREEN BLACK
//        g.setColor(Color.black);
//        g.fillRect(0, 0, frameWidth, frameHeight);
        
        fTheta += 0.015;

        meshEarth.transform.setRotAngleZ(fTheta * 0.5);
        meshEarth.transform.setRotAngleX(fTheta);
        meshEarth.transform.setTranslationMatrix(0, -1400, 24000);

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
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        
        Rasterizer renderer = new Rasterizer(polygon, vCamera, matProj, vLookDir, zBuffer, g2, pixels, fog, directionalLighting, intense, drawState);
        renderer.draw();
        
        int[] filter = {1, 2, 1, 2, 4, 2, 1, 2, 1};
        int filterWidth = 3;
        BufferedImage blurred = blur(toBufferedImage(imageBuffer), filter, filterWidth);

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

        if(state == commandState)
       {
            Character[] s = cmd.getCommand().toArray(new Character[cmd.getCommand().size()]);
            char[] com = new char[cmd.getCommand().size()];

            int size = cmd.getCommand().size();

            for(int ii = 0; ii < size; ii++)
            {
                com[ii] = cmd.getCommand().get(ii);
            }
            
            g2.setColor(Color.gray.darker());
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);

            g2.setColor(Color.white);
            g2.fillRect(cursor.x, cursor.y, cursor.width, cursor.height);

            g2.setColor(Color.black);
            g2.setFont(new Font("Arial",Font.BOLD,15));
            g2.drawChars(com, 0, size, 10, 450);
           
       }

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
