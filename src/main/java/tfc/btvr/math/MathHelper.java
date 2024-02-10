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
	
	public static float lerpQuat(float from, float to, float by) {
		float diff = Math.abs(from - to);
		float aDiff = Math.abs((from + 2) - to);
		if (aDiff < diff) {
			from += 2;
		} else {
			aDiff = Math.abs((from - 2) - to);
			if (aDiff < diff)
				from -= 2;
		}
		
		float res = from + (to - from) * by;
		if (res < -1) res += 2;
		if (res > 1) res -= 2;
		return res;
	}
}
