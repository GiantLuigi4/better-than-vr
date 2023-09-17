package tfc.btvr.util.controls;

public abstract class VRBinding {
	String group, name;
	
	public VRBinding(String group, String name) {
		this.group = group;
		this.name = name;
	}
	
	public abstract void tick();
	
	public void forceRelease() {
	}
}
