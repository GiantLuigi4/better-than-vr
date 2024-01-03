package tfc.btvr.mixin.common;

import net.minecraft.core.entity.player.EntityPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tfc.btvr.itf.VRPlayerAttachments;

@Mixin(value = EntityPlayer.class, remap = false)
public class PlayerMixin implements VRPlayerAttachments {
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
}
