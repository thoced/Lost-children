package mygame;

import com.jme3.ai.navmesh.NavMesh;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.List;



/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ActionListener  {

  private BulletAppState bulletAppState;
  private RigidBodyControl landscape;
  private RigidBodyControl landscape2;
  private CharacterControl player;
  private Vector3f walkDirection = new Vector3f();
  private boolean left = false, right = false, up = false, down = false,sLeft = false,sRight = false;
  private boolean isCrounch = false;
   private Vector3f camDir = new Vector3f();
  private Vector3f camLeft = new Vector3f();
  private Vector3f camAxe = new Vector3f();
  
  // capsule shae
  CapsuleCollisionShape capsuleDebout;
  CapsuleCollisionShape capsuleAccroupi;
  
  // NavMesh
  private NavMesh navMesh;
  private AgentCtrl agent;
  
  // Node
  private Spatial sceneModel;
   
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        
        this.getFlyByCamera().setMoveSpeed(6f);
        
        this.setUpKeys();

      /*  Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);*/
      
              /** Load a model. Uses model and texture from jme3-test-data library! */ 
        sceneModel = (Node)assetManager.loadModel("Scenes/scene05.j3o");
        //sceneModel.setLocalScale(4f);
        
        // creation de la physique
        bulletAppState = new BulletAppState();
        this.stateManager.attach(bulletAppState);
        
        try
        {
        // creation de la physique de la scene
            CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape((Spatial)((Node)sceneModel).getChild("staticMesh"));
                if(sceneShape != null)
                {
                    landscape = new RigidBodyControl(sceneShape, 0);
                    sceneModel.addControl(landscape);
                    bulletAppState.getPhysicsSpace().add(landscape);
                 }
        }catch(IllegalArgumentException iae)
        {
            
        }
         
       try
       {
         CollisionShape sceneShape2 =
            CollisionShapeFactory.createBoxShape((Spatial)((Node)sceneModel).getChild("complexMesh"));
         
                if(sceneShape2 != null)
                {
                 landscape2 = new RigidBodyControl(sceneShape2,0);
                 sceneModel.addControl(landscape2);
                 bulletAppState.getPhysicsSpace().add(landscape2);
                }
       }
       catch(IllegalArgumentException iae)
       {
           
       }
         
       
        
      
       
       
       
       
        // physique du personnage
        capsuleDebout = new CapsuleCollisionShape(1f, 5f, 1);
        capsuleAccroupi = new CapsuleCollisionShape(1.5f,3f,1);
        
        player = new CharacterControl(capsuleDebout, 0.05f);
        player.setJumpSpeed(28);
        player.setFallSpeed(30);
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(-0,25, 0));
        player.setApplyPhysicsLocal(true);
        
        
        this.rootNode.attachChild(sceneModel);
    
      
        bulletAppState.getPhysicsSpace().add(player);
        
     /*   Spatial dyn = (Spatial) ((Node)sceneModel).getChild("dynamicrock");
        PhysicsControl phy = dyn.getControl(PhysicsControl.class);
       bulletAppState.getPhysicsSpace().add(dyn);*/
        
        
        // init NavMesh
         Node n = (Node)sceneModel;
        this.setNavMesh(n);
        // init agent
       //  this.setAgent();
       
     // attachement du control aggent au monstre
     Spatial monster = ((Node)sceneModel).getChild("monster");
     if(monster != null)
     {
         agent = new AgentCtrl(navMesh);
         // attachement de l'agent
         monster.addControl(agent);
     }
      
     
    }

    @Override
    public void simpleUpdate(float tpf) 
    {
        camDir.set(cam.getDirection()).multLocal(0.2f);
        camLeft.set(cam.getLeft()).multLocal(0.2f);
        walkDirection.set(0, 0, 0);
        camAxe.set(0,0,0);
        
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        if(sLeft)
        {
             camAxe.addLocal(camLeft.mult(6f));
        }
        if(sRight)
        {
            camAxe.addLocal(camLeft.mult(6f).negate());
        }
        
         // modif axes
        
        
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation().add(camAxe));
        player.update(tpf);
        
       
     
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private void setNavMesh(Node scene)
    {
        // récupération du mesh
        Spatial geo = scene.getChild("chemin");
        if(geo != null)
        {
            // chargement du navmesh
            Mesh mesh = ((Geometry)geo).getMesh();
            navMesh  = new NavMesh(mesh);
            
            NavMeshPathfinder f = new NavMeshPathfinder(navMesh);
            
            
        }
        else
        {
            System.out.println("Erreur de chargement du NavMesh");
        }
    }
    
    private void setAgent()
    {
        Box b = new Box(2, 2, 2);
        Geometry geom = new Geometry("Box", b);
        
        geom.setLocalTranslation(new Vector3f(20,2,0));
        
          Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
        geom.setMaterial(mat);                   // set the cube's material
        rootNode.attachChild(geom);              // make the cube appear in the scene
        
        // init ctr
        AgentCtrl agent = new AgentCtrl(navMesh);
        geom.addControl(agent);
        
        agent.goTo(new Vector3f(-20,2,0));
        
        
        
    }
    
    private void setUpKeys() 
    {
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_Q));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_Z));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addMapping("Crounch", new KeyTrigger(KeyInput.KEY_LCONTROL));
    inputManager.addMapping("Stref_right", new KeyTrigger(KeyInput.KEY_E));
    inputManager.addMapping("Stref_left", new KeyTrigger(KeyInput.KEY_A));
    
    // clic
    inputManager.addMapping("Clic", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
    inputManager.addListener(this, "Crounch");
    inputManager.addListener(this, "Stref_right");
    inputManager.addListener(this, "Stref_left");
    
    // listener pour le clic
    inputManager.addListener(this, "Clic");
}

    @Override
    public void onAction(String binding, boolean value, float tpf)
    {
        if (binding.equals("Left")) 
        {
      if (value) { left = true; } else { left = false; }
    } else if (binding.equals("Right")) {
      if (value) { right = true; } else { right = false; }
    } else if (binding.equals("Up")) {
      if (value) { up = true; } else { up = false; }
    } else if (binding.equals("Down")) {
      if (value) { down = true; } else { down = false; }
    } else if (binding.equals("Jump") && value) {
      player.jump();
      
    }
    else if(binding.equals("Crounch") && !isCrounch)
    {
       //player.setPhysicsLocation(player.getPhysicsLocation().subtract(new Vector3f(0,10,0)));
      // player.getCollisionShape().setScale(new Vector3f(1,0.5f,1));
        
       player.setCollisionShape(capsuleAccroupi);
     
       isCrounch = !isCrounch;
    }
    else 
        if(binding.equals("Stref_left"))
    {
         if (value)
             sLeft = true;
         else
             sLeft = false;
    }
    else
        if(binding.equals("Stref_right"))
        {
            if(value)
                sRight = true;
            else
                sRight = false;
        }
        
    // clic
    if(binding.equals("Clic"))
    {
         CollisionResults results = new CollisionResults();
         
          Ray ray = new Ray(cam.getLocation(), cam.getDirection());
          
          sceneModel.collideWith(ray, results);
          
          if(results.size() > 0)
          {
              System.out.println("collision !!");
              agent.goTo(results.getClosestCollision().getContactPoint());
          }
          else
              System.out.println("No collision");
         
    }
        
        
        
    }
}
