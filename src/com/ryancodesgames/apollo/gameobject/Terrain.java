
package com.ryancodesgames.apollo.gameobject;

import com.ryancodesgames.apollo.mathlib.Mesh;

public class Terrain 
{
    private Mesh meshTerrain;
    
    public Terrain(Mesh meshTerrain)
    {
        this.meshTerrain = meshTerrain;
    }
    
    public Terrain(){
        
    }
    
    public void setTerain(Mesh mesh)
    {
        this.meshTerrain = mesh;
    }
   
    public Mesh getTerrain()
    {
        return meshTerrain;
    }
}
