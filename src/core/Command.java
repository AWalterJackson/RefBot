package core;

public class Command {
	
	public enum CommandType {
		SINGULAR, EXPECTSRESPONSE
	}
	
	private String client;
	private String sender;
	private String command;
	private String details;
	private CommandType commandtype;
	
	public Command(String cli, String sen, String com, String det){
		this.client = cli;
		this.sender = sen;
		this.command = com;
		this.details = det;
		this.commandtype = CommandType.SINGULAR;
	}
	
	public Command(String cli, String sen, String com, String det, CommandType ctype){
		this.client = cli;
		this.sender = sen;
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
	
	public CommandType getCommandType(){
		return this.commandtype;
	}
	

	public boolean equals(Command other){
		if(this.client != other.getClient()){
			return false;
		}
		if(this.sender != other.getSender()){
			return false;
		}
		if(this.command != other.getCommand()){
			return false;
		}
		if(this.details != other.getDetails()){
			return false;
		}
		if(this.commandtype != other.getCommandType()){
			return false;
		}
		return true;
	}
	
	public Command clone(){
		return new Command(this.client, this.sender, this.command, this.details, this.commandtype);
	}

}
