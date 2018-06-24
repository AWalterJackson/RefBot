package core;

public class Command {
	private String client;
	private String sender;
	private String command;
	private String details;
	
	public Command(String cli, String sen, String com, String det){
		this.client = cli;
		this.sender = sen;
		this.command = com;
		this. details = det;
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
		return true;
	}
	
	public Command clone(){
		return new Command(this.client, this.sender, this.command, this.details);
	}

}
