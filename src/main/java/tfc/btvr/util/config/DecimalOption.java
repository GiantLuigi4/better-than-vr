package tfc.btvr.util.config;

import turniplabs.halplibe.util.ConfigHandler;

import java.util.Properties;

public class DecimalOption extends Config.Option {
	String name;
	double value;
	double def;
	
	public DecimalOption(String name, double value) {
		this.name = name;
		this.def = value;
		this.value = value;
	}
	
	protected void write(Properties properties) {
		properties.put(name, "" + value);
	}
	
	protected void read(ConfigHandler properties) {
		value = Double.parseDouble(properties.getProperty(name));
	}
	
	public double get() {
		return value;
	}
}
