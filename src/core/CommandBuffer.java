package core;

import java.util.ArrayList;

import utils.NBotlogger;

public class CommandBuffer extends Thread{
	
	private static final String CLIENT_NAME = "COMMAND BUFFER";
	
	private volatile String incomeaccessor;			//Semaphore tracking which thread has incoming buffer access
	private volatile String outgoaccessor;			//Semaphore tracking which thread has outgoing buffer access
	private volatile String erroraccessor;			//Semaphore tracking which thread has thread error buffer access
	private ArrayList<Command> incoming;			//Buffer of incoming messages to be passed to CORE
	private ArrayList<Response> outgoing;			//Buffer of outgoing messages to be passed to communicator threads
	private ArrayList<Thread> errorstates;			//Buffer of threads reporting an error state and requiring a restart
	private boolean debugmode;						//Boolean controlling whether to print debug messages to STDOUT
	
	public CommandBuffer(boolean db){
		this.incoming = new ArrayList<Command>();
		this.outgoing = new ArrayList<Response>();
		this.errorstates = new ArrayList<Thread>();
		this.incomeaccessor = "none";
		this.outgoaccessor = "none";
		this.erroraccessor = "none";
		this.debugmode = db;
	}
	
	//Functions controlling the program error states
	public ArrayList<Thread> pullErrors(String client){
		getErrorLock(client);
		ArrayList<Thread> err = new ArrayList<Thread>();
		for(Thread th : this.errorstates){
			err.add(th);
		}
		this.errorstates.clear();
		releaseErrorLock(client);
		return err;
	}
	
	public void writeError(Thread error, String client){
		getErrorLock(client);
		errorstates.add(error);
		releaseErrorLock(client);
	}
	
//	public void popError(Thread error, String client){
//		getErrorLock(client);
//		errorstates.remove(error);
//		releaseErrorLock(client);
//	}
	
	
	//Functions controlling the outgoing responses buffer
	public ArrayList<Response> pullResponses(String client){
		getOutgoingLock(client);
		ArrayList<Response> res = new ArrayList<Response>();
		Response current;
		for(int i = 0; i<outgoing.size(); i++){
			current = outgoing.get(i);
			if(current.getClient() == client){
				res.add(current);
				outgoing.remove(current);
				i--;
			}
		}
		releaseOutgoingLock(client);
		return res;
	}
	
	public void writeOutgoing(Response out){
		getOutgoingLock(out.getClient());
		this.outgoing.add(out);
		releaseOutgoingLock(out.getClient());
	}
	
	public Response popOutgoing(String client, int index){
		getOutgoingLock(client);
		Response res = this.outgoing.get(index);
		this.outgoing.remove(index);
		releaseOutgoingLock(client);
		return res;
	}
	
	public int getoutsize(){
		return outgoing.size();
	}
	
	//Functions controlling the incoming commands buffer
	public ArrayList<Command> pullCommands(String client){
		getIncomingLock(client);
		ArrayList<Command> com = new ArrayList<Command>();
		for(int i = 0; i<incoming.size();i++){
			com.add(incoming.get(i));
		}
		incoming.clear();
		releaseIncomingLock(client);
		return com;
	}
	
	public void writeIncoming(Command inc){
		getIncomingLock(inc.getClient());
		this.incoming.add(inc.clone());
		releaseIncomingLock(inc.getClient());
	}
	
	public Command popIncoming(String client, int index){
		getIncomingLock(client);
		Command com = this.incoming.get(index);
		this.incoming.remove(index);
		releaseIncomingLock(client);
		return com;
	}
	
	
	//Functions controlling semaphore states
	private synchronized boolean getErrorLock(String client){
		while(this.erroraccessor != "none" && this.erroraccessor != client){}
		this.incomeaccessor = client;
		if(debugmode){NBotlogger.log(CLIENT_NAME, "Error locked by "+client);}
		return true;
	}
	
	private synchronized boolean releaseErrorLock(String client){
		if(this.erroraccessor == client){
			this.erroraccessor = "none";
			return true;
		}
		else{
			return false;
		}
	}
	
	private synchronized boolean getIncomingLock(String client){
		while(this.incomeaccessor != "none" && this.incomeaccessor != client){}
		this.incomeaccessor = client;
		if(debugmode){NBotlogger.log(CLIENT_NAME, "Incoming locked by "+client);}
		return true;
	}
	
	private synchronized boolean releaseIncomingLock(String client){
		if(this.incomeaccessor == client){
			this.incomeaccessor = "none";
			return true;
		}
		else{
			return false;
		}
	}
	
	private synchronized boolean getOutgoingLock(String client){
		while(this.outgoaccessor != "none" && this.outgoaccessor != client){}
		this.outgoaccessor = client;
		if(debugmode){NBotlogger.log(CLIENT_NAME, "Outgoing locked by "+client);}
		return true;
	}
	
	private synchronized boolean releaseOutgoingLock(String client){
		if(this.outgoaccessor == client){
			this.outgoaccessor = "none";
			return true;
		}
		else{
			return false;
		}
	}
	
	public int getincsize(){
		return incoming.size();
	}
	
}
