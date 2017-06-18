/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.ai.navmesh.NavMesh;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.ai.navmesh.Path.Waypoint;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author Thonon
 */
public class AgentCtrl extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    
    private float speed = 8f;
    
    private NavMesh navMesh;
    
    private NavMeshPathfinder pathFinder;
    
    private Vector3f targetPosition;
    
    private boolean isPathFinded = false;
    
    private Waypoint nextWaypoint;

    public AgentCtrl(NavMesh navMesh) 
    {
        this.navMesh = navMesh;
        // création du pathfinder
        pathFinder = new NavMeshPathfinder(this.navMesh);
        //
        this.setEnabled(false);
        
    }
    
    public void goTo(Vector3f targetPosition)
    {
        // init de la position target
        this.targetPosition = targetPosition;
        // init du pathfinder
        pathFinder.setPosition(spatial.getLocalTranslation());
        // recheche du chemin
        isPathFinded = pathFinder.computePath(targetPosition);
        
        if(isPathFinded)
            this.setEnabled(true);
        
    }
    
    

    @Override
    protected void controlUpdate(float tpf) 
    {
        //TODO: add code that controls Spatial,
        //e.g. spatial.rotate(tpf,tpf,tpf);
       if(isPathFinded)
       {
          pathFinder.goToNextWaypoint();
          
          if(pathFinder.isAtGoalWaypoint())
          {
              isPathFinded = false;
              this.setEnabled(false);
              return;
          }
  
          nextWaypoint = pathFinder.getNextWaypoint();

          isPathFinded = false;
       }
       else
           
       {
           Vector3f dir = nextWaypoint.getPosition().subtract(this.getSpatial().getLocalTranslation());
           float distance = dir.length();
           if(distance > 0.2f)
           {
               dir.normalizeLocal();
               this.getSpatial().setLocalTranslation(this.getSpatial().getLocalTranslation().add(dir.mult(speed * tpf)));
           }
           else
            isPathFinded = true;
           
         
       }
        
      
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        AgentCtrl control = new AgentCtrl(this.navMesh);
        //TODO: copy parameters to new Control
        return control;
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
       
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }

  
    
}
