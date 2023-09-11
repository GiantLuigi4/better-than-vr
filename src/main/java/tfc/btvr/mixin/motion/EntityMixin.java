package tfc.btvr.mixin.motion;

import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.Config;
import tfc.btvr.lwjgl3.VRHelper;
import tfc.btvr.lwjgl3.VRManager;
import tfc.btvr.lwjgl3.openvr.Device;
import tfc.btvr.math.VecMath;

@Mixin(value = Entity.class, remap = false)
public class EntityMixin {
	@Shadow
	public double xd;
	@Shadow
	public double zd;
	boolean isSinglePlayer = false;
	
	@Inject(at = @At("TAIL"), method = "<init>")
	public void postInit(World world, CallbackInfo ci) {
		//noinspection ConstantValue
		isSinglePlayer = (Object) this instanceof EntityPlayerSP;
	}
	
	@Inject(at = @At("HEAD"), method = "moveRelative", cancellable = true)
	public void preMove(float f, float f1, float f2, CallbackInfo ci) {
		if (isSinglePlayer) {
			
			float[] m = new float[]{-f, -f1};
			
			if (m[0] != 0 || m[1] != 0) {
				
				double len = Math.sqrt(m[0] * m[0] + m[1] * m[1]);
				
				double[] res = new double[]{m[0], 0, m[1]};
				VRHelper.orientVector(
						VRManager.VRInput ? Config.MOTION_HAND.get() : new Device(0),
						res
				);
				res[1] = 0;
				VecMath.normalize(res);
				for (int i = 0; i < res.length; i++) {
					res[i] *= len;
				}
				f = (float) res[0];
				f1 = (float) res[2];
				
				float f3 = MathHelper.sqrt_float(f * f + f1 * f1);
				if (!(f3 < 0.01F)) {
					if (f3 < 1.0F) {
						f3 = 1.0F;
					}
					
					f3 = f2 / f3;
					f *= f3;
					f1 *= f3;
					this.xd += f;
					this.zd += f1;
				}
				
			}
			
			ci.cancel();
		}
	}
}
