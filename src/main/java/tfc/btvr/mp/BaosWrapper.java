package tfc.btvr.mp;

import java.io.ByteArrayOutputStream;

public class BaosWrapper {
	ByteArrayOutputStream out;
	
	byte[] writeBuffer = new byte[8];
	
	public BaosWrapper(ByteArrayOutputStream baos) {
		this.out = baos;
	}
	
	public final void writeByte(int v) {
		out.write(v);
	}
	
	public final void writeInt(int v) {
		writeBuffer[0] = (byte)(v >>> 24);
		writeBuffer[1] = (byte)(v >>> 16);
		writeBuffer[2] = (byte)(v >>>  8);
		writeBuffer[3] = (byte)(v >>>  0);
		out.write(writeBuffer, 0, 4);
	}
	
	public final void writeLong(long v) {
		writeBuffer[0] = (byte)(v >>> 56);
		writeBuffer[1] = (byte)(v >>> 48);
		writeBuffer[2] = (byte)(v >>> 40);
		writeBuffer[3] = (byte)(v >>> 32);
		writeBuffer[4] = (byte)(v >>> 24);
		writeBuffer[5] = (byte)(v >>> 16);
		writeBuffer[6] = (byte)(v >>>  8);
		writeBuffer[7] = (byte)(v >>>  0);
		out.write(writeBuffer, 0, 8);
	}
	
	public final void writeFloat(float v) {
		writeInt(Float.floatToIntBits(v));
	}
	
	public final void writeDouble(double v) {
		writeLong(Double.doubleToLongBits(v));
	}
}
