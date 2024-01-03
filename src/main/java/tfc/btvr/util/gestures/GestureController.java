package tfc.btvr.util.gestures;

import net.minecraft.client.Minecraft;
import org.lwjgl.openvr.HmdMatrix34;
import tfc.btvr.lwjgl3.VRHelper;
import tfc.btvr.lwjgl3.generic.DeviceType;
import tfc.btvr.lwjgl3.openvr.SDevice;

import java.util.ArrayList;

public class GestureController {
	double avgMot = 0;
	double avgAng = 0;
	
	DeviceType device;
	
	public GestureController(DeviceType device) {
		this.device = device;
	}
	
	ArrayList<Gesture> gestures = new ArrayList<>();
	
	HmdMatrix34 prevMatr;
	HmdMatrix34 prevRel;
	
	public void tick(Minecraft mc) {
		SDevice dev = SDevice.getDeviceForRole(device);
		
		HmdMatrix34 matr = dev.getTrueMatrix();
		HmdMatrix34 rel = dev.getMatrix();
		if (prevMatr == null) prevMatr = matr;
		if (prevRel == null) prevRel = rel;
		
		// average out motion
		double[] cVec = VRHelper.getPosition(matr);
		double[] lVec = VRHelper.getPosition(prevMatr);
		for (int i = 0; i < cVec.length; i++) cVec[i] -= lVec[i];
		avgMot += Math.sqrt(cVec[0] * cVec[0] + cVec[1] * cVec[1] + cVec[2] * cVec[2]);
		avgMot /= 2;
		
		cVec = VRHelper.getTraceVector(matr);
		lVec = VRHelper.getTraceVector(prevMatr);
		for (int i = 0; i < cVec.length; i++) cVec[i] -= lVec[i];
		avgAng += Math.sqrt(cVec[0] * cVec[0] + cVec[1] * cVec[1] + cVec[2] * cVec[2]);
		avgAng /= 2;
		
		for (Gesture gesture : gestures) gesture.recognize(this, mc, avgMot, avgAng, dev, device, prevMatr, prevRel);
		
		prevMatr = matr;
		prevRel = rel;
	}
	
	public void addGesture(Gesture gesture) {
		gestures.add(gesture);
	}
}
