package core;

public class Response {
	
	public enum MsgType {
		TEXT, PHOTO, FILE, AUDIO, MULTICAST
	}
	
	private String client;
	private String recipient;
	private String message;
	private String attachment;
	private MsgType messagetype;
	
	public Response(String cli, String rec, String mes){
		this.client = cli;
		this.recipient = rec;
		this.message = mes;
		this.messagetype = MsgType.TEXT;
		this.attachment = "";
	}
	
	public Response(String cli, String rec, String mes, MsgType mtype, String att){
		this.client = cli;
		this.recipient = rec;
		this.message = mes;
		this.messagetype = mtype;
		this.attachment = att;
	}
	
	//Getter functions
	public String getClient(){
		return this.client;
	}
	
	public String getRecipient(){
		return this.recipient;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public MsgType getType(){
		return this.messagetype;
	}
	
	public String getAttachment(){
		return this.attachment;
	}
	
	public boolean equals(Response other){
		if(this.client != other.getClient()){
			return false;
		}
		if(this.recipient != other.getRecipient()){
			return false;
		}
		if(this.message != other.getMessage()){
			return false;
		}
		if(this.messagetype != other.getType()){
			return false;
		}
		return true;
	}
	
	public Response clone(){
		return new Response(this.client, this.recipient, this.message, this.messagetype, this.attachment);
	}
}
