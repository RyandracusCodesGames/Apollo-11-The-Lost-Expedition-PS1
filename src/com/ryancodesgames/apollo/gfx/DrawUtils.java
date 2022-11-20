
package com.ryancodesgames.apollo.gfx;

import static com.ryancodesgames.apollo.gfx.ColorUtils.GRAY;
import static com.ryancodesgames.apollo.gfx.ColorUtils.WHITE;
import static com.ryancodesgames.apollo.gfx.ColorUtils.blend;
import com.ryancodesgames.apollo.mathlib.Vec2D;
import com.ryancodesgames.apollo.mathlib.Vec3D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

public class DrawUtils 
{
    public static void drawTriangle(Graphics2D g2, double x1, double y1, double x2, double y2, double x3, double y3)
    {
        g2.setStroke(new BasicStroke(2));
        g2.draw(new Line2D.Double(x1, y1, x2, y2));
        g2.draw(new Line2D.Double(x2, y2, x3, y3));
        g2.draw(new Line2D.Double(x3, y3, x1, y1));
    }
    
    public static void DrawTriangle(int[] pixels, int x1, int y1, int x2, int y2, int x3, int y3, int color)
    {
        tigrLine(pixels, x1, y1, x2, y2, color);
        tigrLine(pixels, x2, y2, x3, y3, color);
        tigrLine(pixels, x3, y3, x1, y1, color);
    }
    
