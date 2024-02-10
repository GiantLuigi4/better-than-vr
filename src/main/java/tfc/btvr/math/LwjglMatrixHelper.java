package tfc.btvr.math;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;

public class LwjglMatrixHelper {
	protected static float _copysign(float to, float from) {
		if (from == 0) return 0;
		return Math.signum(from) * to;
	}
	
	public static Quaternion rotation(Matrix4f matr) {
		// (Alternative method on https://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/)
		
		Quaternion quaternion = new Quaternion();
		
		quaternion.w = (float) Math.sqrt(Math.max(0, 1 + matr.m00 + matr.m11 + matr.m22)) / 2;
		quaternion.x = (float) Math.sqrt(Math.max(0, 1 + matr.m00 - matr.m11 - matr.m22)) / 2;
		quaternion.y = (float) Math.sqrt(Math.max(0, 1 - matr.m00 + matr.m11 - matr.m22)) / 2;
		quaternion.z = (float) Math.sqrt(Math.max(0, 1 - matr.m00 - matr.m11 + matr.m22)) / 2;
		
		quaternion.x = _copysign(quaternion.x, matr.m31 - matr.m12);
		quaternion.y = _copysign(quaternion.y, matr.m02 - matr.m21);
		quaternion.z = _copysign(quaternion.z, matr.m10 - matr.m01);
		
		return quaternion.normalise(quaternion);
	}
	
	public static Matrix4f rotation(Quaternion q, Matrix4f dst) {
		// https://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/index.htm
		
		double sqw = q.w * q.w;
		double sqx = q.x * q.x;
		double sqy = q.y * q.y;
		double sqz = q.z * q.z;
		
		// invs (inverse square length) is only required if quaternion is not already normalised
		double invs = 1 / (sqx + sqy + sqz + sqw);
		dst.m00 = (float) ((sqx - sqy - sqz + sqw) * invs); // since sqw + sqx + sqy + sqz =1/invs*invs
		dst.m11 = (float) ((-sqx + sqy - sqz + sqw) * invs);
		dst.m22 = (float) ((-sqx - sqy + sqz + sqw) * invs);
		
		double tmp1 = q.x * q.y;
		double tmp2 = q.z * q.w;
		dst.m10 = (float) (2.0 * (tmp1 + tmp2) * invs);
		dst.m01 = (float) (2.0 * (tmp1 - tmp2) * invs);
		
		tmp1 = q.x * q.z;
		tmp2 = q.y * q.w;
		dst.m20 = (float) (2.0 * (tmp1 - tmp2) * invs);
		dst.m02 = (float) (2.0 * (tmp1 + tmp2) * invs);
		tmp1 = q.y * q.z;
		tmp2 = q.x * q.w;
		dst.m21 = (float) (2.0 * (tmp1 + tmp2) * invs);
		dst.m12 = (float) (2.0 * (tmp1 - tmp2) * invs);
		
		return dst;
	}
	
	public static Matrix4f interpMatrix(Matrix4f src, Matrix4f to, double delta) {
		if (src == null) src = new Matrix4f();
		if (to == null) to = new Matrix4f();
		
		Matrix4f dst = new Matrix4f();
		dst.load(to);
		
		Quaternion left = rotation(src);
		Quaternion right = rotation(dst);
		
		left.set(
				tfc.btvr.math.MathHelper.lerpQuat(left.x, right.x, (float) delta),
				tfc.btvr.math.MathHelper.lerpQuat(left.y, right.y, (float) delta),
				tfc.btvr.math.MathHelper.lerpQuat(left.z, right.z, (float) delta),
				tfc.btvr.math.MathHelper.lerpQuat(left.w, right.w, (float) delta)
		);
		
		rotation(left, dst);
		dst.m03 = net.minecraft.core.util.helper.MathHelper.lerp(src.m03, dst.m03, (float) delta);
		dst.m13 = net.minecraft.core.util.helper.MathHelper.lerp(src.m13, dst.m13, (float) delta);
		dst.m23 = net.minecraft.core.util.helper.MathHelper.lerp(src.m23, dst.m23, (float) delta);
		
		return dst;
	}
}
