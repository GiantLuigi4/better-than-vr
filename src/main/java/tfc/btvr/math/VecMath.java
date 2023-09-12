package tfc.btvr.math;

public class VecMath {
	public static void normalize(double[] values) {
		double total = 0;
		for (int i = 0; i < values.length; i++) total += values[i] * values[i];
		total = Math.sqrt(total);
		for (int i = 0; i < values.length; i++) values[i] /= total;
	}
	
	public static double[] rotate(double[] src, double angle) {
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		return new double[]{
				src[0] * c - src[1] * s,
				src[0] * s + src[1] * c,
		};
	}
}
