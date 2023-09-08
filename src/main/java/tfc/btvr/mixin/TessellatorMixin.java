package tfc.btvr.mixin;

import net.minecraft.client.GLAllocation;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ARBVertexBufferObject;
//import org.lwjgl.opengl.GL;
//import org.lwjgl.opengl.GL11;
//import org.lwjgl.opengl.GLCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Mixin(value = Tessellator.class, remap = false)
public class TessellatorMixin {
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	public void preGetMC(int bufferSize, CallbackInfo ci) {
//		useVBO = true;
//		if (vertexBuffers == null) {
//			this.vertexBuffers = GLAllocation.createDirectIntBuffer(this.vboCount);
//			ARBVertexBufferObject.glGenBuffersARB(this.vertexBuffers);
//		}
	}
}
