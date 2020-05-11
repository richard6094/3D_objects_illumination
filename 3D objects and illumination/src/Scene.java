
public class Scene {
	
	Meshable [] meshes;
	Light [] lts;
	int shadeType = 0;
	Point3D view_vector = new Point3D((float)0.0,(float)0.0,(float)1.0);
	int slices, stacks;
	Material mat;

	public Scene(Meshable [] meshes, Light [] lts, int shadeType, Point3D view_vector, int slices, int stacks, Material mat) {
		this.meshes = meshes;
		this.lts = lts;
		this.shadeType = shadeType;
		this.view_vector = view_vector;
		this.slices = slices;
		this.stacks = stacks;
		this.mat = mat;
	}
	
	public void rotateMeshes(Quaternion viewing_quaternion, Point3D viewing_center) {
		for(int i=0; i<this.meshes.length;i++) {
			meshes[i].rotate(viewing_quaternion, viewing_center);
		}
	}
	
	

}
