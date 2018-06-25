package core;

public class Command {
	
	public enum CommandType {
		SINGULAR, INLINEQUERY, CALLBACKQUERY, USERRESPONSE
	}
	
	private String client;
	private String sender;
	private String senderusername;
	private String command;
	private String details;
	private CommandType commandtype;
	
	public Command(String cli, String sen, String uname, String com, String det){
		this.client = cli;
		this.sender = sen;
		this.senderusername = uname;
		this.command = com;
		this.details = det;
		this.commandtype = CommandType.SINGULAR;
	}
	
	public Command(String cli, String sen, String uname, String com, String det, CommandType ctype){
		this.client = cli;
		this.sender = sen;
		this.senderusername = uname;
		this.command = com;
		this.details = det;
		this.commandtype = ctype;
	}
	
	//Getter functions
	public String getClient(){
		return this.client;
	}
	
	public String getSender(){
		return this.sender;
	}
	
	public String getCommand(){
		return this.command;
	}
	
	public String getDetails(){
		return this.details;
	}
	
	public String getUsername(){
		return this.senderusername;
	}
	
	public CommandType getCommandType(){
		return this.commandtype;
	}
	

	public boolean equals(Command other){
		if(!this.client.equals(other.getClient())){
			return false;
		}
		if(!this.sender.equals(other.getSender())){
			return false;
		}
		if(!this.command.equals(other.getCommand())){
			return false;
		}
		if(!this.details.equals(other.getDetails())){
			return false;
		}
		if(this.commandtype != other.getCommandType()){
			return false;
		}
		if(!this.senderusername.equals(other.getUsername())){
			return false;
		}
		return true;
	}
	
	public Command clone(){
		return new Command(this.client, this.sender, this.senderusername, this.command, this.details, this.commandtype);
	}

}
