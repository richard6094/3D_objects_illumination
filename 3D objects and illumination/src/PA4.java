//****************************************************************************
//       Example Main Program for CS480 PA4
//****************************************************************************
// Description: 
//   
//   This is a template program for the sketching tool.  
//
//     LEFTMOUSE: draw line segments 
//     RIGHTMOUSE: draw triangles 
//
//     The following keys control the program:
//
//		Q,q: quit 
//		C,c: clear polygon (set vertex count=0)
//		R,r: randomly change the color
//		S,s: toggle the smooth shading for triangle 
//			 (no smooth shading by default)
//		T,t: show testing examples
//		>:	 increase the step number for examples
//		<:   decrease the step number for examples
//
//****************************************************************************
// History :
//   Aug 2004 Created by Jianming Zhang based on the C
//   code by Stan Sclaroff
//   Nov 2014 modified to include test cases
//   Nov 5, 2019 Updated by Zezhou Sun
//
//	 December 5, 2019 Updated bu Ruiqi Yang


import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.*; 
import java.awt.image.*;
//import java.io.File;
//import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

//import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.FPSAnimator;//for new version of gl


public class PA4 extends JFrame
	implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{
	public static boolean l1_on = true;
	public static boolean l2_on = true;
	public static boolean l3_on = true;
	
	public static int num = 0;
	
	public static int shadeType = 0;
	
	private static final long serialVersionUID = 1L;
	private final int DEFAULT_WINDOW_WIDTH=512;
	private final int DEFAULT_WINDOW_HEIGHT=512;
	private final float DEFAULT_LINE_WIDTH=1.0f;

	private GLCapabilities capabilities;
	private GLCanvas canvas;
	private FPSAnimator animator;

	final private int numTestCase;
	private int testCase;
	private BufferedImage buff;
	@SuppressWarnings("unused")
	private ColorType color;
	private Random rng;
	
	 // specular exponent for materials
	private int ns=5; 
	
	private ArrayList<Point2D> lineSegs;
	private ArrayList<Point2D> triangles;
	private boolean doSmoothShading;
	private int Nsteps;

	/** The quaternion which controls the rotation of the world. */
    private Quaternion viewing_quaternion = new Quaternion();
    private Point3D viewing_center = new Point3D((float)(DEFAULT_WINDOW_WIDTH/2),(float)(DEFAULT_WINDOW_HEIGHT/2),(float)0.0);
    /** The last x and y coordinates of the mouse press. */
    private int last_x = 0, last_y = 0;
    /** Whether the world is being rotated. */
    private boolean rotate_world = false;
    
    /** Random colors **/
    private ColorType[] colorMap = new ColorType[100];
    private Random rand = new Random();
    
	public PA4()
	{
	    capabilities = new GLCapabilities(null);
	    capabilities.setDoubleBuffered(true);  // Enable Double buffering

	    canvas  = new GLCanvas(capabilities);
	    canvas.addGLEventListener(this);
	    canvas.addMouseListener(this);
	    canvas.addMouseMotionListener(this);
	    canvas.addKeyListener(this);
	    canvas.setAutoSwapBufferMode(true); // true by default. Just to be explicit
	    canvas.setFocusable(true);
	    getContentPane().add(canvas);

	    animator = new FPSAnimator(canvas, 60); // drive the display loop @ 60 FPS

	    numTestCase = 2;
	    testCase = 0;
	    Nsteps = 12;

	    setTitle("CS480/680 Lab 11");
	    setSize( DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	    setResizable(false);
	    
	    rng = new Random();
	    color = new ColorType(1.0f,0.0f,0.0f);
	    lineSegs = new ArrayList<Point2D>();
	    triangles = new ArrayList<Point2D>();
	    doSmoothShading = false;
	    
	    for (int i=0; i<100; i++) {
	    	this.colorMap[i] = new ColorType(i*0.005f+0.5f, i*-0.005f+1f, i*0.0025f+0.75f);
	    }
	}

	public void run()
	{
		animator.start();
	}

	public static void main( String[] args )
	{
	    PA4 P = new PA4();
	    P.run();
	}

	//*********************************************** 
	//  GLEventListener Interfaces
	//*********************************************** 
	public void init( GLAutoDrawable drawable) 
	{
	    GL gl = drawable.getGL();
	    gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f);
	    gl.glLineWidth( DEFAULT_LINE_WIDTH );
	    Dimension sz = this.getContentPane().getSize();
	    buff = new BufferedImage(sz.width,sz.height,BufferedImage.TYPE_3BYTE_BGR);
	    clearPixelBuffer();
	}

	// Redisplaying graphics
	public void display(GLAutoDrawable drawable)
	{
	    GL2 gl = drawable.getGL().getGL2();
	    WritableRaster wr = buff.getRaster();
	    DataBufferByte dbb = (DataBufferByte) wr.getDataBuffer();
	    byte[] data = dbb.getData();

	    gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
	    gl.glDrawPixels (buff.getWidth(), buff.getHeight(),
                GL2.GL_BGR, GL2.GL_UNSIGNED_BYTE,
                ByteBuffer.wrap(data));
        drawTestCase();
	}

	// Window size change
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		// deliberately left blank
	}
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
	      boolean deviceChanged)
	{
		// deliberately left blank
	}
	
	void clearPixelBuffer()
	{
		lineSegs.clear();
    	triangles.clear();
		Graphics2D g = buff.createGraphics();
	    g.setColor(Color.BLACK);
	    g.fillRect(0, 0, buff.getWidth(), buff.getHeight());
	    g.dispose();
	}
	
	// drawTest
	void drawTestCase()
	{  
		/* clear the window and vertex state */
		clearPixelBuffer();
	  
		//System.out.printf("Test case = %d\n",testCase);

		switch (testCase){
		case 0:
			shadeTest(true); /* smooth shaded, sphere and torus */
			break;
		case 1:
			shadeTest(false); /* flat shaded, sphere and torus */
			break;
		}	
	}


	//*********************************************** 
	//          KeyListener Interfaces
	//*********************************************** 
	public void keyTyped(KeyEvent key)
	{
	//      Q,q: quit 
	//      C,c: clear polygon (set vertex count=0)
	//		R,r: randomly change the color
	//		S,s: toggle the smooth shading
	//		T,t: show testing examples (toggles between smooth shading and flat shading test cases)
	//		>:	 increase the step number for examples
	//		<:   decrease the step number for examples
	//     +,-:  increase or decrease spectral exponent

	    switch ( key.getKeyChar() ) 
	    {
	    case 'Q' :
	    case 'q' : 
	    	new Thread()
	    	{
	          	public void run() { animator.stop(); }
	        }.start();
	        System.exit(0);
	        break;
	    case 'R' :
	    case 'r' :
	    	color = new ColorType(rng.nextFloat(),rng.nextFloat(),
	    			rng.nextFloat());
	    	break;
	    case 'C' :
	    case 'c' :
	    	clearPixelBuffer();
	    	break;
	    case 'K':
	    case 'k':
	    	PA4.l1_on = !PA4.l1_on;
	    case 'L':
	    case 'l':
	    	PA4.l2_on = !PA4.l2_on;
	    	break;
	    case ';':
	    	PA4.l3_on = !PA4.l3_on;
	    	
	    	break;
	    case 'S' :
	    case 's' :
	    	if(shadeType==0)
	    		shadeType=1;
	    	else if(shadeType==1)
	    		shadeType=2;
	    	else if(shadeType==2)
	    		shadeType=0;
	    	break;
	    case 'T' :
	    case 't' : 
	    	if(num==0)
	    		num=1;
	    	else if(num==1)
	    		num=2;
	    	else if(num==2)
	    		num=3;
	    	else if(num==3)
	    		num=0;
	        break; 
	    case '<':  
	        Nsteps = Nsteps < 4 ? Nsteps: Nsteps / 2;
	        System.out.printf( "Nsteps = %d \n", Nsteps);
	        drawTestCase();
	        break;
	    case '>':
	        Nsteps = Nsteps > 190 ? Nsteps: Nsteps * 2;
	        System.out.printf( "Nsteps = %d \n", Nsteps);
	        drawTestCase();
	        break;
	    case '+':
	    	ns++;
	        drawTestCase();
	    	break;
	    case '-':
	    	if(ns>0)
	    		ns--;
	        drawTestCase();
	    	break;
	    default :
	        break;
	    }
	}

	public void keyPressed(KeyEvent key)
	{
	    switch (key.getKeyCode()) 
	    {
	    case KeyEvent.VK_ESCAPE:
	    	new Thread()
	        {
	    		public void run()
	    		{
	    			animator.stop();
	    		}
	        }.start();
	        System.exit(0);
	        break;
	      default:
	        break;
	    }
	}

	public void keyReleased(KeyEvent key)
	{
		// deliberately left blank
	}

	//************************************************** 
	// MouseListener and MouseMotionListener Interfaces
	//************************************************** 
	public void mouseClicked(MouseEvent mouse)
	{
		// deliberately left blank
	}
	  public void mousePressed(MouseEvent mouse)
	  {
	    int button = mouse.getButton();
	    if ( button == MouseEvent.BUTTON1 )
	    {
	      last_x = mouse.getX();
	      last_y = mouse.getY();
	      rotate_world = true;
	    }
	  }

	  public void mouseReleased(MouseEvent mouse)
	  {
	    int button = mouse.getButton();
	    if ( button == MouseEvent.BUTTON1 )
	    {
	      rotate_world = false;
	    }
	  }

	public void mouseMoved( MouseEvent mouse)
	{
		// Deliberately left blank
	}

	/**
	   * Updates the rotation quaternion as the mouse is dragged.
	   * 
	   * @param mouse
	   *          The mouse drag event object.
	   */
	  public void mouseDragged(final MouseEvent mouse) {
	    if (this.rotate_world) {
	      // get the current position of the mouse
	      final int x = mouse.getX();
	      final int y = mouse.getY();

	      // get the change in position from the previous one
	      final int dx = x - this.last_x;
	      final int dy = y - this.last_y;

	      // create a unit vector in the direction of the vector (dy, dx, 0)
	      final float magnitude = (float)Math.sqrt(dx * dx + dy * dy);
	      if(magnitude > 0.0001)
	      {
	    	  // define axis perpendicular to (dx,-dy,0)
	    	  // use -y because origin is in upper lefthand corner of the window
	    	  final float[] axis = new float[] { -(float) (dy / magnitude),
	    			  (float) (dx / magnitude), 0 };

	    	  // calculate appropriate quaternion
	    	  final float viewing_delta = 3.1415927f / 360.0f * magnitude;
	    	  final float s = (float) Math.sin(0.5f * viewing_delta);
	    	  final float c = (float) Math.cos(0.5f * viewing_delta);
	    	  final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s * axis[2]);
	    	  this.viewing_quaternion = Q.multiply(this.viewing_quaternion);

	    	  // normalize to counteract acccumulating round-off error
	    	  this.viewing_quaternion.normalize();

	    	  // save x, y as last x, y
	    	  this.last_x = x;
	    	  this.last_y = y;
	          drawTestCase();
	      }
	    }

	  }
	  
	public void mouseEntered( MouseEvent mouse)
	{
		// Deliberately left blank
	}

	public void mouseExited( MouseEvent mouse)
	{
		// Deliberately left blank
	} 


	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}
	
	//************************************************** 
	// Test Cases
	// Nov 9, 2014 Stan Sclaroff -- removed line and triangle test cases
	//************************************************** 

	ColorType applyLights(Light [] lts, Material mat, Point3D view_vector, Point3D triangle_normal, Point3D v) {
		ColorType c = new ColorType();
		for(int i=0; i<lts.length;i++) {
			if(i==0&&PA4.l1_on==false)
				continue;
			else if(i==1&&PA4.l2_on==false) {
				continue;
			}
			ColorType l = lts[i].applyLight(mat, view_vector, triangle_normal, v);
			c = c.add(l);
		}
		
		float r = Light.ambient.r*mat.ka.r;
		float g = Light.ambient.g*mat.ka.g;
		float b = Light.ambient.b*mat.ka.b;
		
		ColorType matAmbient = new ColorType(r,g,b);
		
		if(PA4.l3_on==true)
			c = c.add(matAmbient);
		
		return c;
	}
	void drawMesh(Mesh3D mesh, int slices, int stacks, Material mat, Light[] lts, Point3D view_vector, boolean doSmooth, int shadeType) {
		//mesh(slices,stacks),view_vector,mat,lt
		//triangle_normal
		//if shade type =:
		// 0---flat
		// 1---gouraud
		// 2---phong
		
		int i, j, n, m;
		Point3D v0,v1, v2, n0, n1, n2;
		Point3D triangle_normal = new Point3D();
		Point2D[] tri = {new Point2D(), new Point2D(), new Point2D()};
		n = slices;
		m = stacks;
				
		// draw triangles for the current surface, using vertex colors
		for(i=0; i < m-1; ++i)
	    {
			for(j=0; j < n-1; ++j)
			{
				v0 = mesh.v[i][j];
				v1 = mesh.v[i][j+1];
				v2 = mesh.v[i+1][j+1];
				
				triangle_normal = computeTriangleNormal(v0,v1,v2);
				
				if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
				{	
					
					n2 = n1 = n0 =  triangle_normal;
					if(shadeType == 0) {
						// flat shading: use the normal to the triangle itself
						tri[0].c = applyLights(lts,mat,view_vector,triangle_normal,v0);
						tri[1].c = applyLights(lts,mat,view_vector,triangle_normal,v1);
						tri[2].c = applyLights(lts,mat,view_vector,triangle_normal,v2);
					}
					else if(shadeType==1) {
						//Gouraud shading
						tri[0].c = applyLights(lts,mat,view_vector,mesh.n[i][j],v0);
						tri[1].c = applyLights(lts,mat,view_vector,mesh.n[i][j+1],v1);
						tri[2].c = applyLights(lts,mat,view_vector,mesh.n[i+1][j+1],v2);
					}
					
					
//					tri[2].c = tri[1].c = tri[0].c = lt.applyLight(mat, view_vector, triangle_normal, v2);
					

					tri[0].x = (int)v0.x;
					tri[0].y = (int)v0.y;
					tri[1].x = (int)v1.x;
					tri[1].y = (int)v1.y;
					tri[2].x = (int)v2.x;
					tri[2].y = (int)v2.y;

//					SketchBase.drawTriangle(buff,tri[0],tri[1],tri[2],doSmooth); 
					if(shadeType==2) {
						//Phong
						SketchBase.drawTriangle3DPhong(buff, v0, v1, v2, mesh.n[i][j], mesh.n[i][j+1], mesh.n[i+1][j+1], lts, mat, view_vector, doSmooth);
					}
					else {
						SketchBase.drawTriangle3D(buff,v0,v1,v2,tri[0].c,tri[1].c,tri[2].c,doSmooth);
					}
					
				}
				
				v0 = mesh.v[i][j];
				v1 = mesh.v[i+1][j+1];
				v2 = mesh.v[i+1][j];
				
				triangle_normal = computeTriangleNormal(v0,v1,v2);
				
				if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
				{	
					// flat shading: use the normal to the triangle itself
					n2 = n1 = n0 =  triangle_normal;
					if(shadeType == 0) {
						// flat shading: use the normal to the triangle itself
						tri[0].c = applyLights(lts,mat,view_vector,triangle_normal,v0);
						tri[1].c = applyLights(lts,mat,view_vector,triangle_normal,v1);
						tri[2].c = applyLights(lts,mat,view_vector,triangle_normal,v2);
					}
					else if(shadeType==1) {
						//Gouraud shading
						tri[0].c = applyLights(lts,mat,view_vector,mesh.n[i][j],v0);
						tri[1].c = applyLights(lts,mat,view_vector,mesh.n[i+1][j+1],v1);
						tri[2].c = applyLights(lts,mat,view_vector,mesh.n[i+1][j],v2);
					}
//					tri[2].c = tri[1].c = tri[0].c = lt.applyLight(mat, view_vector, triangle_normal, v2);
		
					tri[0].x = (int)v0.x;
					tri[0].y = (int)v0.y;
					tri[1].x = (int)v1.x;
					tri[1].y = (int)v1.y;
					tri[2].x = (int)v2.x;
					tri[2].y = (int)v2.y;
					
//					SketchBase.drawTriangle(buff,tri[0],tri[1],tri[2],doSmooth);     
					if(shadeType==2) {
						//Phong
						SketchBase.drawTriangle3DPhong(buff, v0, v1, v2, mesh.n[i][j], mesh.n[i][j+1], mesh.n[i+1][j+1], lts, mat, view_vector, doSmooth);
					}
					else {
						SketchBase.drawTriangle3D(buff,v0,v1,v2,tri[0].c,tri[1].c,tri[2].c,doSmooth);
					}
				}
			}	
	    }
	}
	void shadeTest(boolean doSmooth){
		// the simple example scene includes one sphere and one torus
		Scene scene_chosen;
		
		float radius = (float)50.0;
        Sphere3D sphere = new Sphere3D((float)256.0, (float)256.0, (float)128.0, (float)1.5*radius, Nsteps, Nsteps);
        Ellipsoid ep = new Ellipsoid((float)256.0, (float)256.0, (float)128.0, (float)1.5*radius, Nsteps, Nsteps);
        Cylinder cd = new Cylinder((float)128.0, (float)128.0, (float)128.0, (float)1.5*radius, (float)300, Nsteps, Nsteps);
        Box bx = new Box((float)128.0, (float)128.0, (float)128.0, (float)1.5*radius, Nsteps, Nsteps);
        Torus to = new Torus((float)384.0, (float)384.0, (float)128.0, (float)1.5*radius, (float)0.5*radius, Nsteps, Nsteps);
        SuperEllipsoid se1 = new SuperEllipsoid((float)256.0, (float)256.0, (float)128.0, (float)5, (float)1, 1*radius ,1*radius ,1*radius, Nsteps, Nsteps);
        SuperEllipsoid se2 = new SuperEllipsoid((float)256.0, (float)256.0, (float)128.0, (float)1, (float)5, 1*radius ,1*radius ,1*radius, Nsteps, Nsteps);
        Torus to2 = new Torus((float)384.0, (float)384.0, (float)256.0, (float)1.5*radius, (float)0.5*radius, Nsteps, Nsteps);
        SuperEllipsoid se3 = new SuperEllipsoid((float)128.0, (float)128.0, (float)128.0, (float)3, (float)5, 1*radius ,1*radius ,1*radius, Nsteps, Nsteps);
        
        Meshable [] meshes1 = {se3,to2,se2};
        Meshable [] meshes2 = {bx,to,se1};
        Meshable [] meshes3 = {bx,to,ep};
        Meshable [] meshes4 = {bx,to,sphere};
        
        
        
        Light[] lts = new Light[2];
        lts[0] = new LightInfinite(new ColorType(1.0f, 1.0f, 1.0f), new Point3D(1.0f, 0f, 0f));
		lts[1] = new LightPoint(new ColorType(255, 105, 180), new Point3D(0f, 0f, 1.0f),new Point3D(-256f, 0, 0));
        
		Material mat = new Material(new ColorType(1f, 1f, 1f), new ColorType(1.0f, 1.1f, 0), new ColorType(0, 1.0f, 1.0f), 1);
		
        // view vector is defined along z axis
        // this example assumes simple othorgraphic projection
        // view vector is used in 
        //   (a) calculating specular lighting contribution
        //   (b) backface culling / backface rejection
        Point3D view_vector = new Point3D((float)0.0,(float)0.0,(float)1.0);
        
        Scene scene1 = new Scene(meshes1, lts, shadeType, view_vector, Nsteps, Nsteps, mat);
        Scene scene2 = new Scene(meshes2, lts, shadeType, view_vector, Nsteps, Nsteps, mat);
        Scene scene3 = new Scene(meshes3, lts, shadeType, view_vector, Nsteps, Nsteps, mat);
        Scene scene4 = new Scene(meshes4, lts, shadeType, view_vector, Nsteps, Nsteps, mat);
        
        if(num==0) {
        	scene_chosen = scene1;
        }
        else if(num==1) {
        	scene_chosen = scene2;
        }
        else if(num==2){
        	scene_chosen = scene3;
        }
        else {
        	scene_chosen = scene4;
        }
        
        
		
		DepthBuffer.initBufferMap(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
		
		
		scene_chosen.rotateMeshes(viewing_quaternion, viewing_center);
		for(int i=0; i<scene_chosen.meshes.length;i++) {
			drawMeshes(scene_chosen.meshes[i], scene_chosen.slices, scene_chosen.stacks, scene_chosen.mat, scene_chosen.lts,
					scene_chosen.view_vector, doSmooth, scene_chosen.shadeType);
		}

		
		
		DepthBuffer.initBufferMap();
	}

	// helper method that computes the unit normal to the plane of the triangle
	// degenerate triangles yield normal that is numerically zero
	public void drawMeshes(Meshable shape, int slices, int stacks, Material mat, Light[] lts, Point3D view_vector, boolean doSmooth, int shadeType) {
		for(int i=0; i<shape.meshes.length; i++) {
			drawMesh(shape.meshes[i],slices,stacks,mat,lts,view_vector,doSmooth, shadeType);
		}
	}
	private Point3D computeTriangleNormal(Point3D v0, Point3D v1, Point3D v2)
	{
		Point3D e0 = v1.minus(v2);
		Point3D e1 = v0.minus(v2);
		Point3D norm = e0.crossProduct(e1);
		
		if(norm.magnitude()>0.000001)
			norm.normalize();
		else 	// detect degenerate triangle and set its normal to zero
			norm.set((float)0.0,(float)0.0,(float)0.0);

		return norm;
	}

}