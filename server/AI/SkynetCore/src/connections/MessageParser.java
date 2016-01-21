package connections;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import configuration.Configuration;
import configuration.Logger;

public class MessageParser {
	
	public static final long UINT32_MAX = 4294967296L;
	
	public static final byte MSG_START = 2;
	public static final byte MSG_END = 3;
	public static final byte MSG_SEPARATOR = 29;
	
	public static final int MSG_BUFFER_SIZE = 30;
	
	ArrayBlockingQueue<Message> msgIn;
	ArrayBlockingQueue<Message> msgOut;
	
	Logger log;
	Connection c;
	Thread send;
	Thread receive;
	
	public MessageParser(ArrayBlockingQueue<Message> msgIn, Connection c) {
		this.c = c;
		this.msgIn = msgIn;
		msgOut = new ArrayBlockingQueue<Message>(MSG_BUFFER_SIZE);
		log = Configuration.getCurrent().getLogger();
		send = new Thread() {
			public void run() {
				sendingTask();
			}
		};
		receive = new Thread() {
			public void run() {
				receivingTask();
			}
		};
		send.start();
		receive.start();
	}

	public MessageParser(Connection c) {
		this.c = c;
		msgIn = new ArrayBlockingQueue<Message>(MSG_BUFFER_SIZE);
		msgOut = new ArrayBlockingQueue<Message>(MSG_BUFFER_SIZE);
		log = Configuration.getCurrent().getLogger();
		send = new Thread() {
			public void run() {
				sendingTask();
			}
		};
		receive = new Thread() {
			public void run() {
				receivingTask();
			}
		};
		send.start();
		receive.start();
	}
	
	public boolean isConnected() {
		return c.isConnected();
	}
	
	public Message readMessage() throws InterruptedException {
		return msgIn.take();
	}
	
	public void writeMessage(Message msg) throws InterruptedException {
		msgOut.put(msg);
	}
	
	public Connection getConnection() {
		return c;
	}
	
	public void receivingTask() {
		byte[] b = new byte[1];
		ArrayList<Byte> msgBytes = new ArrayList<Byte>();
		log.log("Server connection receivingTask started",Logger.DEBUG);
		try {
			while(c.read(b)>0) {
				switch(b[0]) {
				case MSG_START:
					msgBytes.clear();
					break;
				case MSG_END:
					addMessage(msgBytes.toArray(new Byte[0]));
					break;
				default:
					msgBytes.add(b[0]);
					break;
				}
			}
		} catch(Exception e) {log.log("Server connection receivingTask exception: "+e.getClass()+" "+e.getMessage(),Logger.ERROR);}
		c.close();
		log.log("Server connection receivingTask terminated",Logger.DEBUG);
		send.interrupt();
	}
	
	public void sendingTask() {
		Message msg;
		ArrayList<Byte> b = new ArrayList<Byte>();
		log.log("Server connection sendingTask started",Logger.DEBUG);
		try {
			while(true) {
				b.clear();
				msg = msgOut.take();
				b.add(MSG_START);
				addBytes(b,String.valueOf(msg.id).getBytes());
				b.add(MSG_SEPARATOR);
				addBytes(b,msg.tipo.getBytes());
				b.add(MSG_SEPARATOR);
				addBytes(b,msg.msg.getBytes());
				b.add(MSG_END);
				c.write(toPrimitives(b.toArray(new Byte[0])));
			}
		} catch(Exception e) {}
		c.close();
		log.log("Server connection sendingTask terminated",Logger.DEBUG);
	}
	
	public void addMessage(Byte[] msgBytes) {
		try {
			Message msg = new Message();
			String[] parts = split(msgBytes,MSG_SEPARATOR);
			msg.id = Integer.valueOf(parts[0]);
			msg.tipo = parts[1];
			msg.msg = parts[2];
			msg.origin = this;
			msgIn.put(msg);
		} catch(Exception e) {
			log.log("Error adding message: "+e.getClass().getName()+" "+e.getMessage(), Logger.DEBUG);
		}
	}
	
	public String[] split(Byte[] b, byte sep) throws UnsupportedEncodingException {
		ArrayList<String> all = new ArrayList<String>();
		ArrayList<Byte> split = new ArrayList<Byte>();
		for(int i = 0 ; i < b.length ; i++) {
			if(b[i] == MSG_SEPARATOR) {
				all.add(new String(toPrimitives(split.toArray(new Byte[0])),"ASCII"));
				split.clear();
			}
			else split.add(b[i]);
		}
		all.add(new String(toPrimitives(split.toArray(new Byte[0])),"ASCII"));
		return all.toArray(new String[0]);
	}
	
	public void addBytes(ArrayList<Byte> list, byte[] b) {
		for(int i = 0 ; i < b.length ; i++) {
			list.add(b[i]);
		}
	}
	
	public byte[] toPrimitives(Byte[] oBytes) {
	    byte[] bytes = new byte[oBytes.length];
	    for(int i = 0; i < oBytes.length; i++) {
	        bytes[i] = oBytes[i];
	    }
	    return bytes;
	}
	
}
