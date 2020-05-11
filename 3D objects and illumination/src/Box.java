//****************************************************************************
//      Sphere class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//

public class Box extends Meshable
{
	private Point3D center;
	private float r;
	private int stacks,slices;
	public Mesh3D mesh_side_1;
	public Mesh3D mesh_side_2;
	public Mesh3D mesh_side_3;
	public Mesh3D mesh_side_4;
	public Mesh3D mesh_top;
	public Mesh3D mesh_bottom;
	//public Mesh3D [] meshes = new Mesh3D[3];
	
	public Box(float _x, float _y, float _z, float _r, int _stacks, int _slices)
	{
		center = new Point3D(_x,_y,_z);
		r = _r;
		stacks = _stacks;
		slices = _slices;
		meshes = new Mesh3D[6];
		meshes[0] = mesh_side_1;
		meshes[1] = mesh_side_1;
		meshes[2] = mesh_side_1;
		meshes[3] = mesh_side_1;
		meshes[4] = mesh_top;
		meshes[5] = mesh_bottom;
		initMesh();
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		
	}
	
	public void set_radius(float _r)
	{
		r = _r;
		
	}
	
	public void set_stacks(int _stacks)
	{
		stacks = _stacks;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public void set_slices(int _slices)
	{
		slices = _slices;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public int get_n()
	{
		return slices;
	}
	
	public int get_m()
	{
		return stacks;
	}

	private void initMesh()
	{
		mesh_side_1 = new Mesh3D(stacks,slices);
		mesh_side_2 = new Mesh3D(stacks,slices);
		mesh_side_3 = new Mesh3D(stacks,slices);
		mesh_side_4 = new Mesh3D(stacks,slices);
		mesh_top = new Mesh3D(stacks,slices);
		mesh_bottom = new Mesh3D(stacks,slices);
		fillMeshSide_1();  // set the mesh vertices and normals
		fillMeshSide_2();
		fillMeshSide_3();
		fillMeshSide_4();
		fillMeshTop(); 
		fillMeshBottom(); 
		meshes[0] = mesh_side_1;
		meshes[1] = mesh_side_2;
		meshes[2] = mesh_side_3;
		meshes[3] = mesh_side_4;
		meshes[4] = mesh_top;
		meshes[5] = mesh_bottom;
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	private void fillMesh(Mesh3D mesh)
	{
		// ****************Implement Code here*******************//
		int i, j;
		float theta, phi;
		float d_theta = (float)(2*Math.PI)/(float)(slices-1);
		float d_phi = (float)(Math.PI)/(float)(stacks-1);
		float cos_theta, sin_theta;
		float cos_phi, sin_phi;
		
		for(i=0, phi=(float)(-0.5*Math.PI); i<stacks; i++, phi+=d_phi) {
			cos_phi = (float)Math.cos(phi);
			sin_phi = (float)Math.sin(phi);
			
			for(j=0, theta=(float)(-Math.PI); j<slices; j++, theta+=d_theta) {
				cos_theta = (float)Math.cos(theta);
				sin_theta = (float)Math.sin(theta);
				
				mesh.v[i][j].x = center.x+r*cos_phi*cos_theta;
				mesh.v[i][j].y = center.y+r*cos_phi*sin_theta;
				mesh.v[i][j].z = center.z+r*sin_phi;
				
				mesh.n[i][j].x = cos_phi * cos_theta;
				mesh.n[i][j].y = cos_phi * sin_theta;
				mesh.n[i][j].z = sin_phi;
			}
		}
	}
	private void fillMeshSide_1() {
		int i,j;
		float d_bottom = r/(float)(slices-1);
		float d_height = r/(float)(stacks-1);
		float height, bottom;
		
		for(i=0, height=(float)(-r/2); i<stacks; i++,height+=d_height) {
			for(j=0, bottom=(float)(-r/2); j<slices; j++, bottom+=d_bottom) {
				mesh_side_1.v[i][j].x = center.x-r/2;
				mesh_side_1.v[i][j].y = center.y+height;
				mesh_side_1.v[i][j].z = center.z+bottom;
				
				mesh_side_1.n[i][j].x = 1;
				mesh_side_1.n[i][j].y = 0;
				mesh_side_1.n[i][j].z = 0;
			}
		}
		
	}
	private void fillMeshSide_2() {
		int i,j;
		float d_bottom = r/(float)(slices-1);
		float d_height = r/(float)(stacks-1);
		float height, bottom;
		
		for(i=0, height=(float)(-r/2); i<stacks; i++,height+=d_height) {
			for(j=0, bottom=(float)(-r/2); j<slices; j++, bottom+=d_bottom) {
				mesh_side_2.v[i][j].x = center.x+bottom;
				mesh_side_2.v[i][j].y = center.y-height;
				mesh_side_2.v[i][j].z = center.z-r/2;
				
				mesh_side_2.n[i][j].x = 0;
				mesh_side_2.n[i][j].y = 0;
				mesh_side_2.n[i][j].z = 1;
			}
		}
		
	}
	private void fillMeshSide_3() {
		int i,j;
		float d_bottom = r/(float)(slices-1);
		float d_height = r/(float)(stacks-1);
		float height, bottom;
		
		for(i=0, height=(float)(-r/2); i<stacks; i++,height+=d_height) {
			for(j=0, bottom=(float)(-r/2); j<slices; j++, bottom+=d_bottom) {
				mesh_side_3.v[i][j].x = center.x+r/2;
				mesh_side_3.v[i][j].y = center.y-height;
				mesh_side_3.v[i][j].z = center.z+bottom;
				
				mesh_side_3.n[i][j].x = -1;
				mesh_side_3.n[i][j].y = 0;
				mesh_side_3.n[i][j].z = 0;
			}
		}
		
	}
	private void fillMeshSide_4() {
		int i,j;
		float d_bottom = r/(float)(slices-1);
		float d_height = r/(float)(stacks-1);
		float height, bottom;
		
		for(i=0, height=(float)(-r/2); i<stacks; i++,height+=d_height) {
			for(j=0, bottom=(float)(-r/2); j<slices; j++, bottom+=d_bottom) {
				mesh_side_4.v[i][j].x = center.x+bottom;
				mesh_side_4.v[i][j].y = center.y+height;
				mesh_side_4.v[i][j].z = center.z+r/2;
				
				mesh_side_4.n[i][j].x = 0;
				mesh_side_4.n[i][j].y = 0;
				mesh_side_4.n[i][j].z = -1;
			}
		}
		
	}
	
	private void fillMeshTop() {
		int i,j;
		float d_bottom = r/(float)(slices-1);
		float d_height = r/(float)(stacks-1);
		float height, bottom;
		
		for(i=0, height=(float)(-r/2); i<stacks; i++,height+=d_height) {
			for(j=0, bottom=(float)(-r/2); j<slices; j++, bottom+=d_bottom) {
				mesh_top.v[i][j].x = center.x+bottom;
				mesh_top.v[i][j].y = center.y+r/2;
				mesh_top.v[i][j].z = center.z-height;
				
				mesh_top.n[i][j].x = 0;
				mesh_top.n[i][j].y = -1;
				mesh_top.n[i][j].z = 0;
			}
		}
		
	}
	
	private void fillMeshBottom() {
		int i,j;
		float d_bottom = r/(float)(slices-1);
		float d_height = r/(float)(stacks-1);
		float height, bottom;
		
		for(i=0, height=(float)(-r/2); i<stacks; i++,height+=d_height) {
			for(j=0, bottom=(float)(-r/2); j<slices; j++, bottom+=d_bottom) {
				mesh_bottom.v[i][j].x = center.x-bottom;
				mesh_bottom.v[i][j].y = center.y-r/2;
				mesh_bottom.v[i][j].z = center.z-height;
				
				mesh_bottom.n[i][j].x = 0;
				mesh_bottom.n[i][j].y = 1;
				mesh_bottom.n[i][j].z = 0;
			}
		}
		
	}
	
	public void rotate(Quaternion viewing_quaternion, Point3D viewing_center) {
		mesh_side_1.rotateMesh(viewing_quaternion, viewing_center);
		mesh_side_2.rotateMesh(viewing_quaternion, viewing_center);
		mesh_side_3.rotateMesh(viewing_quaternion, viewing_center);
		mesh_side_4.rotateMesh(viewing_quaternion, viewing_center);
		mesh_top.rotateMesh(viewing_quaternion, viewing_center);
		mesh_bottom.rotateMesh(viewing_quaternion, viewing_center);
	}
}