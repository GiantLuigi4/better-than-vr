package tfc.btvr.math;

public class MathHelper {
	public static double wrapDegrees(double deg) {
		deg += 180;
		if (deg >= 360) deg = deg % 360;
		else while (deg < 0) deg += 360; // TODO: optimize
		deg -= 180;
		
		return deg;
	}
	
	public static double atan2(double x, double y) {
		return Math.atan2(x, y);
	}
}
