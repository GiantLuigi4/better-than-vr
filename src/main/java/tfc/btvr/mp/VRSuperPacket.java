package tfc.btvr.mp;

import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.net.packet.Packet;
import tfc.btvr.mp.packets.VRPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VRSuperPacket extends Packet {
	VRPacket packet;
	ByteArrayOutputStream data;
	
	IOException err;
	
	public VRSuperPacket(VRPacket packet) {
		this.packet = packet;
		data = new ByteArrayOutputStream();
		try {
			packet.writePacketData(new BaosWrapper(data));
		} catch (IOException err) {
			this.err = err;
		}
	}
	
	public VRSuperPacket() {
	}
	
	@Override
	public void readPacketData(DataInputStream dataInputStream) throws IOException {
		byte id = dataInputStream.readByte();
		this.packet = VRPacket.decode(id, dataInputStream);
		
		data = new ByteArrayOutputStream();
		packet.writePacketData(new BaosWrapper(data));
	}
	
	@Override
	public void writePacketData(DataOutputStream dataOutputStream) throws IOException {
		if (err != null) throw err;
		
		dataOutputStream.write((byte) packet.getId());
		dataOutputStream.write(data.toByteArray());
	}
	
	@Override
	public void processPacket(NetHandler netHandler) {
		packet.handle(netHandler);
	}
	
	@Override
	public int getPacketSize() {
		return data.size() + 1;
	}
}
