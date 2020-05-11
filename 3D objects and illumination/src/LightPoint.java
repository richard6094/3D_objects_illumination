//****************************************************************************
//       Infinite light source class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//
import java.util.Random;

public class LightPoint extends Light
{
	private Random rnd=new Random();
	
	public LightPoint(ColorType _c, Point3D _direction, Point3D _position)
	{
		color = new ColorType(_c);
		direction = new Point3D(_direction);
		position = new Point3D(_position); 	}
	
	// apply this light source to the vertex / normal, given material
	// return resulting color value
	// v: viewing vector
	// n: face normal
	// p: position of the object
	public ColorType applyLight(Material mat, Point3D v, Point3D n, Point3D p){
		ColorType res = new ColorType();
//		Point3D direction;
		float distance;
		distance = position.getDistance(position, p);
		
		// ****************Implement Code here*******************//
		// dot product between light direction and normal
		// light must be facing in the positive direction
		// dot <= 0.0 implies this light is facing away (not toward) this point
		// therefore, light only contributes if dot > 0.0 
		
		double dot = direction.dotProduct(n);
		float atten = radAtten(1f,1f,1f,distance)*angAtten(p,0.707f,1f);
		atten = Math.max(atten, 1);
		atten = Math.min(0.9f, atten);
		
		
		
//		color.r*=atten;
//		color.g*=atten;
//		color.b*=atten;
		
//		color.r*=0.1f;
//		color.g*=0.11f;
//		color.b*=0.999f;
		
		if(dot>0.0)
		{
			// diffuse component
			if(mat.diffuse)
			{
				res.r = (float)(dot*mat.kd.r*color.r);
				res.g = (float)(dot*mat.kd.g*color.g);
				res.b = (float)(dot*mat.kd.b*color.b);
			}
			
			// specular component
			if(mat.specular)
			{
				Point3D r = direction.reflect(n);
				dot = r.dotProduct(v);
				if(dot>0.0)
				{
					res.r += (float)(dot*mat.ks.r*Math.pow(color.r,mat.ns));
					res.g += (float)(dot*mat.ks.g*Math.pow(color.g,mat.ns));
					res.b += (float)(dot*mat.ks.b*Math.pow(color.b,mat.ns));
				}
			}
			
			// clamp so that allowable maximum illumination level is not exceeded
			res.clamp();
		}
		return(res);
	}

}
