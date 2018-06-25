package core;

public class Response {
	
	public enum MsgType {
		TEXT, PHOTO, FILE, AUDIO, MULTICAST, CALLBACK, MULTIPART, ENDMULTIPART
	}
	
	private String client;
	private String recipient;
	private String recipientuname;
	private String message;
	private String attachment;
	private MsgType messagetype;
	private String markdown;
	
	public Response(String cli, String rec, String uname, String mes){
		this.client = cli;
		this.recipient = rec;
		this.recipientuname = uname;
		this.message = mes;
		this.messagetype = MsgType.TEXT;
		this.attachment = "";
		this.markdown = "";
	}
	
	public Response(String cli, String rec, String uname, String mes, MsgType mtype){
		this.client = cli;
		this.recipient = rec;
		this.recipientuname = uname;
		this.message = mes;
		this.messagetype = mtype;
		this.attachment = "";
		this.markdown = "";
	}
	
	public Response(String cli, String rec, String uname, String mes, MsgType mtype, String att){
		this.client = cli;
		this.recipient = rec;
		this.recipientuname = uname;
		this.message = mes;
		this.messagetype = mtype;
		this.attachment = att;
		this.markdown = "";
	}
	
	public Response(String cli, String rec, String uname, String mes, String replymarkdown){
		this.client = cli;
		this.recipient = rec;
		this.recipientuname = uname;
		this.message = mes;
		this.messagetype = MsgType.CALLBACK;
		this.attachment = "";
		this.markdown = replymarkdown;
	}
	
	//Getter functions
	public String getClient(){
		return this.client;
	}
	
	public String getRecipient(){
		return this.recipient;
	}
	
	public String getRecipientUsername(){
		return this.recipientuname;
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
	
	public String getMarkdown(){
		return this.markdown;
	}
	
	public boolean equals(Response other){
		if(!this.client.equals(other.getClient())){
			return false;
		}
		if(!this.recipient.equals(other.getRecipient())){
			return false;
		}
		if(!this.message.equals(other.getMessage())){
			return false;
		}
		if(this.messagetype != other.getType()){
			return false;
		}
		if(this.markdown.equals(other.getMarkdown())){
			return false;
		}
		return true;
	}
	
	public Response clone(){
		return new Response(this.client, this.recipient, this.recipientuname, this.message, this.messagetype, this.attachment);
	}
}
