package tfc.btvr.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.OpenGLHelper;
import net.minecraft.client.render.shader.Shaders;
//import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;
import org.lwjgl.openvr.*;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.VRRenderManager;
import tfc.btvr.lwjgl3.openvr.Eye;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@Mixin(value = OpenGLHelper.class, remap = false)
public class GLHelperMixin {
	@Shadow
	public static boolean enableOcclusionCheck;
	@Shadow
	public static boolean enableSphericalFog;
	
	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public static void testCapabilities(Minecraft minecraft) {
		System.out.println(GL11.glGetString(7938));
		System.out.println(GL11.glGetString(7936));
		System.out.println(GL11.glGetString(7937));
		ContextCapabilities capabilities = GLContext.getCapabilities();
		System.out.println("GL_ARB_framebuffer_object: " + capabilities.GL_ARB_framebuffer_object);
		System.out.println("GL_ARB_occlusion_query: " + capabilities.GL_ARB_occlusion_query);
		System.out.println("GL_NV_fog_distance: " + capabilities.GL_NV_fog_distance);
		
		enableOcclusionCheck = capabilities.GL_ARB_occlusion_query;
		enableSphericalFog = capabilities.GL_NV_fog_distance;
		if (minecraft.gameSettings.disableShaders.value) {
			Shaders.enableShaders = false;
			System.out.println("Shaders disabled in options file!");
		} else {
			Shaders.enableShaders = capabilities.GL_ARB_framebuffer_object && capabilities.OpenGL20;
			System.out.println("Enable Shaders: " + Shaders.enableShaders);
		}
		
		if (BTVRSetup.checkVR()) {
			MemoryStack ms = MemoryStack.stackPush();
			IntBuffer w = ms.mallocInt(1);
			IntBuffer h = ms.mallocInt(1);
			VRSystem.VRSystem_GetRecommendedRenderTargetSize(w, h);
			ms.pop();

			VRRenderManager.init(w, h);
		}
		
		int vao = ARBVertexArrayObject.glGenVertexArrays();
		ARBVertexArrayObject.glBindVertexArray(vao);
	}
}
