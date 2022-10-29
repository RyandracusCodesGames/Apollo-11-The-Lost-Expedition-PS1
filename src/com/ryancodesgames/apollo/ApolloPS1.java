
package com.ryancodesgames.apollo;

import javax.swing.JFrame;

public class ApolloPS1 {
    
    private static final int WIDTH = 800;
    
    private static final int HEIGHT = 600;
    
    private static final String TITLE = "Apollo 11 - The Lost Expedition (PS1)";

    public static void main(String[] args) 
    {
        GamePanel gp = new GamePanel();
        
        JFrame jframe = new JFrame();
        jframe.setSize(WIDTH, HEIGHT);
        jframe.setResizable(false);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setTitle(TITLE);
        jframe.setLocationRelativeTo(null);
        jframe.add(gp);
        jframe.setVisible(true);
        
        gp.startGameThread();
    }
    
    public static int getFrameWidth()
    {
        return WIDTH;
    }
    
    public static int getFrameHeight()
    {
        return HEIGHT;
    }
    
}
