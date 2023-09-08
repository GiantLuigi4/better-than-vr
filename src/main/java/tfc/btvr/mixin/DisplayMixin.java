package tfc.btvr.mixin;

import org.lwjgl.LWJGLException;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = Display.class, remap = false)
public class DisplayMixin {
	private static int w, h;
	private static String title;
	
	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public static void setDisplayMode(DisplayMode mode) throws LWJGLException {
		w = mode.getWidth();
		h = mode.getHeight();
	}
	
	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public static void setTitle(String newTitle) {
		title = newTitle;
	}
	
	private static long handle;
	
	static {
		GLFW.glfwInit();
	}
	
	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public static void create() throws LWJGLException {
		GLFW.glfwDefaultWindowHints(); // optional, the current window hints are already the default
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // the window will stay hidden after creation
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // the window will be resizable
		
		GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_API);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		handle = GLFW.glfwCreateWindow(w, h, title, 0, 0);
		GLFW.glfwMakeContextCurrent(handle);
		GL.createCapabilities();
		GLFW.glfwShowWindow(handle);
	}
	
	@Overwrite
	public static void create(PixelFormat pixel_format) throws LWJGLException {
		create();
	}
	
	@Overwrite
	public static void create(PixelFormat pixel_format, Drawable shared_drawable) throws LWJGLException {
		create();
	}
	
	@Overwrite
	public static void create(PixelFormat pixel_format, ContextAttribs attribs) throws LWJGLException {
		create();
	}
	
	@Overwrite
	public static void create(PixelFormat pixel_format, Drawable shared_drawable, ContextAttribs attribs) throws LWJGLException {
		create();
	}
	
	@Overwrite
	public static void create(PixelFormatLWJGL pixel_format) throws LWJGLException {
		create();
	}
	
	@Overwrite
	public static void create(PixelFormatLWJGL pixel_format, Drawable shared_drawable) throws LWJGLException {
		create();
	}
	
	@Overwrite
	public static void create(PixelFormatLWJGL pixel_format, org.lwjgl.opengles.ContextAttribs attribs) throws LWJGLException {
		create();
	}
	
	@Overwrite
	public static void create(PixelFormatLWJGL pixel_format, Drawable shared_drawable, org.lwjgl.opengles.ContextAttribs attribs) throws LWJGLException {
		create();
	}
}