   public static void texturedTriangle(Graphics2D g2, int x1, int y1, double u1, double v1, int x2, int y2, double
    u2, double v2, int x3, int y3, double u3, double v3, BufferedImage img, double visibility, boolean fog, int[] pix)
    {
 
        if(y2 < y1)
        {
            int temp = y1;
            y1 = y2;
            y2 = temp;
            
            int tempx = x1;
            x1 = x2;
            x2 = tempx;
            
            double tempu = u1;
            u1 = u2;
            u2 = tempu;
            
            double tempv = v1;
            v1 = v2;
            v2 = tempv;
        }
        
        if(y3 < y1)
        {
            int temp = y1;
            y1 = y3;
            y3 = temp;
            
            int tempx = x1;
            x1 = x3;
            x3 = tempx;
            
            double tempu = u1;
            u1 = u3;
            u3 = tempu;
            
            double tempv = v1;
            v1 = v3;
            v3 = tempv;
        }
        
        if(y3 < y2)
        {
            int temp = y2;
            y2 = y3;
            y3 = temp;
            
            int tempx = x2;
            x2 = x3;
            x3 = tempx;
            
            double tempu = u2;
            u2 = u3;
            u3 = tempu;
            
            double tempv = v2;
            v2 = v3;
            v3 = tempv;
        }
        
        int dy1 = y2 - y1;
        int dx1 = x2 - x1;
        double dv1 = v2 - v1;
        double du1 = u2 - u1;
        
        int dy2 = y3 - y1;
        int dx2 = x3 - x1;
        double dv2 = v3 - v1;
        double du2 = u3 - u1;
        
        double tex_u, tex_v;
        
        double dax_step = 0, dbx_step = 0, du1_step = 0, dv1_step = 0, du2_step = 0, dv2_step = 0;
        
         if (dy1 != 0) dax_step = dx1 / (float)Math.abs(dy1);
         if (dy2 != 0) dbx_step = dx2 / (float)Math.abs(dy2);

	 if (dy1 != 0) du1_step = du1 / (float)Math.abs(dy1);
	 if (dy1 != 0) dv1_step = dv1 / (float)Math.abs(dy1);
 
	 if (dy2 != 0) du2_step = du2 / (float)Math.abs(dy2);
	 if (dy2 != 0) dv2_step = dv2 / (float)Math.abs(dy2);
         
         if(dy1 != 0)
         {
             for(int i = y1; i <= y2; i++)
             {
                 int ax = (int)(x1 + (i - y1) * dax_step);
		 int bx = (int)(x1 + (i - y1) * dbx_step);
                 
                 double tex_su = u1 + (float)(i - y1) * du1_step;
		 double tex_sv = v1 + (float)(i - y1) * dv1_step;
                 
                 double tex_eu = u1 + (float)(i - y1) * du2_step;
		 double tex_ev = v1 + (float)(i - y1) * dv2_step;
                 
                 if(ax > bx)
                 {
                     int temp = ax;
                     ax = bx;
                     bx = temp;
                     
                     double temps = tex_su;
                     tex_su = tex_eu;
                     tex_eu = temps;
                     
                     double tempv = tex_sv;
                     tex_sv = tex_ev;
                     tex_ev = tempv;
                 }
                 
                 tex_u = tex_su;
                 tex_v = tex_sv;
                 
                 double tstep = 1.0 / (float)(bx-ax);
                 double t = 0.0;

                 
                 for(int j = ax; j < bx; j++)
                 {
                     tex_u = (1.0 - t) * tex_su + t * tex_eu;
                     tex_v = (1.0 - t) * tex_sv + t * tex_ev;
                     
                     Color background = Color.black;
                     Color col = new Color(img.getRGB(
                          (int)Math.max(0,tex_u*(img.getWidth()-1)),
                          (int)Math.max(0,tex_v*(img.getHeight()-1))
                        ));
                     
                     if(fog)
                     {
                        // col = blend(background, col,(float)visibility);
                         g2.setColor(col); 
                     }
                     
                     else
                     {
                        // g2.setColor(new Color(getRGB((int)Math.max(0,tex_u*(img.getWidth()-1)),(int)Math.max(0,tex_v*(img.getHeight()-1)), width, height, pixelLength, pixels, hasAlphaChannel)));
                     }
                    
                      
                     draw(pix, j, i, col);
                     
                     t += tstep;
                 }

             }

         }
         
         dy1 = y3 - y2;
             dx1 = x3 - x2;
             dv1 = v3 - v2;
             du1 = u3 - u2;
             
             if (dy1 != 0) dax_step = dx1 / (float)Math.abs(dy1);
	     if (dy2 != 0) dbx_step = dx2 / (float)Math.abs(dy2);

             du1_step = 0; dv1_step = 0;
             
             if (dy1 != 0) du1_step = du1 /(float)Math.abs(dy1);
             if (dy1 != 0) dv1_step = dv1 / (float)Math.abs(dy1);
             
             if(dy1 != 0)
         {
             for(int i = y2; i <= y3; i++)
             {
                 int ax = (int)(x2 + (float)(i - y2) * dax_step);
		 int bx = (int)(x1 + (float)(i - y1) * dbx_step);
                 
                 double tex_su = u2 + (float)(i - y2) * du1_step;
		 double tex_sv = v2 + (float)(i - y2) * dv1_step;
                 
                 double tex_eu = u1 + (float)(i - y1) * du2_step;
		 double tex_ev = v1 + (float)(i - y1) * dv2_step;
                 
                 if(ax > bx)
                 {
                     int temp = ax;
                     ax = bx;
                     bx = temp;
                     
                     double temps = tex_su;
                     tex_su = tex_eu;
                     tex_eu = temps;
                     
                     double tempv = tex_sv;
                     tex_sv = tex_ev;
                     tex_ev = tempv;
                 }
                 
                 tex_u = tex_su;
                 tex_v = tex_sv;
                 
                 double tstep = 1.0/ (float)(bx-ax);
                 double t = 0.0;
                 
                 for(int j = ax; j < bx; j++)
                 {
                     tex_u = (1.0 - t) * tex_su + t * tex_eu;
                     tex_v = (1.0 - t) * tex_sv + t * tex_ev;
                     
                     Color background = Color.black;
                     Color col = new Color(img.getRGB(
                          (int)Math.max(0,tex_u*(img.getWidth()-1)),
                          (int)Math.max(0,tex_v*(img.getHeight()-1))
                        ));

                    if(fog)
                     {
                        // col = blend(background, col,(float)visibility);
                         g2.setColor(col); 
                     }
                     
                     else
                     {
                        // g2.setColor(new Color(getRGB((int)Math.max(0,tex_u*(img.getWidth()-1)),(int)Math.max(0,tex_v*(img.getHeight()-1)), width, height, pixelLength, pixels, hasAlphaChannel)));
                     }
                      
                     draw(pix, j, i, col);

                     t += tstep;
                     
                 }
             }

         }
    }
    
