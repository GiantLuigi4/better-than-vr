package tfc.btvr.mixin.common.tracking;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import org.lwjgl.util.vector.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import tfc.btvr.itf.VRPlayerAttachments;
import tfc.btvr.mp.VRSuperPacket;
import tfc.btvr.mp.packets.MatricesPacket;

@Mixin(value = EntityPlayerMP.class, remap = false)
public class MPPlayerMixin implements VRPlayerAttachments {
	@Shadow
	public MinecraftServer mcServer;
	@Unique
	boolean enabled = false;
	@Unique
	boolean senabled = false;
	
	@Unique
	private static final Logger LOGGER = LoggerFactory.getLogger("BTVR::PlayerAttachments");
	
	@Override
	public void better_than_vr$setEnabled(boolean value) {
		if (senabled) {
			LOGGER.warn("Set enabled called twice");
			return;
		}
		
		enabled = value;
		senabled = true;
	}
	
	@Override
	public boolean better_than_vr$enabled() {
		return enabled;
	}
	
	Matrix4f[] matrices = new Matrix4f[3];
	
	@Override
	public Matrix4f better_than_vr$getMatrix(int device) {
		return matrices[device];
	}
	
	@Override
	public void better_than_vr$handleMatricies(MatricesPacket packet) {
		matrices[0] = packet.getM0();
		matrices[1] = packet.getM1();
		matrices[2] = packet.getM2();
		
		// client might not send the right id, so recreate the packet
		VRSuperPacket packet1 = new VRSuperPacket(new MatricesPacket((EntityPlayer) (Object) this));
		for (EntityPlayerMP playerEntity : this.mcServer
				.configManager
				.playerEntities
		) {
			if (playerEntity == (Object) this) continue;
			
			if (((VRPlayerAttachments) playerEntity).better_than_vr$enabled()) {
				playerEntity.playerNetServerHandler.sendPacket(packet1);
			}
		}
	}
}
