
package com.ryancodesgames.apollo.ui;

import com.ryancodesgames.apollo.GamePanel;
import com.ryancodesgames.apollo.renderer.Rasterizer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Pattern;

public class CommandHandler implements KeyListener
{
    GamePanel gp;
    Command cmd;
    
    public CommandHandler(GamePanel gp, Command cmd)
    {
        this.gp = gp;
        this.cmd = cmd;
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
        if(gp.state != gp.commandState)
        {
            if(e.getKeyCode() == KeyEvent.VK_ENTER)
            {
                gp.state = gp.commandState;
            }
        }
       
        if(gp.state == gp.commandState)
        {
            int key = e.getKeyCode();
            
            if(cmd.codes.containsKey(key))
            {
                char keyPress = cmd.codes.get(key);
                cmd.addCommand(keyPress);
            }

            int factor = 9;
            
            gp.cursor.x += factor;
 
            if(e.getKeyCode() == KeyEvent.VK_SLASH)
            {
                gp.state = gp.gameState;

                int size = cmd.getCommand().size();             
                char[] com = new char[size];
                
                for(int i = 0; i < size; i++)
                {
                    com[i] = cmd.getCommand().get(i);
                }
 
                String readCommand = String.copyValueOf(com);
                String[] split = readCommand.split(Pattern.quote(" "));
                
                for(int i = 0; i < split.length; i++)
                {
                    for(String s: cmd.getCommandList())
                    {      
                        if(split[i].equals(s))
                        {
                            switch(split[i])
                            {
                                case "FOGON":
                                gp.fog = true;
                                break;
                                case "INTENSITY":
                                gp.intense = Double.valueOf(split[i+1]);
                                break;
                                case "FOGOFF":
                                gp.fog = false;
                                break;
                                case "WIREFRAME":
                                gp.drawState = gp.WIREFRAME;
                                break;
                                case "SURFACE":
                                gp.drawState = gp.SURFACE;
                                break;
                                case "TEXTURED":
                                gp.drawState = gp.TEXTURED;
                                break;
                                case "LIGHTING":
                                    if(split[i+1].equals("DIRECTIONAL"))
                                    {
                                        gp.directionalLighting = true;
                                    }
                                break;
                            }
                        }
                    }
                }
                
                cmd.clear();
                gp.cursor.x = 12;
            }
            
            if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            {
                if(gp.cursor.x > 20)
                {
                    gp.cursor.x -= factor * 2;
                    cmd.removeCommand();
                }
            }
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e)
    {
        
    }
    
    @Override
    public void keyReleased(KeyEvent e)
    {
        
    }
}