    public static void TexturedTriangle(Graphics2D g2, int x1, int y1, double u1, double v1, double w1, int x2, int y2, double
    u2, double v2, double w2, int x3, int y3, double u3, double v3, double w3,Texture tex, double visibility, boolean fog, int[] pix, double[] zBuffer, int[] texArray)
    {
        
        if(y2 < y1)
        {
            int temp = y1;
            y1 = y2;
            y2 = temp;
            
            int tempx = x1;
            x1 = x2;
            x2 = tempx;
            
            double tempu = u1;
            u1 = u2;
            u2 = tempu;
            
            double tempv = v1;
            v1 = v2;
            v2 = tempv;
            
            double tempw = w1;
            w1 = w2;
            w2 = tempw;
            
        }
        
        if(y3 < y1)
        {
            int temp = y1;
            y1 = y3;
            y3 = temp;
            
            int tempx = x1;
            x1 = x3;
            x3 = tempx;
            
            double tempu = u1;
            u1 = u3;
            u3 = tempu;
            
            double tempv = v1;
            v1 = v3;
            v3 = tempv;
            
            double tempw = w1;
            w1 = w3;
            w3 = tempw;
        }
        
        if(y3 < y2)
        {
            int temp = y2;
            y2 = y3;
            y3 = temp;
            
            int tempx = x2;
            x2 = x3;
            x3 = tempx;
            
            double tempu = u2;
            u2 = u3;
            u3 = tempu;
            
            double tempv = v2;
            v2 = v3;
            v3 = tempv;
            
            double tempw = w2;
            w2 = w3;
            w3 = tempw;
            
        }
        
        int dy1 = y2 - y1;
        int dx1 = x2 - x1;
        double dv1 = v2 - v1;
        double du1 = u2 - u1;
        double dw1 = w2 - w1;
        
        int dy2 = y3 - y1;
        int dx2 = x3 - x1;
        double dv2 = v3 - v1;
        double du2 = u3 - u1;
        double dw2 = w3 - w1;
        
        double tex_u, tex_v, tex_w;
        
        double dax_step = 0, dbx_step = 0, du1_step = 0, dv1_step = 0, du2_step = 0, dv2_step = 0, dw1_step = 0, dw2_step = 0;
        
         if (dy1 != 0) dax_step = dx1 / (float)Math.abs(dy1);
         if (dy2 != 0) dbx_step = dx2 / (float)Math.abs(dy2);

	 if (dy1 != 0) du1_step = du1 / (float)Math.abs(dy1);
	 if (dy1 != 0) dv1_step = dv1 / (float)Math.abs(dy1);
         if (dy1 != 0) dw1_step = dw1 / (float)Math.abs(dy1);
 
	 if (dy2 != 0) du2_step = du2 / (float)Math.abs(dy2);
	 if (dy2 != 0) dv2_step = dv2 / (float)Math.abs(dy2);
         if (dy2 != 0) dw2_step = dw2 / (float)Math.abs(dy2);
         
         if(dy1 != 0)
         {
             for(int i = y1; i <= y2; i++)
             {
                 int ax = (int)(x1 + (i - y1) * dax_step);
		 int bx = (int)(x1 + (i - y1) * dbx_step);
                 
                 double tex_su = u1 + (float)(i - y1) * du1_step;
		 double tex_sv = v1 + (float)(i - y1) * dv1_step;
                 double tex_sw = w1 + (float)(i - y1) * dw1_step;
                 
                 double tex_eu = u1 + (float)(i - y1) * du2_step;
		 double tex_ev = v1 + (float)(i - y1) * dv2_step;
                 double tex_ew = w1 + (float)(i - y1) * dw2_step;
                 
                 if(ax > bx)
                 {
                     int temp = ax;
                     ax = bx;
                     bx = temp;
                     
                     double temps = tex_su;
                     tex_su = tex_eu;
                     tex_eu = temps;
                     
                     double tempv = tex_sv;
                     tex_sv = tex_ev;
                     tex_ev = tempv;
                     
                     double tempw = tex_sw;
                     tex_sw = tex_ew;
                     tex_ew = tempw;
                 }
                 
                 tex_u = tex_su;
                 tex_v = tex_sv;
                 tex_w = tex_sw;
                 
                 double tstep = 1.0 / (float)(bx-ax);
                 double t = 0.0;

                 
                 for(int j = ax; j < bx; j++)
                 {
                    tex_u = (1.0 - t) * tex_su + t * tex_eu;
                    tex_v = (1.0 - t) * tex_sv + t * tex_ev;
                    tex_w = (1.0 - t) * tex_sw + t * tex_ew;

                    if(Math.abs(tex_w) > zBuffer[i * 800 + j])
                    {
                        int iu = (int) ((tex_u / tex_w) * tex.getWidth()) & tex.getWidthMask();
                        int iv = (int) ((tex_v / tex_w) * tex.getHeight()) & tex.getHeightMask();
                        int col = tex.getTexArray()[iu + (iv << tex.getWidthShift())];
                                               
                        int backgroundColor = blend(GRAY, WHITE, 0.4f);
                        
                        if(fog)
                        {
                            col = blend(backgroundColor, col, (float)visibility);
                        }
                
                        draw(pix, j, i, col);
                        zBuffer[i * 800 + j] = Math.abs(tex_w);
                    }
                        
                    t += tstep;
                 }

             }

         }
         
         dy1 = y3 - y2;
             dx1 = x3 - x2;
             dv1 = v3 - v2;
             du1 = u3 - u2;
             dw1 = w3 - w2;
             
             if (dy1 != 0) dax_step = dx1 / (float)Math.abs(dy1);
	     if (dy2 != 0) dbx_step = dx2 / (float)Math.abs(dy2);

             du1_step = 0; dv1_step = 0; dw1_step = 0;
             
             if (dy1 != 0) du1_step = du1 /(float)Math.abs(dy1);
             if (dy1 != 0) dv1_step = dv1 / (float)Math.abs(dy1);
             if (dy1 != 0) dw1_step = dw1 / (float)Math.abs(dy1);
             
             if(dy1 != 0)
         {
             for(int i = y2; i <= y3; i++)
             {
                 int ax = (int)(x2 + (float)(i - y2) * dax_step);
		 int bx = (int)(x1 + (float)(i - y1) * dbx_step);
                 
                 double tex_su = u2 + (float)(i - y2) * du1_step;
		 double tex_sv = v2 + (float)(i - y2) * dv1_step;
                 double tex_sw = w2 + (float)(i - y2) * dw1_step;
                 
                 double tex_eu = u1 + (float)(i - y1) * du2_step;
		 double tex_ev = v1 + (float)(i - y1) * dv2_step;
                 double tex_ew = w1 + (float)(i - y1) * dw2_step;
                 
                 if(ax > bx)
                 {
                     int temp = ax;
                     ax = bx;
                     bx = temp;
                     
                     double temps = tex_su;
                     tex_su = tex_eu;
                     tex_eu = temps;
                     
                     double tempv = tex_sv;
                     tex_sv = tex_ev;
                     tex_ev = tempv;
                     
                     double tempw = tex_sw;
                     tex_sw = tex_ew;
                     tex_ew = tempw;
                 }
                 
                 tex_u = tex_su;
                 tex_v = tex_sv;
                 tex_w = tex_sw;
                 
                 double tstep = 1.0/ (float)(bx-ax);
                 double t = 0.0;
                 
                 for(int j = ax; j < bx; j++)
                 {
                     tex_u = (1.0 - t) * tex_su + t * tex_eu;
                     tex_v = (1.0 - t) * tex_sv + t * tex_ev;
                     tex_w = (1.0 - t) * tex_sw + t * tex_ew;
                     
                   if(Math.abs(tex_w) > zBuffer[i * 800 + j])
                    {
                        int iu = (int) ((tex_u / tex_w) * tex.getWidth()) & tex.getWidthMask();
                        int iv = (int) ((tex_v / tex_w) * tex.getHeight()) & tex.getHeightMask();
                        int col = tex.getTexArray()[iu + (iv << tex.getWidthShift())];
                                               
                        int backgroundColor = blend(GRAY, WHITE, 0.4f);
                        
                        if(fog)
                        {
                            col = blend(backgroundColor, col, (float)visibility);
                        }

                        draw(pix, j, i, col);

                        zBuffer[i * 800 + j] = Math.abs(tex_w);
                    }
                     t += tstep;
                     
                 }
             }

         }
    }
    
