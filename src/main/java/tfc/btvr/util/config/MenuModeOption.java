package tfc.btvr.util.config;

import turniplabs.halplibe.util.ConfigHandler;

import java.util.Properties;

public class MenuModeOption extends Config.Option {
	String name;
	MenuMode def;
	MenuMode value;
	
	public MenuMode get() {
		return value;
	}
	
	public enum MenuMode {
		CHOICE, RANDOM, FLAT, VOID
	}
	
	public MenuModeOption(String name, MenuMode defaultV) {
		this.name = name;
		this.def = defaultV;
		this.value = defaultV;
	}
	
	protected void write(Properties properties) {
		properties.put(name, value.name().toLowerCase());
	}
	
	protected void read(ConfigHandler properties) {
		switch (properties.getString(name)) {
			case "flat":
				value = MenuMode.FLAT;
				break;
			case "random":
				value = MenuMode.RANDOM;
				break;
			case "void":
				value = MenuMode.VOID;
				break;
			case "choice":
				value = MenuMode.CHOICE;
				break;
		}
	}
}
