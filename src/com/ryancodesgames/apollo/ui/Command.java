
package com.ryancodesgames.apollo.ui;

import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Command 
{
   private final int[] keyCodes = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77,
   78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 32, 8, 46};
   
   private final Character[] cmd = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N',
   'O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9',' ', ' ', '.'};
   
   public HashMap<Integer, Character> codes = new HashMap<>();
   
   private List<Character> addCommand = new ArrayList<>();
   
   private List<String> commandList = new ArrayList<>();
   
   public Command()
   {
       for(int i = 0; i < keyCodes.length; i++)
       {
           codes.put(keyCodes[i],cmd[i]);
       }
       
       try
       {
           File file = new File("commands.txt");
           Scanner inputStream = new Scanner(file);
           
           String readLine;
           while(inputStream.hasNextLine())
           {
               readLine = inputStream.nextLine();
               commandList.add(readLine);
           }
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }
   }
   
   public void addCommand(char c)
   {
       addCommand.add(c);
   }
   
   public void removeCommand()
   {
       addCommand.remove(addCommand.size()-1);
       addCommand.remove(addCommand.size()-1);
   }
   
   public void clear()
   {
       addCommand.clear();
   }
   
   public HashMap getCodes()
   {
       return codes;
   }
   
   public List<String> getCommandList()
   {
       return commandList;
   }
   
   public List<Character> getCommand()
   {
       return addCommand;
   }
   
  
}
