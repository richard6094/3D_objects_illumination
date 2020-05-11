//****************************************************************************
//       material class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//   Nov 19, 2019 Modified by Zezhou Sun

public class Material 
{
	public ColorType ka, kd, ks;
	public int ns;
	public boolean specular, diffuse, ambient, attenuation;
	
	public Material(ColorType _ka, ColorType _kd, ColorType _ks, int _ns)
	{
		ks = new ColorType(_ks);  // specular coefficient for r,g,b
		ka = new ColorType(_ka);  // ambient coefficient for r,g,b
		kd = new ColorType(_kd);  // diffuse coefficient for r,g,b
		ns = _ns;  // specular exponent
		
		// set boolean variables 
		specular = (ns>0 && (ks.r > 0.0 || ks.g > 0.0 || ks.b > 0.0));
		diffuse = (kd.r > 0.0 || kd.g > 0.0 || kd.b > 0.0);
		ambient = (ka.r > 0.0 || ka.g > 0.0 || ka.b > 0.0);
		attenuation = true;
	}
	
	public Material(Material mat) {
		ka = new ColorType(mat.ka);
		kd = new ColorType(mat.kd);
		ks = new ColorType(mat.ks);
		ns = mat.ns;
		specular = mat.specular;
		diffuse = mat.diffuse;
		ambient = mat.ambient;
	}
	
	public Material copy() {
		return new Material(ka, kd, ks, ns);
	}
}