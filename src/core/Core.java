package core;

import utils.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import core.BotParams;

import core.Telegram;
import core.Command;
import core.CommandBuffer;
import core.Response;
import core.Response.MsgType;

import org.json.*;

public class Core extends Thread {

	private static final String CLIENT_NAME = "CORE";
	private static final boolean DEBUG_MODE = false;
	private static String storagepath;

	private static int telegramErrors;

	private static boolean loadedTG;

	private static BotParams config;

	private static int interval;

	public static void main(String[] args) throws Exception {
		config = new BotParams();
		interval = config.getInterval();

		CommandBuffer commandbuffer = new CommandBuffer(DEBUG_MODE);
		ArrayList<Command> incoming;
		ArrayList<Thread> errs;
		Telegram tg;

		// Error Logging
		telegramErrors = 0;

		loadedTG = false;

		storagepath = System.getProperty("user.dir") + "\\storage";
		File storage = new File(storagepath);
		if (!storage.exists()) {
			NBotlogger.log(CLIENT_NAME, "Storage directory not found. Creating.");
			if (!storage.mkdir()) {
				NBotlogger.log(CLIENT_NAME, "Failed to create storage directory. Exiting.");
				System.exit(-1);
			}
		}

		// Start communicator threads
		if (config.loadTelegram()) {
			tg = new Telegram(commandbuffer, config.getTelegramToken(), config.getTelegramMaster());
			tg.start();
			loadedTG = true;
		}

		while (true) {
			// Handle thread restarts
			errs = commandbuffer.pullErrors(CLIENT_NAME);
			for (Thread err : errs) {
				if (err.getClass().equals(Telegram.class)) {
					telegramErrors++;
					tg = new Telegram(commandbuffer, config.getTelegramToken(), config.getTelegramMaster());
					tg.start();
					NBotlogger.log(CLIENT_NAME, "TELEGRAM thread reinitialised");
				}
			}
			// Handle command processing
			incoming = commandbuffer.pullCommands(CLIENT_NAME);

			for (int i = 0; i < incoming.size(); i++) {
				commandbuffer.writeOutgoing(process(incoming.get(i)));
			}
			Thread.sleep(interval);
		}
	}

