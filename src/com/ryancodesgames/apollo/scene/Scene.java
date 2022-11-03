
package com.ryancodesgames.apollo.scene;

import com.ryancodesgames.apollo.camera.Camera;
import com.ryancodesgames.apollo.mathlib.Mesh;
import com.ryancodesgames.apollo.mathlib.PolygonGroup;
import java.util.List;

public class Scene 
{
    private PolygonGroup polygon;
    private List<Camera> cameras;
    private List<Mesh> meshes;
    private Animation animation;
    
    public Scene(PolygonGroup polygon, Animation animation)
    {
        this.polygon = polygon;
        this.animation = animation;
    }
    
    public void addCamera(Camera camera)
    {
        cameras.add(camera);
    }
    
    public void removeCamera(Camera camera)
    {
        cameras.remove(camera);
    }
    
    public void addMesh(Mesh mesh)
    {
        meshes.add(mesh);
    }
    
    public void removeMesh(Mesh mesh){
        meshes.remove(mesh);
    }
    
    public PolygonGroup getScenePolygonGroup()
    {
        return polygon;
    }  
}
