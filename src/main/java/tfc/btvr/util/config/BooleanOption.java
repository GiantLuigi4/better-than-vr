package tfc.btvr.util.config;

import turniplabs.halplibe.util.ConfigHandler;

import java.util.Properties;

public class BooleanOption extends Config.Option {
	String name;
	boolean def;
	boolean value;
	
	public BooleanOption(String name, boolean value) {
		this.name = name;
		this.def = value;
		this.value = value;
	}
	
	protected void write(Properties properties) {
		properties.put(name, "" + value);
	}
	
	protected void read(ConfigHandler properties) {
		value = properties.getBoolean(name);
	}
	
	public boolean get() {
		return value;
	}
}
