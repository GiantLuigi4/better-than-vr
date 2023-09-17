package tfc.btvr.util.gestures;

import net.minecraft.client.Minecraft;
import org.lwjgl.openvr.HmdMatrix34;
import tfc.btvr.lwjgl3.openvr.Device;
import tfc.btvr.lwjgl3.openvr.DeviceType;

public abstract class Gesture {
	public abstract void recognize(Minecraft mc, double avgMot, double avgAng, Device dev, DeviceType type, HmdMatrix34 prevMatrix, HmdMatrix34 prevRel);
}
