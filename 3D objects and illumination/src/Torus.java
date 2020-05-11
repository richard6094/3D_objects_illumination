//****************************************************************************
//      Sphere class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//

public class Torus extends Meshable
{
	private Point3D center;
	private float R, r;
	private int stacks,slices;
	public Mesh3D mesh;
	
	public Torus(float _x, float _y, float _z, float _R, float _r, int _stacks, int _slices)
	{
		center = new Point3D(_x,_y,_z);
		R = _R;
		r = _r;
		stacks = _stacks;
		slices = _slices;
		meshes = new Mesh3D[1];
		meshes[0] = mesh;
		initMesh();
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		fillMesh(meshes[0]);  // update the triangle mesh
	}
	
	public void set_radius(float _r)
	{
		r = _r;
		fillMesh(meshes[0]); // update the triangle mesh
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
		meshes[0] = new Mesh3D(stacks,slices);
		fillMesh(meshes[0]);  // set the mesh vertices and normals
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	private void fillMesh(Mesh3D mesh)
	{
		// ****************Implement Code here*******************//
		int i, j;
		float theta, phi;
		float d_theta = (float)(2*Math.PI)/(float)(slices-1);
		float d_phi = (float)(2*Math.PI)/(float)(stacks-1);
		float cos_theta, sin_theta;
		float cos_phi, sin_phi;
		
		
		for(i=0, theta=(float)(-Math.PI); i<slices; i++, theta+=d_theta) {
			cos_theta = (float)Math.cos(theta);
			sin_theta = (float)Math.sin(theta);
			
			for(j=0, phi=(float)(-Math.PI); j<stacks; j++, phi+=d_phi) {
				cos_phi = (float)Math.cos(phi);
				sin_phi = (float)Math.sin(phi);
				
				mesh.v[j][i].x = center.x+(R+r*cos_phi)*cos_theta;
				mesh.v[j][i].y = center.y+(R+r*cos_phi)*sin_theta;
				mesh.v[j][i].z = center.z+r*sin_phi;
				
				
				float n1_x = -r*sin_phi*cos_theta; 
				float n1_y = -r*sin_phi*sin_theta; 
				float n1_z = r*cos_phi; 
				Point3D p1 = new Point3D(n1_x, n1_y, n1_z);
				
				float n2_x = -(R+r*cos_phi)*sin_theta; 
				float n2_y =  (R+r*cos_phi)*cos_theta; 
				float n2_z =  0;
				Point3D p2 = new Point3D(n2_x, n2_y, n2_z);
				
				mesh.n[j][i] = p1.crossProduct(p2);
				mesh.n[j][i] = Light.normal(mesh.n[j][i]);
				
				
			}
		}
	}
	public void rotate(Quaternion viewing_quaternion, Point3D viewing_center) {
		meshes[0].rotateMesh(viewing_quaternion, viewing_center);
	}
}