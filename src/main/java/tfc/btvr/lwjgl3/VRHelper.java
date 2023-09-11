package tfc.btvr.lwjgl3;

import org.lwjgl.openvr.HmdMatrix34;
import tfc.btvr.lwjgl3.openvr.Device;
import tfc.btvr.math.MatrixHelper;
import tfc.btvr.math.VecMath;

public class VRHelper {
	public static double[] getTraceVector(Device device) {
		double[] res = new double[3];
		HmdMatrix34 matr = device.getMatrix();
		MatrixHelper.mulMatr(
				0, 0, -1,
				
				matr.m(0), matr.m(1), matr.m(2), 0,
				matr.m(4), matr.m(5), matr.m(6), 0,
				matr.m(8), matr.m(9), matr.m(10), 0,
				
				res
		);
		
		VecMath.normalize(res);
		return res;
	}
	
	public static void orientVector(Device device, double[] vector) {
		HmdMatrix34 matr = device.getMatrix();
		MatrixHelper.mulMatr(
				vector[0], vector[1], vector[2],
				
				matr.m(0), matr.m(1), matr.m(2), 0,
				matr.m(4), matr.m(5), matr.m(6), 0,
				matr.m(8), matr.m(9), matr.m(10), 0,
				
				vector
		);
	}
	
	public static double[] playerRelative(Device device) {
		return new double[]{
				device.getMatrix().m(3),
				device.getMatrix().m(7) - 0.235,
				device.getMatrix().m(11),
		};
	}
}