    public static void drawSurface(int[] pixels, Vec3D v1, Vec3D v2, Vec3D v3, Vec2D vt1, Vec2D vt2, Vec2D vt3, Texture tex, double[] zbuff)
    {
        int minX = (int)(Math.min(Math.min(v1.x, v2.x), v3.x)),
        maxX = (int)(Math.max(Math.max(v1.x, v2.x), v3.x)+1),
        minY = (int)(Math.min(Math.min(v1.y, v2.y), v3.y)),
        maxY = (int)(Math.max(Math.max(v1.y, v2.y), v3.y)+1);
        
        for(int y = minY; y < maxY; y++)
        {
            for(int x = minX; x < maxX; x++)
            {
                Vec3D p = new Vec3D(x, y, 0);
                Vec3D bc = p.barycenter(p, v1, v2, v3);
                float err = -0.0001f;
                
                if (bc.x >= err && bc.y >= err && bc.z >= err) 
                {
                    double z = bc.x*v1.z + bc.y*v2.z + bc.z*v3.z;
                    int zbuff_idx = y * 800 + x;
                    if (z > zbuff[zbuff_idx]) continue;
                    zbuff[zbuff_idx] = z;
                }

                Vec3D bcc = bc;
                bcc.x = bc.x/(v1.z);
                bcc.y = bc.y/(v2.z);
                bcc.z = bc.z/(v3.z);
                double bd = bcc.x+bcc.y+bcc.z;
                bcc.x = bcc.x/bd;
                bcc.y = bcc.y/bd;
                bcc.z = bcc.z/bd;

                double u = bcc.x*vt1.u + bcc.y*vt2.u + bcc.z*vt3.u;
                double v = 1.0-(bcc.x*vt1.v+ bcc.y*vt2.v + bcc.z*vt3.v);
                
                int tx = (int)(tex.getWidth()-1 * u);
                int ty = (int)(tex.getHeight()-1 * v);
              
                int col = tex.getPixel(tx, ty);

                draw(pixels, x, y, col);
            }
        } 
    }

