
package com.ryancodesgames.apollo.gfx;

import java.awt.image.BufferedImage;

/*
    CLASS MEANT TO BE FAST, COMPACT METHOD TO HOLD COLOR INFORMATION
    OF A PARTICULAR PIXEL.
*/
public class ColorUtils 
{
    //COLOR PALETTE
    public static final int BLACK = -16777216;
    public static final int WHITE = -1;
    public static final int BLUE = -16776961;
    public static final int GREEN = -16711936;
    public static final int GRAY = -8355712;
    public static final int RED = -65536;
    public static final int ORANGE = -14336;
    public static final int MAGENTA = -65281;
    public static final int YELLOW = -256;
    //DARKER VERSION OF COLOR PALETTE
    public static final int BLACK_LIGHTER = -16579837;
    public static final int WHITE_DARKER = -5066062;
    public static final int BLUE_DARKER = -16777038;
    public static final int GREEN_DARKER = -16731648;
    public static final int GRAY_DARKER = -10921639;
    public static final int RED_DARKER = -5111808;
    public static final int ORANGE_DARKER = -5075968;
    public static final int MAGENTA_DARKER = -5111630;
    public static final int YELLOW_DARKER= -5066240;
    
    /**
    * Bits count representing each color value.
    */
    public static final byte COLOR_BITS = 8;
	
    /**
     * Value representing the range of each color value. (0 - {@value #COLOR_ONE})
     */
    public static final int COLOR_ONE = (1 << COLOR_BITS) - 1;

    /**
     * Number representing the BufferedImage color type that the ColorProcessor handles.
     */
    public static final byte COLOR_TYPE = BufferedImage.TYPE_INT_ARGB;
    public int mix(int col, int col2)
    {
        int i1 = col;
        int i2 = col2;
        
        int a1 = (i1 >> 24 & 0xff);
        int r1 = ((i1 & 0xff0000) >> 16);
        int g1 = ((i1 & 0xff00) >> 8);
        int b1 = (i1 & 0xff);

        int a2 = (i2 >> 24 & 0xff);
        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);

        int a = a1 + a2;
        int r = r1 + r2;
        int g = g1 + g2;
        int b = b1 + b2;

        return a << 24 | r << 16 | g << 8 | b ;
    }
    
    public int multiply(int col, int col2)
    {
        int i1 = col;
        int i2 = col2;
        
        int a1 = (i1 >> 24 & 0xff);
        int r1 = ((i1 & 0xff0000) >> 16);
        int g1 = ((i1 & 0xff00) >> 8);
        int b1 = (i1 & 0xff);

        int a2 = (i2 >> 24 & 0xff);
        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);

        int a = a1 * a2;
        int r = r1 * r2;
        int g = g1 * g2;
        int b = b1 * b2;

        return a << 24 | r << 16 | g << 8 | b ;
    }
    
    public int blend(int col, int col2, float ratio)
    {
        if ( ratio > 1f ) ratio = 1f;
        else if ( ratio < 0f ) ratio = 0f;
        float iRatio = 1.0f - ratio;

        int i1 = col;
        int i2 = col2;

        int a1 = (i1 >> 24 & 0xff);
        int r1 = ((i1 & 0xff0000) >> 16);
        int g1 = ((i1 & 0xff00) >> 8);
        int b1 = (i1 & 0xff);

        int a2 = (i2 >> 24 & 0xff);
        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);

        int a = (int)((a1 * iRatio) + (a2 * ratio));
        int r = (int)((r1 * iRatio) + (r2 * ratio));
        int g = (int)((g1 * iRatio) + (g2 * ratio));
        int b = (int)((b1 * iRatio) + (b2 * ratio));

        return a << 24 | r << 16 | g << 8 | b ;
    }
}
