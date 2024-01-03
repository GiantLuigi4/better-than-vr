package tfc.btvr.mp.packets;

import net.minecraft.core.net.handler.NetHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MatricesPacket extends VRPacket {
	public static final int ID = (PktId.nextId++);
	
	public MatricesPacket() {
		super(ID);
	}
	
	@Override
	public void readPacketData(DataInputStream dataInputStream) throws IOException {
	}
	
	@Override
	public void writePacketData(ByteArrayOutputStream baos) throws IOException {
	}
	
	@Override
	public void handle(NetHandler netHandler) {
	
	}
}
