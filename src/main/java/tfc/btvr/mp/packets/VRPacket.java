package tfc.btvr.mp.packets;

import net.minecraft.core.net.handler.NetHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

public abstract class VRPacket {
	private static final Function<DataInputStream, VRPacket>[] decoders;
	
	public static VRPacket decode(int id, DataInputStream dis) {
		return decoders[id].apply(dis);
	}
	
	protected static VRPacket $decode(VRPacket packet, DataInputStream dis) {
		packet.tryReadPacketData(dis);
		return packet;
	}
	
	static {
		ArrayList<Function<DataInputStream, VRPacket>> building = new ArrayList<>();
		
		building.add((dis) -> $decode(new MatricesPacket(), dis));
		
		decoders = building.toArray(new Function[]{});
	}
	
	public void tryReadPacketData(DataInputStream dataInputStream) {
		try {
			readPacketData(dataInputStream);
		} catch (Throwable err) {
			err.printStackTrace();
		}
	}
	
	public abstract void readPacketData(DataInputStream dataInputStream) throws IOException;
	
	public abstract void writePacketData(ByteArrayOutputStream baos) throws IOException;
	
	private final int id;
	
	public VRPacket(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public abstract void handle(NetHandler netHandler);
}
