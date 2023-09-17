package tfc.btvr.util.gestures.immersion;

import net.minecraft.client.Minecraft;
import org.lwjgl.openvr.HmdMatrix34;
import tfc.btvr.lwjgl3.VRHelper;
import tfc.btvr.lwjgl3.openvr.Device;
import tfc.btvr.lwjgl3.openvr.DeviceType;
import tfc.btvr.util.gestures.Gesture;

public class EatingGesture extends Gesture {
	@Override
	public void recognize(Minecraft mc, double avgMot, double avgAng, Device dev, DeviceType type, HmdMatrix34 prevMatrix, HmdMatrix34 prevRel) {
		double[] coord = VRHelper.playerRelative(dev);
	}
}
