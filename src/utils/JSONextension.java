package utils;

import org.json.*;

public final class JSONextension {
	
	public static String getOptionalField(JSONObject obj, String key){
		try{
			return obj.get(key).toString();
		}
		catch(JSONException e){
			return "";
		}
	}
	
	public static String getOptionalField(String obj, String key){
		JSONObject json = new JSONObject(obj);
		try{
			return json.get(key).toString();
		}
		catch(JSONException e){
			return "";
		}
	}
	
	public static JSONObject getOptionalObject(JSONObject obj, String key){
		try{
			return obj.getJSONObject(key);
		}
		catch(JSONException e){
			return new JSONObject("{\"nodata\":true");
		}
	}
	
	public static JSONObject getOptionalObject(String obj, String key){
		JSONObject json = new JSONObject(obj);
		try{
			return json.getJSONObject(key);
		}
		catch(JSONException e){
			return new JSONObject("\"nodata\":true");
		}
	}
	
	public static boolean hasOptionalField(String obj, String key){
		JSONObject json = new JSONObject(obj);
		try{
			json.get("key");
			return true;
		}
		catch(JSONException e){
			return false;
		}
	}
	
	public static boolean hasOptionalField(JSONObject json, String key){
		try{
			json.get(key);
			return true;
		}
		catch(JSONException e){
			return false;
		}
	}
}