	private static Response process(Command c) {
		String com = c.getCommand().toLowerCase();
		String senderusername;
		try { 
			senderusername = Telegram.getUsername(c.getSender());
		}
		catch(Exception e){
			senderusername = "";
		}
		NBotlogger.log(CLIENT_NAME, com + " from " + c.getSender() + " via " + c.getClient());
		switch (com) {
		case "/status":
			if (c.getSender().equals(config.getTelegramMaster())) {
				String sysinfo = "OS: " + System.getProperty("os.name") + "\nVersion: "
						+ System.getProperty("os.version");
				String modules = "";
				if (loadedTG) {
					modules += "TELEGRAM,";
				}
				String errs = "";
				if (loadedTG) {
					errs += "Telegram: " + telegramErrors + "\n";
				}

				modules = modules.substring(0, modules.length() - 1);
				errs = errs.substring(0, errs.length() - 1);

				return new Response(c.getClient(), c.getSender(), "System running.\nSystem Information:\n" + sysinfo
						+ "\n\nLoaded Modules:\n" + modules + "\n\nErrors since last launch:\n" + errs);
			} else {
				return new Response(c.getClient(), c.getSender(),
						"You are not authorised to execute this command. This incident has been logged.");
			}
		case "/start":
			return new Response(c.getClient(), c.getSender(),
					"Hi there! I'm RefBot, I allow you to store a character reference including an image and description within Telegram and also allow you to view references stored by others.\nLet's begin, try out /commands to see what I do in detail!\n\nBot created by @Nantangitan, if you like the bot and want to help me buy food, try /donate !");
		case "/donate":
			return new Response(c.getClient(), c.getSender(), "Donations can be made at paypal.me/nantangitan\nAnd anything at all is greatly appreciated. :D");
		case "/set":
			if(senderusername.equals("")){
				return new Response(c.getClient(), c.getSender(), "You must have a Telegram username set in order to use this functionality.");
			}
			if (c.getDetails().equals("")) {
				return new Response(c.getClient(), c.getSender(),
						"User sent malformed command.\nRefer to command formatting in client tooltips.");
			}
			String caption;
			String[] toks = c.getDetails().split(" ", 2);
			String url = toks[0];
			if (!validURL(url)) {
				return new Response(c.getClient(), c.getSender(),
						"URL does not comply with current ruleset.\nTo view rules, use /rules");
			}
			if (toks.length > 1) {
				caption = toks[1];
			} else {
				caption = "";
			}

			String jsonwriteable = "{\"url\":\"" + url + "\",\"caption\":\"" + caption + "\"}";
			try {
				PrintWriter outfile = new PrintWriter(
						storagepath + "\\" + Telegram.getUsername(c.getSender()) + ".json");
				outfile.println(jsonwriteable);
				outfile.close();
				return new Response(c.getClient(), c.getSender(), caption, MsgType.PHOTO, url);
			} catch (FileNotFoundException e) {
				return new Response(c.getClient(), c.getSender(),
						"An error has occurred writing the data to storage. Please try again in a few moments.\nThis error has been logged.");
			} catch (Exception e) {
				return new Response(c.getClient(), c.getSender(),
						"An error has occurred reading user data from Telegram, this was likely due to a communication error with the server and should resolve within a few minutes.");
			}
		case "/get":
			if (c.getDetails().matches("@[a-z|0-9|A-Z|_]{5,}$")) {
				File data = new File(storagepath + "\\" + c.getDetails().replaceAll("@", "").toLowerCase() + ".json");
				if (data.exists()) {
					try {
						BufferedReader reader = new BufferedReader(new FileReader(data));
						JSONObject jsondata = new JSONObject(reader.readLine());
						reader.close();
						return new Response(c.getClient(), c.getSender(), jsondata.getString("caption"), MsgType.PHOTO,
								jsondata.getString("url"));
					} catch (IOException e) {
						return new Response(c.getClient(), c.getSender(),
								"An error has occurred reading reference data. Please try again later.");
					}
				} else {
					return new Response(c.getClient(), c.getSender(),
							"No reference stored for that user.\nTell them to get on board! :D");
				}
			} else {
				return new Response(c.getClient(), c.getSender(), "Malformed or missing username. Make sure to specify using the format: @username");
			}
		case "/delete":
			if(senderusername.equals("")){
				return new Response(c.getClient(), c.getSender(), "You must have a Telegram username set in order to use this functionality.");
			}
			try {
				File data = new File(storagepath + "\\" + Telegram.getUsername(c.getSender()) + ".json");
				if (data.exists()) {
					data.delete();
					return new Response(c.getClient(), c.getSender(),
							"Datafile deleted. This includes both the reference image and the description.");
				} else {
					return new Response(c.getClient(), c.getSender(),
							"No datafile was found for this user, nothing to delete.");
				}
			} catch (Exception e) {
				return new Response(c.getClient(), c.getSender(),
						"An error has occurred reading user data from Telegram, this was likely due to a communication error with the server and should resolve within a few minutes.");
			}
		case "/report":
			if(senderusername.equals("")){
				return new Response(c.getClient(), c.getSender(), "You must have a Telegram username set in order to use this functionality.");
			}
			if(c.getDetails().equals("")){
				return new Response(c.getClient(), c.getSender(), "No user specified for report.");
			}
			String[] dets = c.getDetails().split(" ", 2);
			String reason;
			if(!dets[0].matches("@[a-z|0-9|A-Z|_]{5,}$")){
				return new Response(c.getClient(), c.getSender(), "Malformed username. Username must consist only of _, 0-9 or a-z, must be at least 5 characters long and are case-insensitive.");
			}
			if(dets.length == 1){
				reason = "None";
			}
			else{
				reason = dets[1];
			}
			return new Response(c.getClient(), c.getSender(), c.getDetails(), MsgType.MULTICAST, reason);
		case "/commands":
			return new Response(c.getClient(), c.getSender(), "/commands - see all commands as a message\n\n/delete - wipes your stored data. (All relevant files will be deleted and no trace will remain)\n\n/donate - Displays donation information\n\n/get - \'get @username\' Retrieves the reference sheet and character description for the given user\n\n/report - \'report @username <reason>\' Reports the given user for content violation. Note that this command will also track who submitted the report.\n\n/rules - Retrieves the Terms of Service for using this bot AND the list of allowed domains for images.\n\n/set - \'set <Image URL> <Character Description>\' Sets your current reference sheet and description. Overwrites previous data.\n\n/start - Display the welcome message again");
		case "/rules":
			return new Response(c.getClient(), c.getSender(),
					"*1.* Do not store images of characters you do not own.\n\n*2.* There is no strictly disallowed content, however photographs depicting illegal acts will be dealt with on a case by case basis and appropriate action taken where neccessary.\n\n*3.* Repeated abuse of the report system will result in this feature being disabled for your telegram account.\n\n*4.* If you are repeatedly reported and the report is upheld (You are found to be in breach of these rules), this bot may be disabled for your telegram account entirely.\n\n*Image guidelines and Deletion policy*\nImages must be at most 5mb in size and be either a JPG, PNG, GIF or BMP, descriptions can be of arbitrary length.\nRefbot does not store or retain anything other than the link and description provided paired with your telegram username.\nWhen you use the delete function, ALL details are deleted in their entirety and no records retained.");
		//Metacases, these are only ever invoked by other parts of the program.
		case "reportforward":
			return new Response(c.getClient(), c.getSender(), c.getDetails());
		case "bounce":
			return new Response(c.getClient(), c.getSender(), c.getDetails());
		default:
			return new Response(c.getClient(), c.getSender(), "Unknown Command.");
		}

	}

	private static boolean validURL(String url) {
		String linkprefix = "(http(s?):\\/\\/)?";
		String linkdomain = "(d\\.facdn\\.net|i\\.imgur\\.com|static1\\.e621\\.net){1}.*";
		String linksuffix = "(\\.jp(e?)g|\\.png|\\.gif){1}";
		String linkpattern = linkprefix + linkdomain + linksuffix;
		if (url.matches(linkpattern)) {
			return true;
		} else {
			return false;
		}
	}

}
