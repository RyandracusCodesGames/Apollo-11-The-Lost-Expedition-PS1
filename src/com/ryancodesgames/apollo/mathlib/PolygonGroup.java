
package com.ryancodesgames.apollo.mathlib;

import java.util.ArrayList;
import java.util.List;

public class PolygonGroup 
{
    private List<Mesh> polygon = new ArrayList<>();
    
    private int iteratorIndex;
   
    public void setTransform(Transformation t, int i)
    {
        polygon.get(i).transform = t;
    }
    
    public void addMesh(Mesh input)
    {
        polygon.add(input);
    }
    
    public void removeMesh(Mesh input)
    {
        polygon.remove(input);
    }
    
    public Mesh getMesh(int i)
    {
        return polygon.get(i);
    }
    
    public List<Mesh> getPolygonGroup()
    {
        return polygon;
    }
    
    public int increment()
    {
        return iteratorIndex++;
    }
    public void resetIterator()
    {
        if(iteratorIndex >= polygon.size())
        {
            iteratorIndex = 0;
        }     
    }
    
    public boolean hasNext()
    {
        return (iteratorIndex < polygon.size());
    }
    
    @Override
    public Object clone()
    {
        return polygon;
    }
   
}
