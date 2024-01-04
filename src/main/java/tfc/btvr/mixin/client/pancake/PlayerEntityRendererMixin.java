package tfc.btvr.mixin.client.pancake;

import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.model.ModelBiped;
import net.minecraft.core.entity.player.EntityPlayer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.VRCamera;
import tfc.btvr.itf.VRPlayerAttachments;

import java.nio.FloatBuffer;

@Mixin(value = PlayerRenderer.class, remap = false)
public abstract class PlayerEntityRendererMixin {
	@Shadow
	private ModelBiped modelBipedMain;
	
	@Shadow
	protected abstract void rotateModel(EntityPlayer entity, float ticksExisted, float headYawOffset, float renderPartialTicks);
	
	@Shadow
	protected abstract void translateModel(EntityPlayer entity, double x, double y, double z);
	
	@Shadow
	public abstract void loadEntityTexture(EntityPlayer entity);
	
	private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(4 * 4);
	
	@Unique
	private static void draw(
			Matrix4f matr,
			EntityPlayer entity,
			float pct,
			boolean left,
			Runnable call
	) {
		float[] data = new float[]{
				matr.m00, matr.m10, matr.m20, 0,
				matr.m01, matr.m11, matr.m21, 0,
				matr.m02, matr.m12, matr.m22, 0,
				matr.m03, matr.m13, matr.m23, 1,
		};
		buffer.put(data);
		buffer.flip();
		
		GL11.glPushMatrix();
		
		GL11.glMultMatrix(buffer);
		VRCamera.handMatrix(
				entity,
				pct,
				left
		);
		
		call.run();
		GL11.glPopMatrix();
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingRenderer;doRenderLiving(Lnet/minecraft/core/entity/EntityLiving;DDDFF)V"), method = "renderPlayer")
	public void doRenderLiving(EntityPlayer entity, double x, double y, double z, float yaw, float renderPartialTicks, CallbackInfo ci) {
		VRPlayerAttachments attachments = (VRPlayerAttachments) entity;
		
		if (attachments.better_than_vr$enabled()) {
			float f = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * renderPartialTicks;
			this.translateModel(entity, x, y, z);
			float f3 = ((float) entity.tickCount + renderPartialTicks);
			this.rotateModel(entity, f3, f, renderPartialTicks);
			this.loadEntityTexture(entity);
			
			draw(
					attachments.better_than_vr$getMatrix(1),
					entity, renderPartialTicks,
					true, () -> {
						VRCamera.draw(
								VRCamera.normal,
								entity,
								true,
								1 / 16f
						);
					}
			);
			draw(
					attachments.better_than_vr$getMatrix(2),
					entity, renderPartialTicks,
					false, () -> {
						VRCamera.draw(
								VRCamera.normal,
								entity,
								false,
								1 / 16f
						);
					}
			);
		}
	}
}
