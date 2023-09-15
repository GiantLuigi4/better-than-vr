package tfc.btvr.math;

//import net.minecraft.client.GLAllocation;
//import org.joml.Matrix4f;
//import org.joml.Quaternionf;
//import org.joml.Vector3f;
//import java.nio.FloatBuffer;

import org.lwjgl.openvr.HmdMatrix34;

public class MatrixHelper {
	public static void mulMatr(
			double vx, double vy, double vz,
			
			double m00, double m01, double m02, double m03,
			double m10, double m11, double m12, double m13,
			double m20, double m21, double m22, double m23,
			
			double[] dst
	) {
		dst[0] = vx * m00 + vy * m01 + vz * m02 + m03;
		dst[1] = vx * m10 + vy * m11 + vz * m12 + m13;
		dst[2] = vx * m20 + vy * m21 + vz * m22 + m23;
	}
	
	public static double[] interpMatrix(HmdMatrix34 src, HmdMatrix34 dst, double delta) {
		// TODO: https://docs.google.com/viewer?url=http://www.cs.wisc.edu/graphics/Courses/838-s2002/Papers/polar-decomp.pdf ?
		// https://github.com/WebKit/webkit/blob/main/Source/WebCore/platform/graphics/transforms/TransformationMatrix.cpp
		double[] d = new double[4 * 3];
		for (int i = 0; i < d.length; i++) {
			d[i] = src.m(i) * (1 - delta) + dst.m(i) * delta;
		}
		
		return d;
	}
}