   public static void draw(int[] pixels, int x, int y, int col)
   {
       if(x >= 0 && y >= 0 && x <= 800 && y <= 600)
       {
           pixels[x + y * 800] = col;
       }
   }
   
   public static int index(int x, int y)
   {
       if(x >= 1 && y >= 0 && x <= 800-1 && y <= 600-1)
       {
           return (x + y * 800);
       }
       return 0;  
   }
   
   public static void fill(int[] pixels, int width, int height, int col)
    {
        for(int x = 0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {
                pixels[x + y * width] = col;
            }
        }
    }
   
   public static void draw(int[] pixels, int x, int y, Color col)
   {
       if(x >= 0 && y >= 0 && x <= 800 && y <= 600)
       {
           pixels[x + y * 800] = col.getRGB();
       }
   }
   
    public static void tigrLine(int[] pixels, int x0, int y0, int x1, int y1, int color) 
    {
        int sx, sy, dx, dy, err, e2;
        dx = Math.abs(x1 - x0);
        dy = Math.abs(y1 - y0);
        if (x0 < x1)
            sx = 1;
        else
            sx = -1;
        if (y0 < y1)
            sy = 1;
        else
            sy = -1;
        err = dx - dy;

        do {
            draw(pixels, x0, y0, color);
            e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        } while (x0 != x1 || y0 != y1);
     }
       
    public static void fillRect(int[] pixels, int x, int y, int width, int height, int col)
    {
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                draw(pixels, i + x, j + y, col);
            }
        }
    }
    
    
}
