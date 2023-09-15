package tfc.btvr.math;

//import net.minecraft.client.GLAllocation;
//import org.joml.Matrix4f;
//import org.joml.Quaternionf;
//import org.joml.Vector3f;
//import java.nio.FloatBuffer;

import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.util.vector.Matrix4f;

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
	
	public static double[] convert(HmdMatrix34 src) {
		double[] d = new double[4 * 3];
		for (int i = 0; i < d.length; i++)
			d[i] = src.m(i);
		return d;
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
	
	private static final Matrix4f matrix4f = new Matrix4f();
	private static final Matrix4f matrix4f1 = new Matrix4f();
	private static final Matrix4f dst = new Matrix4f();
	
	public static double[] mul(double[] left, double[] right) {
		boolean m34 = false;
		if (left.length != 16) {
			m34 = true;
			left = toM44(left);
		}
		
		//@formatter:off
		matrix4f.m00 = (float) left[0]; matrix4f.m01 = (float) left[1]; matrix4f.m02 = (float) left[2]; matrix4f.m03 = (float) left[3];
		matrix4f.m10 = (float) left[4]; matrix4f.m11 = (float) left[5]; matrix4f.m12 = (float) left[6]; matrix4f.m13 = (float) left[7];
		matrix4f.m20 = (float) left[8]; matrix4f.m21 = (float) left[9]; matrix4f.m22 = (float) left[10]; matrix4f.m23 = (float) left[11];
		matrix4f.m30 = (float) left[12]; matrix4f.m31 = (float) left[13]; matrix4f.m32 = (float) left[14]; matrix4f.m33 = (float) left[15];
		
		matrix4f1.m00 = (float) right[0]; matrix4f1.m01 = (float) right[1]; matrix4f1.m02 = (float) right[2]; matrix4f1.m03 = (float) right[3];
		matrix4f1.m10 = (float) right[4]; matrix4f1.m11 = (float) right[5]; matrix4f1.m12 = (float) right[6]; matrix4f1.m13 = (float) right[7];
		matrix4f1.m20 = (float) right[8]; matrix4f1.m21 = (float) right[9]; matrix4f1.m22 = (float) right[10]; matrix4f1.m23 = (float) right[11];
		matrix4f1.m30 = (float) right[12]; matrix4f1.m31 = (float) right[13]; matrix4f1.m32 = (float) right[14]; matrix4f1.m33 = (float) right[15];
		//@formatter:on
		
		Matrix4f.mul(matrix4f, matrix4f1, dst);
//		dst.load(matrix4f);
		
		left = new double[]{
				dst.m00, dst.m01, dst.m02, dst.m03,
				dst.m10, dst.m11, dst.m12, dst.m13,
				dst.m20, dst.m21, dst.m22, dst.m23,
				dst.m30, dst.m31, dst.m32, dst.m33,
		};
		
		if (m34) {
			left = toM34(left);
		}
		
		return left;
	}
	
	private static double[] toM44(double[] left) {
		return new double[]{
				left[0], left[1], left[2], left[3],
				left[4], left[5], left[6], left[7],
				left[8], left[9], left[10], left[11],
				0, 0, 0, 1
		};
	}
	
	private static double[] toM34(double[] left) {
		return new double[]{
				left[0], left[1], left[2],
				left[3], left[4], left[5],
				left[6], left[7], left[8],
				left[9], left[10], left[11],
		};
	}
	
	// https://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/index.htm
	//@formatter:off
	public static double[] quatToMat(double[] quat) {return quatToMat(quat[0], quat[1], quat[2], quat[3]);}
	public static double[] quatToMat(double x, double y, double z, double w) {
		dst.setIdentity();
		
		return new double[]{
				1 - 2 * y * y - 2 * z * z, 2 * x * y - 2 * z * w, 2 * x * z + 2 * y * w, 0,
				2 * x * y + 2 * z * w, 1 - 2 * x * x - 2 * z * z, 2 * y * z - 2 * x * w, 0,
				2 * x * z - 2 * y * w, 2 * y * z + 2 * x * w, 1 - 2 * x * x - 2 * y * y, 0,
				0, 0, 0, 1
		};
	}
	//@formatter:on
	
	// https://www.euclideanspace.com/maths/geometry/rotations/conversions/angleToQuaternion/index.htm
	public static double[] axisQuat(double angle, double x, double y, double z) {
		double s = Math.sin(angle / 2);
		return new double[]{
				x * s,
				y * s,
				z * s,
				Math.cos(angle / 2),
		};
	}
}
