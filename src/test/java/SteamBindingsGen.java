import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.util.collection.Pair;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class SteamBindingsGen {
	private static JsonArray actions = new JsonArray();
	private static HashMap<String, JsonObject> actionSets = new HashMap<>();
	private static JsonObject locale = new JsonObject();
	private static HashMap<String, HashMap<Binding, bindingset>> defaults = new HashMap<>();
	
	static class bindingset {
		Binding key;
		ArrayList<Pair<String, String>> acts = new ArrayList<>();
		
		public bindingset(Binding key) {
			this.key = key;
		}
		
		public void addAction(String output, String type) {
			acts.add(Pair.of(output, type));
		}
	}
	
	enum Binding {
		LEFT_JOYSTICK("vector2"),
		RIGHT_JOYSTICK("vector2"),
		LEFT_B("boolean"), // X on oculus touch
		LEFT_A("boolean"), // Y on oculus touch
		RIGHT_B("boolean"),
		RIGHT_A("boolean"),
		LEFT_TRIGGER("boolean"),
		RIGHT_TRIGGER("boolean"),
		;
		String type;
		
		Binding(String name) {
			this.type = name;
		}
	}
	
	public static void main(String[] args) {
		setupSet("gameplay", "Gameplay");
		addAction(Binding.LEFT_JOYSTICK, "position", "Move", false, "Move");
		addAction(Binding.LEFT_JOYSTICK, "click", "Jump", false, "Jump");
		addAction(Binding.RIGHT_JOYSTICK, "position", "Rotate", false, "Rotate");
		addAction(Binding.LEFT_A, "click", "Crouch", false, "Sneak");
		addAction(Binding.LEFT_TRIGGER, "click", "Attack", false, "Attack");
		addAction(Binding.RIGHT_TRIGGER, "click", "UseItem", false, "Use Item");
		addAction(Binding.RIGHT_B, "click", "OpenInventory", false, "Open Inventory");
		addAction(Binding.RIGHT_A, "click", "Pause", false, "Pause Game");
		
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		{
			// write action manifest
			JsonObject obj = new JsonObject();
			
			obj.add("actions", actions);
			JsonArray sets = new JsonArray();
			for (JsonObject value : actionSets.values()) {
				sets.add(value);
			}
			obj.add("action_sets", sets);
			
			JsonArray lo = new JsonArray();
			lo.add(locale);
			locale.addProperty("language_tag", "en_us");
			obj.add("localization", lo);
			
			JsonArray bindings = new JsonArray();
			JsonObject bindingsObj = new JsonObject();
			bindingsObj.addProperty("controller_type", "oculus_touch");
			bindingsObj.addProperty("binding_url", "bindings/oculus_touch.json");
			bindings.add(bindingsObj);
			obj.add("default_bindings", bindings);
			
			try {
				FileOutputStream fos = new FileOutputStream("src/main/resources/btvr/actions.json");
				fos.write(gson.toJson(obj).getBytes(StandardCharsets.UTF_8));
				fos.flush();
				fos.close();
			} catch (Throwable err) {
				err.printStackTrace();
			}
		}
		
		{
			// write oculus bindings
			JsonObject obj = new JsonObject();
			obj.addProperty("action_manifest_version", 0);
			obj.addProperty("category", "steamvr_input");
			obj.addProperty("controller_type", "oculus_touch");
			obj.addProperty("description", "Default bindings for BTVR");
			obj.addProperty("name", "BTVR Defaults");
			obj.add("options", new JsonObject());
			obj.add("simulated_actions", new JsonArray());
			
			JsonObject bindingsObj = new JsonObject();
			for (String set : defaults.keySet()) {
				JsonObject setObj = new JsonObject();
				JsonArray sources = new JsonArray();
				
				HashMap<Binding, bindingset> map = defaults.get(set);
				for (Binding binding : map.keySet()) {
					bindingset stringBindingPair = map.get(binding);
					
					JsonObject source = new JsonObject();
					
					String type;
					switch (stringBindingPair.key) {
						case LEFT_B:
						case LEFT_A:
						case RIGHT_B:
						case RIGHT_A:
						case LEFT_TRIGGER:
						case RIGHT_TRIGGER:
							type = "button";
							break;
						case LEFT_JOYSTICK:
						case RIGHT_JOYSTICK:
							type = "joystick";
							break;
						default:
							throw new RuntimeException("" + stringBindingPair.key);
					}
					
					String pth = "/user/hand/" + (stringBindingPair.key.name().contains("LEFT") ? "left" : "right") +
							"/input/";
					switch (stringBindingPair.key) {
						case LEFT_JOYSTICK:
						case RIGHT_JOYSTICK:
							pth += "joystick";
							break;
						case LEFT_B:
							pth += "x";
							break;
						case LEFT_A:
							pth += "y";
							break;
						case RIGHT_B:
							pth += "b";
							break;
						case RIGHT_A:
							pth += "a";
							break;
						case LEFT_TRIGGER:
						case RIGHT_TRIGGER:
							pth += "trigger";
							break;
					}
					
					JsonObject inputs = new JsonObject();
					for (Pair<String, String> act : stringBindingPair.acts) {
						JsonObject aaaa = new JsonObject();
						aaaa.addProperty("output", act.getLeft());
						inputs.add(act.getRight(), aaaa);
					}
					source.add("inputs", inputs);
					
					source.addProperty("mode", type);
					source.addProperty("path", pth);
					
					sources.add(source);
				}
				
				setObj.add("sources", sources);
				bindingsObj.add(set, setObj);
			}
			obj.add("bindings", bindingsObj);
			
			try {
				FileOutputStream fos = new FileOutputStream("src/main/resources/btvr/bindings/oculus_touch.json");
				fos.write(gson.toJson(obj).getBytes(StandardCharsets.UTF_8));
				fos.flush();
				fos.close();
			} catch (Throwable err) {
				err.printStackTrace();
			}
		}
	}
	
	private static String activeSet;
	
	public static void setupSet(String set, String locale) {
		JsonObject theSet = new JsonObject();
		theSet.addProperty("name", "/actions/" + set);
		theSet.addProperty("usage", "leftright");
		actionSets.put(set, theSet);
		activeSet = set;
		
		SteamBindingsGen.locale.addProperty("/actions/" + set, locale);
	}
	
	public static void addAction(
			Binding binding,
			String type,
			String name,
			boolean optional,
			String english
	) {
		locale.addProperty("/actions/" + activeSet + "/in/" + name, english);
		
		JsonObject action = new JsonObject();
		action.addProperty("requirement", optional ? "optional" : "mandatory");
		action.addProperty("name", "/actions/" + activeSet + "/in/" + name);
		action.addProperty("type", binding.type);
		actions.add(action);
		
		HashMap<Binding, bindingset> defa = defaults.computeIfAbsent("/actions/" + activeSet, (key) -> new HashMap<>());
		bindingset set = defa.computeIfAbsent(binding, (k) -> new bindingset(binding));
		set.addAction("/actions/" + activeSet + "/in/" + name, type);
	}
}
