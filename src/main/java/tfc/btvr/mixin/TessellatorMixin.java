package tfc.btvr.mixin;

import net.minecraft.client.render.Tessellator;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

@Mixin(value = Tessellator.class, remap = false)
public class TessellatorMixin {
	@Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTexCoordPointer(IILjava/nio/FloatBuffer;)V"))
	public void preTexCoord(int size, int stride, FloatBuffer floatBuffer) {
		GL11.glTexCoordPointer(size, GL11.GL_FLOAT, stride, floatBuffer);
	}
	
	@Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColorPointer(IZILjava/nio/ByteBuffer;)V"))
	public void preColor(int size, boolean unsigned, int stride, ByteBuffer pointer) {
		GL11.glColorPointer(size, unsigned ? GL11.GL_UNSIGNED_BYTE : GL11.GL_BYTE, stride, pointer);
	}
	
	@Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glNormalPointer(ILjava/nio/ByteBuffer;)V"))
	public void preNormal(int stride, ByteBuffer pointer) {
		GL11.glNormalPointer(GL11.GL_BYTE, stride, pointer);
	}
	
	@Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glVertexPointer(IILjava/nio/FloatBuffer;)V"))
	public void preVert(int size, int stride, FloatBuffer pointer) {
		GL11.glVertexPointer(size, GL11.GL_FLOAT, stride, pointer);
	}
}
