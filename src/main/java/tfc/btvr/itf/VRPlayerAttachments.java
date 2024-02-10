package tfc.btvr.itf;

import org.lwjgl.util.vector.Matrix4f;
import tfc.btvr.mp.packets.MatricesPacket;

public interface VRPlayerAttachments {
	void better_than_vr$setEnabled(boolean value);
	
	boolean better_than_vr$enabled();
	
	Matrix4f better_than_vr$getMatrix(int device);
	
	Matrix4f better_than_vr$getOldMatrix(int device);
	
	void better_than_vr$handleMatricies(MatricesPacket packet);
	
	float better_than_vr$getOffsetX(float pct);
	
	float better_than_vr$getOffsetZ(float pct);
	
	float better_than_vr$getRotation(float pct);
}
