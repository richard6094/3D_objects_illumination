//****************************************************************************
//      Sphere class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//

public class Cylinder extends Meshable
{
	private Point3D center;
	private float r;
	private float d;
	private int stacks,slices;
	public Mesh3D mesh_side;
	public Mesh3D mesh_top;
	public Mesh3D mesh_bottom;
	//public Mesh3D [] meshes = new Mesh3D[3];
	
	public Cylinder(float _x, float _y, float _z, float _r, float _d, int _stacks, int _slices)
	{
		center = new Point3D(_x,_y,_z);
		r = _r;
		d = _d;
		stacks = _stacks;
		slices = _slices;
		meshes = new Mesh3D[3];
		meshes[0] = mesh_side;
		meshes[1] = mesh_top;
		meshes[2] = mesh_bottom;
		initMesh();
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		fillMeshSide();  // update the triangle mesh
	}
	
	public void set_radius(float _r)
	{
		r = _r;
		fillMeshSide(); // update the triangle mesh
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
		mesh_side = new Mesh3D(stacks,slices);
		mesh_top = new Mesh3D(stacks,slices);
		mesh_bottom = new Mesh3D(stacks,slices);
		fillMeshSide();  // set the mesh vertices and normals
		fillMeshTop(); 
		fillMeshBottom(); 
		meshes[0] = mesh_side;
		meshes[1] = mesh_top;
		meshes[2] = mesh_bottom;
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
	
	private void fillMeshSide() {
		int i,j;
		float theta, height;
		float d_theta = (float)(2*Math.PI)/(float)(slices-1);
		float d_height = (float)d/(float)(stacks-1);
		float cos_theta, sin_theta;
		for(i=0, height=(float)(-d/2); i<stacks; i++, height+=d_height) {
			for(j=0, theta=(float)(-Math.PI); j<slices; j++, theta+=d_theta) {
				cos_theta = (float)Math.cos(theta);
				sin_theta = (float)Math.sin(theta);
				
				mesh_side.v[i][j].x = center.x + r*cos_theta;
				mesh_side.v[i][j].y = center.y + r*sin_theta;
				mesh_side.v[i][j].z = center.z + height;
				
				mesh_side.n[i][j].x = -cos_theta;
				mesh_side.n[i][j].y = -sin_theta;
				mesh_side.n[i][j].z = 0;
			}
		}
	}
	private void fillMeshTop() {
		int i,j;
		float theta, radium;
		float d_theta = (float)(2*Math.PI)/(float)(slices-1);
		float d_radium = (float)r/(float)(stacks-1);
		float cos_theta, sin_theta;
		for(i=0, radium=(float)r; i<stacks; i++, radium-=d_radium) {
			for(j=0, theta=(float)(-Math.PI); j<slices; j++, theta+=d_theta) {
				cos_theta = (float)Math.cos(theta);
				sin_theta = (float)Math.sin(theta);
				
				mesh_top.v[i][j].x = center.x + radium*cos_theta;
				mesh_top.v[i][j].y = center.y + radium*sin_theta;
				mesh_top.v[i][j].z = center.z + d/2;
				
				mesh_top.n[i][j].x = 0;
				mesh_top.n[i][j].y = 0;
				mesh_top.n[i][j].z = -1;
			}
		}
	}
	private void fillMeshBottom() {
		int i,j;
		float theta, radium;
		float d_theta = (float)(2*Math.PI)/(float)(slices-1);
		float d_radium = (float)r/(float)(stacks-1);
		float cos_theta, sin_theta;
		for(i=0, radium=(float)0; i<stacks; i++, radium+=d_radium) {
			for(j=0, theta=(float)(-Math.PI); j<slices; j++, theta+=d_theta) {
				cos_theta = (float)Math.cos(theta);
				sin_theta = (float)Math.sin(theta);
				
				mesh_bottom.v[i][j].x = center.x + radium*cos_theta;
				mesh_bottom.v[i][j].y = center.y + radium*sin_theta;
				mesh_bottom.v[i][j].z = center.z - d/2;
				
				mesh_bottom.n[i][j].x = 0;
				mesh_bottom.n[i][j].y = 0;
				mesh_bottom.n[i][j].z = 1;
			}
		}
	}
	
	public void rotate(Quaternion viewing_quaternion, Point3D viewing_center) {
		mesh_side.rotateMesh(viewing_quaternion, viewing_center);
		mesh_top.rotateMesh(viewing_quaternion, viewing_center);
		mesh_bottom.rotateMesh(viewing_quaternion, viewing_center);
	}
}