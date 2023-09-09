package tfc.btvr.math;

public class VecMath {
	public static void normalize(double[] values) {
		double total = 0;
		for (int i = 0; i < values.length; i++) total += values[i] * values[i];
		total = Math.sqrt(total);
		for (int i = 0; i < values.length; i++) values[i] /= total;
	}
}
