package tfc.btvr.model;

import net.minecraft.client.render.model.Cube;
import net.minecraft.core.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class VRModel {
	protected Cube leftArm;
	protected Cube leftArmOverlay;
	protected Cube rightArm;
	protected Cube rightArmOverlay;
	
	public VRModel() {
		leftArm = new Cube(40, 16, 64, 64);
		leftArm.mirror = true;
		leftArm.addBox(-2.0F, -6.0F, -2.0F, 4, 12, 4, 0, true);
		
		// TODO: check
		leftArmOverlay = new Cube(48, 48, 64, 64);
		leftArmOverlay.addBox(-2.0F, -6.0F, -2.0F, 4, 12, 4, 0.25F, true);
		leftArmOverlay.setRotationPoint(5.0F, 2.0F, 0.0F);
		
		rightArm = new Cube(32, 48, 64, 64);
		rightArm.addBox(-2.0F, -6.0F, -2.0F, 4, 12, 4, 0, true);
		
		// TODO: check
		rightArmOverlay = new Cube(40, 32, 64, 64);
		rightArmOverlay.addBox(-2.0F, -6.0F, -2.0F, 4, 12, 4, 0.25F, true);
		rightArmOverlay.setRotationPoint(-5.0F, 2.0F, 10.0F);
	}
	
	protected void drawCube(Cube cube, float scale) {
		cube.showModel = true;
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		cube.render(1);
		GL11.glPopMatrix();
	}
	
	public void draw(EntityPlayer player, boolean left, float scale) {
		Cube c0 = left ? leftArm : rightArm;
		Cube c1 = left ? leftArmOverlay : rightArmOverlay;
		
		drawCube(c0, scale);
		drawCube(c1, scale);
	}
}
