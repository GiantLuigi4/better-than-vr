package tfc.btvr.math;

//import net.minecraft.client.GLAllocation;
//import org.joml.Matrix4f;
//import org.joml.Quaternionf;
//import org.joml.Vector3f;
//import java.nio.FloatBuffer;

import net.minecraft.core.util.helper.MathHelper;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;

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
	
	protected static float _copysign(float to, float from) {
		if (from == 0) return 0;
		return Math.signum(from) * to;
	}
	
	public static Quaternion rotation(HmdMatrix34 matr) {
		// (Alternative method on https://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/)
		
		Quaternion quaternion = new Quaternion();
		
		quaternion.w = (float) Math.sqrt(Math.max(0, 1 + matr.m(0) + matr.m(5) + matr.m(10))) / 2;
		quaternion.x = (float) Math.sqrt(Math.max(0, 1 + matr.m(0) - matr.m(5) - matr.m(10))) / 2;
		quaternion.y = (float) Math.sqrt(Math.max(0, 1 - matr.m(0) + matr.m(5) - matr.m(10))) / 2;
		quaternion.z = (float) Math.sqrt(Math.max(0, 1 - matr.m(0) - matr.m(5) + matr.m(10))) / 2;
		
		quaternion.x = _copysign(quaternion.x, matr.m(9) - matr.m(6));
		quaternion.y = _copysign(quaternion.y, matr.m(2) - matr.m(8));
		quaternion.z = _copysign(quaternion.z, matr.m(4) - matr.m(1));
		
		return quaternion.normalise(quaternion);
	}
	
	public static HmdMatrix34 rotation(Quaternion q, HmdMatrix34 dst) {
		// https://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/index.htm
		
		double sqw = q.w * q.w;
		double sqx = q.x * q.x;
		double sqy = q.y * q.y;
		double sqz = q.z * q.z;
		
		// invs (inverse square length) is only required if quaternion is not already normalised
		double invs = 1 / (sqx + sqy + sqz + sqw);
		dst.m(0, (float) ((sqx - sqy - sqz + sqw) * invs)); // since sqw + sqx + sqy + sqz =1/invs*invs
		dst.m(5, (float) ((-sqx + sqy - sqz + sqw) * invs));
		dst.m(10, (float) ((-sqx - sqy + sqz + sqw) * invs));
		
		double tmp1 = q.x * q.y;
		double tmp2 = q.z * q.w;
		dst.m(4, (float) (2.0 * (tmp1 + tmp2) * invs));
		dst.m(1, (float) (2.0 * (tmp1 - tmp2) * invs));
		
		tmp1 = q.x * q.z;
		tmp2 = q.y * q.w;
		dst.m(8, (float) (2.0 * (tmp1 - tmp2) * invs));
		dst.m(2, (float) (2.0 * (tmp1 + tmp2) * invs));
		tmp1 = q.y * q.z;
		tmp2 = q.x * q.w;
		dst.m(9, (float) (2.0 * (tmp1 + tmp2) * invs));
		dst.m(6, (float) (2.0 * (tmp1 - tmp2) * invs));
		
		return dst;
	}
	
	public static double[] interpMatrix(HmdMatrix34 src, HmdMatrix34 dst, double delta) {
		Quaternion left = rotation(src);
		Quaternion right = rotation(dst);
		
		left.set(
				tfc.btvr.math.MathHelper.lerpQuat(left.x, right.x, (float) delta),
				tfc.btvr.math.MathHelper.lerpQuat(left.y, right.y, (float) delta),
				tfc.btvr.math.MathHelper.lerpQuat(left.z, right.z, (float) delta),
				tfc.btvr.math.MathHelper.lerpQuat(left.w, right.w, (float) delta)
		);
		
		rotation(left, dst);
		dst.m(3, MathHelper.lerp(src.m(3), dst.m(3), (float) delta));
		dst.m(7, MathHelper.lerp(src.m(7), dst.m(7), (float) delta));
		dst.m(11, MathHelper.lerp(src.m(11), dst.m(11), (float) delta));
		
		double[] d = new double[4 * 3];
		for (int i = 0; i < d.length; i++) {
			d[i] = dst.m(i);
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
	
	public static Matrix4f toMat4(HmdMatrix34 matrix) {
		Matrix4f matr = new Matrix4f();
		matr.m00 = matrix.m(0);
		matr.m01 = matrix.m(1);
		matr.m02 = matrix.m(2);
		matr.m03 = matrix.m(3);
		
		matr.m10 = matrix.m(4);
		matr.m11 = matrix.m(5);
		matr.m12 = matrix.m(6);
		matr.m13 = matrix.m(7);
		
		matr.m20 = matrix.m(8);
		matr.m21 = matrix.m(9);
		matr.m22 = matrix.m(10);
		matr.m23 = matrix.m(11);

//		matr.m30 = matrix.m(12);
//		matr.m31 = matrix.m(13);
//		matr.m32 = matrix.m(14);
//		matr.m33 = matrix.m(15);
		
		return matr;
	}
}
