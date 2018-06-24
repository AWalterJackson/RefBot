package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


import org.json.JSONException;
import org.json.JSONObject;

import utils.NBotlogger;

public class BotParams {

	private static final String CLIENT_NAME = "CONFIG";
	private static final String FILENAME = "config.json";

	// Core chat modules
	private boolean telegram;
	
	private int threadinterval;

	// Telegram Config
	private String telegramtoken;
	private String telegrammaster;
	
	public BotParams() {
		NBotlogger.log(CLIENT_NAME, "Loading Configuration");
		JSONObject config;

		NBotlogger.log(CLIENT_NAME, "Working Directory = " + System.getProperty("user.dir"));

		try {
			BufferedReader br = new BufferedReader(new FileReader(FILENAME));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			config = new JSONObject(sb.toString());
			br.close();
			// Communicators
			this.threadinterval = config.getInt("thread_interval");
			this.telegram = config.getJSONObject("load_clients").getBoolean("telegram");

			// TELEGRAM Configuration
			this.telegramtoken = config.getJSONObject("telegram_config").getString("token");
			this.telegrammaster = config.getJSONObject("telegram_config").getString("master");
			
		} catch (JSONException e) {
			NBotlogger.log(CLIENT_NAME, "Malformed JSON in Config");
			e.printStackTrace();
			System.exit(-1);

		} catch (IOException e) {
			NBotlogger.log(CLIENT_NAME, "Error reading config file, does it exist?");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public String getTelegramToken() {
		return this.telegramtoken;
	}

	public boolean loadTelegram() {
		return this.telegram;
	}
	
	public String getTelegramMaster(){
		return this.telegrammaster;
	}
	
	public int getInterval(){
		return this.threadinterval;
	}

}
