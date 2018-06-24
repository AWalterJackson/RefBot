package core;

import core.CommandBuffer;
import core.Command;
import core.Response;
import utils.HttpsHandler;
import utils.JSONextension;
import utils.NBotlogger;

import java.io.IOException;
import java.util.ArrayList;

import org.json.*;

public class Telegram extends Thread {
	private final String CLIENT_NAME = "TELEGRAM";
	private static String token;
	private static String master;
	private CommandBuffer commandbuffer;
	private ArrayList<Response> responses;
	private volatile boolean stayalive;

	public Telegram(CommandBuffer cb, String token, String master) {
		this.commandbuffer = cb;
		Telegram.token = token;
		this.stayalive = true;
		Telegram.master = master;
	}

	public void shutdown() {
		this.stayalive = false;
		return;
	}

	private Command buildCommand(JSONObject message) {
		String senderid;
		String messagecommand;
		String messagedetail;

		String[] messagetokens;

		assert (message.getInt("date") > 0);

		if (message.getJSONObject("chat").get("type").toString().equals("group")) {
			senderid = message.getJSONObject("chat").get("id").toString();
		} else {
			senderid = message.getJSONObject("from").get("id").toString();
		}

		messagetokens = message.getString("text").split(" ", 2);
		if (messagetokens.length > 1) {
			messagedetail = messagetokens[1];
		} else {
			messagedetail = "";
		}
		messagecommand = messagetokens[0].split("@", 2)[0];

		return new Command(CLIENT_NAME, senderid, messagecommand, messagedetail);
	}

	private String buildResponse(Response message) {
		JSONObject res = new JSONObject();
		res.put("chat_id", message.getRecipient());
		res.put("text", message.getMessage());
		
		//res.put("reply_markup", "{\"one_time_keyboard\":true,\"keyboard\":[[{\"text\":\"A1\"}]]}");
		res.put("parse_mode", "Markdown");
		return res.toString();
	}

	private String buildReportResponse(Response message) {
		JSONObject res = new JSONObject();
		res.put("chat_id", message.getRecipient());
		res.put("text", "Report received! I'll look into it as soon as possible.\n-@Nantangitan\n\nPlease note that abuse of this system will not be tolerated.");
		try {
			this.commandbuffer.writeIncoming(new Command(message.getClient(), master, "reportforward",
					"@"+getUsername(message.getRecipient()) + " reported " + message.getMessage() + "\nReason: "+ message.getAttachment()));
		} catch (Exception e) {
			System.out.println("Err");
		}
		return res.toString();
	}

	private String buildPhotoResponse(Response message) {
		JSONObject res = new JSONObject();
		res.put("chat_id", message.getRecipient());
		res.put("caption", "");
		res.put("photo", message.getAttachment());
		return res.toString();
	}

	public String getClient() {
		return this.CLIENT_NAME;
	}

	public static String getUsername(String id) throws Exception {
		JSONObject data = new JSONObject();
		data.put("chat_id", id);
		JSONObject response = new JSONObject(HttpsHandler.httpspost("https://api.telegram.org/bot" + token + "/getChat",
				"application/json", data.toString()));
		return response.getJSONObject("result").getString("username").toLowerCase();
	}

	public void run() {
		long offset = 0;
		JSONObject message;
		Command incoming;

		try {
			JSONObject response = new JSONObject(
					HttpsHandler.httpsget("https://api.telegram.org/bot" + token + "/getMe"));
			if (response.getBoolean("ok")) {
				NBotlogger.log(CLIENT_NAME, "Telegram API connection established");
			}
			response = response.getJSONObject("result");
			NBotlogger.log(CLIENT_NAME, "BOT ID: " + response.get("id").toString());
			NBotlogger.log(CLIENT_NAME, "BOT NAME: " + response.get("first_name").toString());

			while (stayalive) {
				response = new JSONObject(
						HttpsHandler.httpsget("https://api.telegram.org/bot" + token + "/getupdates?offset=" + offset));
				if (response.getBoolean("ok")) {
					if (JSONextension.getOptionalField(response, "result") != "") {
						JSONArray updates = response.getJSONArray("result");

						for (int i = 0; i < updates.length(); i++) {
							message = updates.getJSONObject(i);
							offset = message.getInt("update_id") + 1;
							message = message.getJSONObject("message");
							if (JSONextension.getOptionalField(message, "entities") != "") {
								incoming = buildCommand(message);
								commandbuffer.writeIncoming(incoming);
							}
						}
					}
				}
				// System.out.println("Handling Responses");
				responses = commandbuffer.pullResponses(CLIENT_NAME);
				if (responses.size() > 0) {
					for (int i = 0; i < responses.size(); i++) {
						Response current = responses.get(i);
						switch(current.getType()){
						case TEXT: 
							HttpsHandler.httpspost("https://api.telegram.org/bot" + token + "/sendMessage",
									"application/json", buildResponse(current));
							break;
						case PHOTO:
							try{
							HttpsHandler.httpspost("https://api.telegram.org/bot" + token + "/sendPhoto",
									"application/json", buildPhotoResponse(current));
							}
							catch (IOException e){
								HttpsHandler.httpspost("https://api.telegram.org/bot" + token + "/sendMessage",
										"application/json", buildResponse(new Response(current.getClient(), current.getRecipient(), "Unable to send image. This is likely due to the file size exceeding the allowed limit (5mb).")));
							}
							break;
						case MULTICAST:
							HttpsHandler.httpspost("https://api.telegram.org/bot" + token + "/sendMessage",
									"application/json", buildReportResponse(current));
						default:
							break;
						}
					}
				}
				Thread.sleep(3000);
			}
		} catch (IOException e) {
			NBotlogger.log(CLIENT_NAME, "Telegram API returned fail code.\n");
			this.commandbuffer.writeError(this, CLIENT_NAME);

		} catch (Exception e) {
			NBotlogger.log(CLIENT_NAME, "Exception raised in Telegram Communicator.\n");
			this.commandbuffer.writeError(this, CLIENT_NAME);
		}
	}
}
