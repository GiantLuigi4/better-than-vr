package tfc.btvr.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.world.World;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class MenuCreator {
	protected static void writeInt(OutputStream baos, int value) throws IOException {
		baos.write(value >>> 24);
		baos.write(value >>> 16);
		baos.write(value >>> 8);
		baos.write(value);
	}
	
	public static void create(int size) throws IOException {
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		EntityPlayer player = mc.thePlayer;
		World world = mc.theWorld;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzos = new GZIPOutputStream(baos);
		writeInt(gzos, size);
		for (int x = -size; x <= size; x++) {
			for (int y = -size; y <= size; y++) {
				for (int z = -size; z <= size; z++) {
					writeInt(gzos, world.getBlockId(
							(int) player.getPosition(0).xCoord + x,
							(int) player.getPosition(0).yCoord + y,
							(int) player.getPosition(0).zCoord + z
					));
					gzos.write(world.getBlockMetadata(
							(int) player.getPosition(0).xCoord + x,
							(int) player.getPosition(0).yCoord + y,
							(int) player.getPosition(0).zCoord + z
					));
				}
			}
		}
		gzos.finish();
		gzos.flush();
		gzos.close();
		
		byte[] data = baos.toByteArray();
		FileOutputStream fos = new FileOutputStream("cliffs.dat");
		fos.write(data);
		fos.flush();
		fos.close();
	}
}
