
package com.ryancodesgames.apollo.gameobject;

import com.ryancodesgames.apollo.mathlib.Mesh;
import com.ryancodesgames.apollo.mathlib.Movement;
import com.ryancodesgames.apollo.mathlib.PolygonGroup;
import com.ryancodesgames.apollo.mathlib.Vec3D;

public class GameObject 
{
    private Mesh gameMesh;
    private PolygonGroup groupMesh;
    private Vec3D origin;
    private Movement moveObj;
    private int state;
    private boolean isMoving;
    
    protected static final int STATE_DESTROYED = 0;
    protected static final int STATE_MOVING = 1;
    protected static final int STATE_IDLE = 2;
    
    public GameObject(Mesh gameMesh, PolygonGroup groupMesh, Vec3D origin, Movement m)
    {
        this.gameMesh = gameMesh;
        this.groupMesh = groupMesh;
        this.origin = origin;
        this.moveObj = m;
    }
    
    public GameObject(Vec3D origin, Mesh gameMesh)
    {
        this.origin = origin;
        this.gameMesh = gameMesh;
    }
    
    //SETTER METHODS TO UPDATE INFORMATION OF THE GAMEOBJECT
    public void setMesh(Mesh input)
    {
        this.gameMesh = input;
    }
    
    public void setPolyGroup(PolygonGroup poly)
    {
        this.groupMesh = poly;
    }
    
    public void setState(int state)
    {
        this.state = state;
    }
    
    public void setMovement(Movement m)
    {
        this.moveObj = m;
    }
    //SETTING ORIGIN POINT IMPORTANT TO APPLY UNIFORM TRANSFORMATIONS TO ALL POINTS
    //OF A MESH VIA A SINGULAR ORIGIN POINT.
    public void setOrigin(Vec3D point)
    {
        this.origin = point;
    }
    
    //STATE BOOLEANS
    public boolean isDestroyed()
    {
        return state == STATE_DESTROYED;
    }
    
    public boolean isIdle()
    {
        return state == STATE_IDLE;
    }
    
    public boolean isMoving()
    {
        return state == STATE_MOVING;
    }
    //BASIC GETTERS
    public Mesh getMesh()
    {
        return gameMesh;
    }
    
    public PolygonGroup getPoly()
    {
        return groupMesh;
    }
    
    public Vec3D getOrigin()
    {
        return origin;
    }
    
    public Movement getMovement()
    {
        return moveObj;
    }
}
