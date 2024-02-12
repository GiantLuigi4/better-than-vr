package tfc.btvr.mixin.client.pancake;

import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.model.ModelBiped;
import net.minecraft.client.render.model.ModelPlayer;
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
import tfc.btvr.math.LwjglMatrixHelper;

import java.nio.FloatBuffer;
import java.util.Arrays;

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
		GL11.glTranslated(0, 2 / 8f, 0);
		GL11.glMultMatrix(buffer);
		VRCamera.handMatrix(
				entity,
				pct,
				left
		);
		GL11.glTranslated(0, 2 / 8f, 0);
		
		call.run();
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glScalef(1f / 32, 1f / 32, 1f / 32);
		call.run();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glPopMatrix();
	}
	
	boolean[] showVs = new boolean[4];
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingRenderer;render(Lnet/minecraft/core/entity/EntityLiving;DDDFF)V", shift = At.Shift.AFTER), method = "renderPlayer")
	public void postDoRenderLiving(EntityPlayer entity, double x, double y, double z, float yaw, float renderPartialTicks, CallbackInfo ci) {
		modelBipedMain.bipedLeftArm.showModel = showVs[0];
		modelBipedMain.bipedRightArm.showModel = showVs[1];
		if (modelBipedMain instanceof ModelPlayer) {
			((ModelPlayer) modelBipedMain).bipedLeftArmOverlay.showModel = showVs[2];
			((ModelPlayer) modelBipedMain).bipedRightArmOverlay.showModel = showVs[3];
		}
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingRenderer;render(Lnet/minecraft/core/entity/EntityLiving;DDDFF)V"), method = "renderPlayer")
	public void doRenderLiving(EntityPlayer entity, double x, double y, double z, float yaw, float renderPartialTicks, CallbackInfo ci) {
		VRPlayerAttachments attachments = (VRPlayerAttachments) entity;
		
		if (attachments.better_than_vr$enabled()) {
			GL11.glPushMatrix();
			
			this.translateModel(entity, x, y, z);
			GL11.glRotated(-attachments.better_than_vr$getRotation(renderPartialTicks), 0, 1, 0);
			GL11.glTranslated(-attachments.better_than_vr$getOffsetX(renderPartialTicks), 0, -attachments.better_than_vr$getOffsetZ(renderPartialTicks));
			GL11.glRotated(attachments.better_than_vr$getRotation(renderPartialTicks), 0, 1, 0);
			GL11.glTranslated(0, -entity.heightOffset + entity.getHeadHeight(), 0);
			
			this.loadEntityTexture(entity);
			
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			draw(
					LwjglMatrixHelper.interpMatrix(
							attachments.better_than_vr$getOldMatrix(1),
							attachments.better_than_vr$getMatrix(1),
							renderPartialTicks
					),
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
					LwjglMatrixHelper.interpMatrix(
							attachments.better_than_vr$getOldMatrix(2),
							attachments.better_than_vr$getMatrix(2),
							renderPartialTicks
					),
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
			
			GL11.glEnable(GL11.GL_CULL_FACE);
			
			showVs[0] = modelBipedMain.bipedLeftArm.showModel;
			showVs[1] = modelBipedMain.bipedRightArm.showModel;
			modelBipedMain.bipedLeftArm.showModel = false;
			modelBipedMain.bipedRightArm.showModel = false;
			if (modelBipedMain instanceof ModelPlayer) {
				showVs[2] = ((ModelPlayer) modelBipedMain).bipedLeftArmOverlay.showModel;
				showVs[3] = ((ModelPlayer) modelBipedMain).bipedRightArmOverlay.showModel;
				((ModelPlayer) modelBipedMain).bipedLeftArmOverlay.showModel = false;
				((ModelPlayer) modelBipedMain).bipedRightArmOverlay.showModel = false;
			}
			
			GL11.glPopMatrix();
		} else {
			Arrays.fill(showVs, true);
		}
	}
}
