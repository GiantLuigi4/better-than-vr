package tfc.btvr.math;

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
}