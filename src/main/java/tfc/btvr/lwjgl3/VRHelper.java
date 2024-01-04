package tfc.btvr.lwjgl3;

import net.minecraft.client.Minecraft;
import org.lwjgl.openvr.HmdMatrix34;
import tfc.btvr.Config;
import tfc.btvr.lwjgl3.openvr.SDevice;
import tfc.btvr.math.MatrixHelper;
import tfc.btvr.math.VecMath;

public class VRHelper {
	public static double[] getTraceVector(HmdMatrix34 matr) {
		double[] res = new double[3];
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
	
	public static double[] getTraceVector(SDevice device) {
		HmdMatrix34 matr = device.getMatrix();
		return getTraceVector(matr);
	}
	
	public static void orientVector(SDevice device, double[] vector) {
		HmdMatrix34 matr = device.getMatrix();
		MatrixHelper.mulMatr(
				vector[0], vector[1], vector[2],
				
				matr.m(0), matr.m(1), matr.m(2), 0,
				matr.m(4), matr.m(5), matr.m(6), 0,
				matr.m(8), matr.m(9), matr.m(10), 0,
				
				vector
		);
	}
	
	public static double[] playerRelative(HmdMatrix34 matr) {
		double[] cursedMatr = MatrixHelper.convert(matr);
		
		cursedMatr[3] -= VRManager.ox;
		cursedMatr[11] -= VRManager.oz;
		
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		if (mc.thePlayer != null) {
			double pct = VRRenderManager.getPct();
			if (!Config.EXTRA_SMOOTH_ROTATION.get()) pct = 1;
			
			double delt = VRManager.getRotation(pct);
			
			cursedMatr = MatrixHelper.mul(
					cursedMatr, MatrixHelper.quatToMat(
							MatrixHelper.axisQuat(
									Math.toRadians(-delt),
									0, 1, 0
							)
					)
			);
		}
		
		return new double[]{
				cursedMatr[3],
				cursedMatr[7] + 0.12f,
				cursedMatr[11],
		};
	}
	
	public static double[] playerRelative(SDevice device) {
		return playerRelative(device.getTrueMatrix());
	}
	
	public static double[] mergeMot(float[] computer, float[] vr) {
		double[] res = new double[]{computer[0], 0, computer[1]};
		VRHelper.orientVector(
				SDevice.HEAD,
				res
		);
		res[1] = 0;
		
		double[] res1 = new double[]{vr[0], 0, vr[1]};
		VRHelper.orientVector(
				Config.MOTION_HAND.get(),
				res1
		);
		res1[1] = 0;
		
		res[0] += res1[0];
		res[1] += res1[1];
		res[2] += res1[2];
		
		return res;
	}
	
	public static double[] getPosition(HmdMatrix34 matr34) {
		return new double[]{
				matr34.m(3),
				matr34.m(7),
				matr34.m(11),
		};
	}
}
