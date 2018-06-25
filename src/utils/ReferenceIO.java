package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handles all IO with relation to character references Including main CRUD
 * operations and helper operations around reference reporting.
 * 
 * @author Nantang'Itan
 * @version 1.0
 * @since 2018-06-24
 */
public class ReferenceIO {

	private static final String CLIENT_NAME = "REFERENCE_IO";
	private final JSONObject errorbject = new JSONObject("{\"error\":\"error\"}");

	private final String storagepath;

	public ReferenceIO(String spath) {
		this.storagepath = spath;
	}

	public JSONObject readReference(String username, String character) {
		try {
			JSONObject userdata = readFromJSON(username);
			return userdata.getJSONObject("characters").getJSONObject(character);
		} catch (FileNotFoundException e) {
			return buildJSONError("No data found for given username.");
		} catch (IOException e) {
			return buildJSONError("Error reading from database.");
		} catch (JSONException e) {
			return buildJSONError("No character found with that name belonging to that user");
		}
	}

	public JSONObject createReference(String uid, String username, JSONObject character) {
		try {
			JSONObject userdata = readFromJSON(username);
			if (!uid.equals(userdata.getString("userid"))) {
				return buildJSONError("Telegram UID does not match for given username");
			} else {
				if (userdata.getJSONObject("characters").has(character.getString("name"))) {
					return buildJSONError("Character already exists, please use /update");
				} else {
					JSONObject storedcharacter = buildCharacterJSON(character.getString("image"),
							character.getString("caption"));
					userdata.getJSONObject("characters").put(character.getString("name"), storedcharacter);

					writeToJSON(userdata, username);

					return character;
				}
			}
		} catch (FileNotFoundException e) {
			JSONObject newuser = buildNewUserJSON(uid, username);
			try {
				writeToJSON(newuser, username);
			} catch (IOException e1) {
				e.printStackTrace();
				return buildJSONError("Error accessing database.");
			}
			return createReference(uid, username, character);
		} catch (NoSuchFileException e){
			JSONObject newuser = buildNewUserJSON(uid, username);
			try {
				writeToJSON(newuser, username);
			} catch (IOException e1) {
				e.printStackTrace();
				return buildJSONError("Error accessing database.");
			}
			return createReference(uid, username, character);
		} catch (IOException e) {
			e.printStackTrace();
			return buildJSONError("Error accessing database.");
		}
	}

	public JSONObject updateReference(String uid, String username, JSONObject character) {
		try{
			JSONObject userdata = readFromJSON(username);
			if(!uid.equals(userdata.getString("userid"))){
				return buildJSONError("Telegram UID does not match for given username");
			}
			else{
				if(!userdata.getJSONObject("characters").has(character.getString("name"))){
					return buildJSONError("No character with that name found.");
				}
				else{
					String name = character.getString("name");
					userdata.getJSONObject("characters").getJSONObject(name).put("image", character.getString("image"));
					userdata.getJSONObject("characters").getJSONObject(name).put("caption", character.getString("caption"));
					
					writeToJSON(userdata, username);
					return character;
				}
			}
		} catch (FileNotFoundException e){
			return buildJSONError("No data found for given username.");
		} catch (IOException e){
			return buildJSONError("Error accessing database.");
		}
	}

	public JSONObject deleteReference(String uid, String username, String character) {
		try{
			JSONObject userdata = readFromJSON(username);
			if(!uid.equals(userdata.getString("userid"))){
				return buildJSONError("Telegram UID does not match for given username");
			}
			else{
				if(!userdata.getJSONObject("characters").has(character)){
					return buildJSONError("No character with that name found.");
				}
				else{
					userdata.getJSONObject("characters").remove(character);
					
					writeToJSON(userdata, username);
					
					JSONObject confirmation = new JSONObject();
					confirmation.put("status", "Character deleted successfully");
					return confirmation;
				}
			}
		} catch (FileNotFoundException e){
			return buildJSONError("No data found for given username.");
		} catch (IOException e) {
			return buildJSONError("Error accessing database.");
		}
	}

	private void writeToJSON(JSONObject json, String filename) throws IOException, FileNotFoundException {
		File outfile = new File(this.storagepath + filename + ".json");

		if (!outfile.exists()) {
			outfile.createNewFile();
		} else {
			outfile.delete();
			outfile.createNewFile();
		}

		PrintWriter outwriter = new PrintWriter(outfile);
		outwriter.print(json.toString());

		outwriter.close();
	}

	private JSONObject readFromJSON(String filename) throws IOException, JSONException, FileNotFoundException {
		File userfile = new File(this.storagepath + filename + ".json");
		if (!userfile.exists()) {
			throw new FileNotFoundException();
		} else {
			//System.out.println()
			String jsondata = new String(Files.readAllBytes(Paths.get(this.storagepath + filename + ".json")));
			return new JSONObject(jsondata);
		}
	}

	public JSONObject buildEmptyUserJSON() {
		JSONObject emptyuser = new JSONObject();
		emptyuser.put("usertext", "");
		emptyuser.put("userid", "");
		emptyuser.put("characters", new JSONObject());
		emptyuser.put("reports_made", "");

		return emptyuser;
	}

	public JSONObject buildNewUserJSON(String uid, String username) {
		JSONObject newuser = buildEmptyUserJSON();
		newuser.put("usertext", username);
		newuser.put("userid", uid);

		return newuser;
	}

	public JSONObject buildCharacterJSON(String imageid, String caption) {
		JSONObject character = new JSONObject();
		character.put("image", imageid);
		character.put("caption", caption);

		return character;
	}

	public JSONObject buildJSONError(String text) {
		JSONObject error = new JSONObject();
		error.put("error", text);
		return error;
	}

}
