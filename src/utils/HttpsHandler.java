package utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public final class HttpsHandler {
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final boolean debug = false;

	public static String httpget(String url) throws Exception {
		// Open connection
		URL resourcelocator = new URL(url);
		HttpURLConnection con = (HttpURLConnection) resourcelocator.openConnection();

		// Set Request Type
		con.setRequestMethod("GET");

		// Set Header Details
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responsecode = con.getResponseCode();
		if (debug) {
			System.out.println("GET sent to: " + url);
			System.out.println("Response code: " + responsecode);
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputline;
		StringBuffer response = new StringBuffer();

		while ((inputline = in.readLine()) != null) {
			response.append(inputline);
		}
		in.close();

		return response.toString();
	}

	public static String httpsget(String url) throws Exception {
		// Open connection
		URL resourcelocator = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) resourcelocator.openConnection();

		// Set Request Type
		con.setRequestMethod("GET");

		// Set Header Details
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responsecode = con.getResponseCode();
		if (debug) {
			System.out.println("GET sent to: " + url);
			System.out.println("Response code: " + responsecode);
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputline;
		StringBuffer response = new StringBuffer();

		while ((inputline = in.readLine()) != null) {
			response.append(inputline);
		}
		in.close();

		return response.toString();
	}

	public static String httpspost(String url, String type, String data) throws Exception {
		// Open Connection
		URL resourcelocator = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) resourcelocator.openConnection();

		// Set request type
		con.setRequestMethod("POST");

		// Set Header Details
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-type", type);

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(data);
		wr.flush();
		wr.close();

		int responsecode = con.getResponseCode();
		if (debug) {
			System.out.println(responsecode);
		}
		// Debugdata
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputline;
		StringBuffer response = new StringBuffer();

		while ((inputline = in.readLine()) != null) {
			response.append(inputline);
		}
		in.close();
		if (debug) {
			System.out.println(response.toString());
		}
		return response.toString();
	}
}
