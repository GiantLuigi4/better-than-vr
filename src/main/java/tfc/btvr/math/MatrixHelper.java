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
	
//	private static float smoothInterp(double x, double y, double delta) {
//		x = ((x / Math.PI) + 1) / 2;
//		y = ((y / Math.PI) + 1) / 2;
//		if (x < 0.5 && y > 0.5)
//			if (Math.abs(x - y) > 0.5f)
//				x += 1;
//		if (y < 0.5 && x > 0.5)
//			if (Math.abs(y - x) > 0.5f)
//				y += 1;
//		x = ((x * 2) - 1) * Math.PI;
//		y = ((y * 2) - 1) * Math.PI;
//
//		return (float) (x * delta + y * (1 - delta));
//	}
//	private static Quaternionf interpQuat(Quaternionf q4fOld, Quaternionf q4f, double delta) {
//		Quaternionf q = new Quaternionf();
//		q.set(q4fOld.w, q4fOld.x, q4fOld.y, q4fOld.z);
//		Vector3f euler = q.getEulerAnglesXYZ(new org.joml.Vector3f());
//		q.mul(
//				new Quaternionf()
//						.rotateX(euler.x)
//						.rotateY(euler.y)
//						.rotateZ(euler.z)
//						.invert()
//		);
//		float[] rem = new float[]{q.x, q.y, q.z, q.w};
//
//		q.set(q4f.w, q4f.x, q4f.y, q4f.z);
//		Vector3f eulerNew = q.getEulerAnglesXYZ(new org.joml.Vector3f());
//		q.mul(
//				new Quaternionf()
//						.rotateX(eulerNew.x)
//						.rotateY(eulerNew.y)
//						.rotateZ(eulerNew.z)
//						.invert()
//		);
//		float[] remNew = new float[]{q.x, q.y, q.z, q.w};
//
//		q.set(0, 0, 0, 1)
//				.rotateX(smoothInterp(eulerNew.x, euler.x, delta))
//				.rotateY(smoothInterp(eulerNew.y, euler.y, delta))
//				.rotateZ(smoothInterp(eulerNew.z, euler.z, delta))
//				.mul(new Quaternionf(
//						smoothInterp(rem[0], remNew[0], delta),
//						smoothInterp(rem[1], remNew[1], delta),
//						smoothInterp(rem[2], remNew[2], delta),
//						smoothInterp(rem[3], remNew[3], delta)
//				))
//				.normalize();
//
//		return q;
//	}
//
//	public static float[][] matrixTo2dFloatArray(HmdMatrix34 matrix34) {
//		float[][] floats = new float[3][4];
//		floats[0][0] = matrix34.m(0);
//		floats[0][1] = matrix34.m(1);
//		floats[0][2] = matrix34.m(2);
//		floats[0][3] = matrix34.m(3);
//		floats[1][0] = matrix34.m(4);
//		floats[1][1] = matrix34.m(5);
//		floats[1][2] = matrix34.m(6);
//		floats[1][3] = matrix34.m(7);
//		floats[2][0] = matrix34.m(8);
//		floats[2][1] = matrix34.m(9);
//		floats[2][2] = matrix34.m(10);
//		floats[2][3] = matrix34.m(11);
//		return floats;
//	}
	
	public static double[] interpMatrix(HmdMatrix34 src, HmdMatrix34 dst, double delta) {
//		float[][] matrix = matrixTo2dFloatArray(src);
//
//		Quaternionf q0 = new Quaternionf();
//		//fmax->max
//		//for src, max->fmax
//		q0.w = (float) (Math.sqrt(Math.max(0, 1 + matrix[0][0] + matrix[1][1] + matrix[2][2])) / 2);
//		q0.x = (float) (Math.sqrt(Math.max(0, 1 + matrix[0][0] - matrix[1][1] - matrix[2][2])) / 2);
//		q0.y = (float) (Math.sqrt(Math.max(0, 1 - matrix[0][0] + matrix[1][1] - matrix[2][2])) / 2);
//		q0.z = (float) (Math.sqrt(Math.max(0, 1 - matrix[0][0] - matrix[1][1] + matrix[2][2])) / 2);
//		q0.x = Math.copySign(q0.x, matrix[2][1] - matrix[1][2]);
//		q0.y = Math.copySign(q0.y, matrix[0][2] - matrix[2][0]);
//		q0.z = Math.copySign(q0.z, matrix[1][0] - matrix[0][1]);
//		Vector3f lPos = new Vector3f(matrix[0][3], matrix[1][3], matrix[2][3]);
//
//		matrix = matrixTo2dFloatArray(dst);
//
//		Quaternionf q1 = new Quaternionf();
//		//fmax->max
//		//for src, max->fmax
//		q1.w = (float) (Math.sqrt(Math.max(0, 1 + matrix[0][0] + matrix[1][1] + matrix[2][2])) / 2);
//		q1.x = (float) (Math.sqrt(Math.max(0, 1 + matrix[0][0] - matrix[1][1] - matrix[2][2])) / 2);
//		q1.y = (float) (Math.sqrt(Math.max(0, 1 - matrix[0][0] + matrix[1][1] - matrix[2][2])) / 2);
//		q1.z = (float) (Math.sqrt(Math.max(0, 1 - matrix[0][0] - matrix[1][1] + matrix[2][2])) / 2);
//		q1.x = Math.copySign(q1.x, matrix[2][1] - matrix[1][2]);
//		q1.y = Math.copySign(q1.y, matrix[0][2] - matrix[2][0]);
//		q1.z = Math.copySign(q1.z, matrix[1][0] - matrix[0][1]);
//		Vector3f rPos = new Vector3f(matrix[0][3], matrix[1][3], matrix[2][3]);
//
//		Quaternionf smoothedQuat = interpQuat(q0, q1, delta);
//		Vector3f smoothedPos = new Vector3f(lPos.mul((float) delta)).add(rPos.mul(1 - (float) delta));
//
//		FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(3*3);
//		smoothedQuat.getAsMatrix3f(buffer);
//
//		return new double[]{
//				buffer.get(0), buffer.get(3), buffer.get(6), smoothedPos.x,
//				buffer.get(1), buffer.get(4), buffer.get(7), smoothedPos.y,
//				buffer.get(2), buffer.get(5), buffer.get(8), smoothedPos.z,
////				smoothedPos.x, smoothedPos.y, smoothedPos.z
//		};
	
		// TODO: https://docs.google.com/viewer?url=http://www.cs.wisc.edu/graphics/Courses/838-s2002/Papers/polar-decomp.pdf ?
		// https://github.com/WebKit/webkit/blob/main/Source/WebCore/platform/graphics/transforms/TransformationMatrix.cpp
		double[] d = new double[4 * 3];
		for (int i = 0; i < d.length; i++) {
			d[i] = src.m(i) * (1 - delta) + dst.m(i) * delta;
		}
		
		return d;
	}
}
