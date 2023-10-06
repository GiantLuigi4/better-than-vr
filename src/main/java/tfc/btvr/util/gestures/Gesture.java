package tfc.btvr.util.gestures;

import net.minecraft.client.Minecraft;
import org.lwjgl.openvr.HmdMatrix34;
import tfc.btvr.lwjgl3.generic.DeviceType;
import tfc.btvr.lwjgl3.openvr.SDevice;

public abstract class Gesture {
	public void recognize(GestureControllers controller, Minecraft mc, double avgMot, double avgAng, SDevice dev, DeviceType type, HmdMatrix34 prevMatrix, HmdMatrix34 prevRel) {
		recognize(mc, avgMot, avgAng, dev, type, prevMatrix, prevRel);
	}
	
	public abstract void recognize(Minecraft mc, double avgMot, double avgAng, SDevice dev, DeviceType type, HmdMatrix34 prevMatrix, HmdMatrix34 prevRel);
}
