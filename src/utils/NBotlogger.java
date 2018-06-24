package utils;

import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class NBotlogger {

	private static final int MAX_NAME = 16;
	private static final String LOG = "LOG             ";
	
	public static void log(String name, String message){
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		if(name.length() > MAX_NAME){
			System.out.println(LOG+"> "+message);
		}
		else{
			char[] array = new char[16-name.length()];
			Arrays.fill(array, ' ');
			System.out.println(dtf.format(now)+" | "+name+new String(array)+"> "+message);
		}
	}
}
