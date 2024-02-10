package tfc.btvr.mp.packets;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.handler.NetHandler;
import org.lwjgl.util.vector.Matrix4f;
import tfc.btvr.itf.NetHandlerAccessor;
import tfc.btvr.itf.VRPlayerAttachments;
import tfc.btvr.mp.BaosWrapper;

import java.io.DataInputStream;
import java.io.IOException;

public class MatricesPacket extends VRPacket {
	public static final int ID = (PktId.nextId++);
	
	Matrix4f m0;
	Matrix4f m1;
	Matrix4f m2;
	float offsetX, offsetZ, rotation;
	
	int playerId;
	
	public MatricesPacket() {
		super(ID);
	}
	
	public MatricesPacket(EntityPlayer player) {
		super(ID);
		this.playerId = player.id;
		this.m0 = ((VRPlayerAttachments) player).better_than_vr$getMatrix(0);
		this.m1 = ((VRPlayerAttachments) player).better_than_vr$getMatrix(1);
		this.m2 = ((VRPlayerAttachments) player).better_than_vr$getMatrix(2);
		this.offsetX = ((VRPlayerAttachments) player).better_than_vr$getOffsetX(1);
		this.offsetZ = ((VRPlayerAttachments) player).better_than_vr$getOffsetZ(1);
		this.rotation = ((VRPlayerAttachments) player).better_than_vr$getRotation(1);
	}
	
	protected void writeMatr(Matrix4f m, BaosWrapper wrapper) {
		wrapper.writeFloat(m.m00);
		wrapper.writeFloat(m.m01);
		wrapper.writeFloat(m.m02);
		wrapper.writeFloat(m.m03);
		wrapper.writeFloat(m.m10);
		wrapper.writeFloat(m.m11);
		wrapper.writeFloat(m.m12);
		wrapper.writeFloat(m.m13);
		wrapper.writeFloat(m.m20);
		wrapper.writeFloat(m.m21);
		wrapper.writeFloat(m.m22);
		wrapper.writeFloat(m.m23);
		wrapper.writeFloat(m.m30);
		wrapper.writeFloat(m.m31);
		wrapper.writeFloat(m.m32);
		wrapper.writeFloat(m.m33);
	}
	
	protected Matrix4f readMatrix(DataInputStream dis) throws IOException {
		Matrix4f res = new Matrix4f();
		res.m00 = dis.readFloat();
		res.m01 = dis.readFloat();
		res.m02 = dis.readFloat();
		res.m03 = dis.readFloat();
		res.m10 = dis.readFloat();
		res.m11 = dis.readFloat();
		res.m12 = dis.readFloat();
		res.m13 = dis.readFloat();
		res.m20 = dis.readFloat();
		res.m21 = dis.readFloat();
		res.m22 = dis.readFloat();
		res.m23 = dis.readFloat();
		res.m30 = dis.readFloat();
		res.m31 = dis.readFloat();
		res.m32 = dis.readFloat();
		res.m33 = dis.readFloat();
		return res;
	}
	
	@Override
	public void readPacketData(DataInputStream dataInputStream) throws IOException {
		this.playerId = dataInputStream.readInt();
		m0 = readMatrix(dataInputStream);
		m1 = readMatrix(dataInputStream);
		m2 = readMatrix(dataInputStream);
		offsetX = dataInputStream.readFloat();
		offsetZ = dataInputStream.readFloat();
		rotation = dataInputStream.readFloat();
	}
	
	@Override
	public void writePacketData(BaosWrapper baos) {
		baos.writeInt(playerId);
		writeMatr(m0, baos);
		writeMatr(m1, baos);
		writeMatr(m2, baos);
		baos.writeFloat(offsetX);
		baos.writeFloat(offsetZ);
		baos.writeFloat(rotation);
	}
	
	@Override
	public void handle(NetHandler netHandler) {
		NetHandlerAccessor accessor = (NetHandlerAccessor) netHandler;
		
		if (accessor.better_than_vr$isServer()) {
			EntityPlayer player = accessor.better_than_vr$getPlayer();
			((VRPlayerAttachments) player).better_than_vr$handleMatricies(this);
		} else {
			Entity e = accessor.better_than_vr$getEntity(playerId);
			if (e instanceof VRPlayerAttachments) {
				((VRPlayerAttachments) e).better_than_vr$handleMatricies(this);
			}
		}
	}
	
	public Matrix4f getM0() {
		return m0;
	}
	
	public Matrix4f getM1() {
		return m1;
	}
	
	public Matrix4f getM2() {
		return m2;
	}
	
	public float getOffsetX() {
		return offsetX;
	}
	
	public float getOffsetZ() {
		return offsetZ;
	}
	
	public float getRotation() {
		return rotation;
	}
}
