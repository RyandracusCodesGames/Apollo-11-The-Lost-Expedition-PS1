
package com.ryancodesgames.apollo.gfx;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;


public class GraphicsContext 
{
    //GRAPHICS DATA
    private int[] pixels;
    private ColorModel cm;
    private Image imageBuffer;
    private MemoryImageSource mImageProducer;
    
    public GraphicsContext(int[] pixels, ColorModel cm, Image imageBuffer, MemoryImageSource mImageProducer, int width, int height)
    {
        cm = getCompatibleColorModel();

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
        
        this.pixels = pixels;
        this.mImageProducer = mImageProducer;
        this.imageBuffer = imageBuffer;
        this.cm = cm;
    }
    
    public int[] getPixels()
    {
        return pixels;
    }
    
    public ColorModel getColorModel()
    {
        return cm;
    }
    
    public Image getImageBuffer()
    {
        return imageBuffer;
    }
    
    public MemoryImageSource getMemoryImageSource()
    {
        return mImageProducer;
    }
    
    public void fill()
    {
        int[] pi = pixels; // this avoid crash when resizing
        //a=h/w
        final int h= 600;
        if(pi.length != 800 * 600) return;        
        for (int x=0;x<800;x++) {
            for (int y=0;y<600;y++) {
                boolean found=false;
                if (!found) {
                    pi[x + y * 800] = -16777216;
                }
            }
        }   
    }
    
     protected static ColorModel getCompatibleColorModel(){        
        GraphicsConfiguration gfx_config = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();        
        return gfx_config.getColorModel();
    }
}
