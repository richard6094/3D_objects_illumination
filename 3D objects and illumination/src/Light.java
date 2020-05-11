
abstract public class Light {
	public static ColorType ambient = new ColorType(5 ,5 ,22);
	public Point3D direction;
	public ColorType color;
	public Point3D position;
	
	public abstract ColorType applyLight(Material mat, Point3D v, Point3D n, Point3D p);
	
	public static Point3D normal(Point3D direction) {
		float x, y ,z, _x, _y ,_z;
		x = direction.x;
		y = direction.y;
		z = direction.z;
		double sum = Math.sqrt(x*x+y*y+z*z);
		
		_x = (float)(x/sum);
		_y = (float)(y/sum);
		_z = (float)(z/sum);
		
		Point3D result = new Point3D(_x,_y,_z);
		
		return result;
		
	}
	
	
	public float radAtten(float a0, float a1, float a2, float d) {
		if(this instanceof LightInfinite==true) {
			return 1.0f;
		}
		else {
			return (float)(1/(a0+a1*d+a2*d*d));
		}
	}
	
	public float angAtten(Point3D obj, float cosMax, float coe) {
		float cosObj;
		Point3D directionObj;
		if(this instanceof LightPoint==false) {
			return 1.0f;
		}
		directionObj = new Point3D(obj.x-position.x, obj.y-position.y, obj.z-position.z);
		directionObj = Light.normal(directionObj);
		cosObj = directionObj.getCos(directionObj, Light.normal(direction));
		
		if(cosObj<cosMax)
			return 0;
		else {
			return (float)(Math.pow(directionObj.dotProduct(direction),coe));
		}
	}
	
	public static ColorType applyLights(Light [] lts, Material mat, Point3D view_vector, Point3D triangle_normal, Point3D v) {
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
//		c = lts[0].applyLight(mat, view_vector, triangle_normal, v);
		
		float r = ambient.r*mat.ka.r;
		float g = ambient.g*mat.ka.g;
		float b = ambient.b*mat.ka.b;
		
		ColorType matAmbient = new ColorType(r,g,b);
		
		if(PA4.l3_on==true)
			c = c.add(matAmbient);
		
		return c;
	}
}
