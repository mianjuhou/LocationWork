package com.jtv.locationwork.socket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class SockThreadImp extends SockeThread {


	public SockThreadImp(Context con, Socket sock) {
		super(con, sock);
	}

	public SockThreadImp(Context con, Socket sock, Handler handler) {
		super(con, sock, handler);
	}

	@Override
	public void sendSocket(String date) throws IOException {
		if (sock != null) {
			OutputStream outputStream = getOutPutStream();
//			if (outputStream == null) {
//				outputStream = sock.getOutputStream();
//			}
			outputStream.write(date.getBytes());
			outputStream.flush();
			Log.i("socket thread ", "send to services  date [ " + date + " ]");
		} else { 
			try{
				throw new IOException();
			}catch(Exception e){
				Log.i("socket thread ", "send to services  date [ " + date + " ] error .."+e);
			}
			
		}
	}

	@Override
	public void receiverData(Object obj){
		
	}

	@Override
	public void closeSocket() {
		super.closeSocket();
	}
}
