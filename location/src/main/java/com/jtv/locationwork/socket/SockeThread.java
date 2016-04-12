package com.jtv.locationwork.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import com.jtv.locationwork.util.LogUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class SockeThread extends Thread {

	public static final int ERROR_NOT_HOST = 100208;

	public static final int ERROR_NOT_PORT = 100207;

	public static final int ERROR_IO = 100206;

	public static final int ERROR_EXCEPTION = 100205;

	public static final int OK = 200204;

	public static final int RECEIVER_DATA = 300209;

	public Socket sock;

	private BufferedReader bufferedReader;

	public Context con;

	private long time = -1;

	public Handler handler;

	private int pool = 2048;// 默认的池子大小

	private String TAG = "SOCKET";

	public void start() {
		super.start();
	}

	public void setSleepThread(long time) {
		this.time = time;
	}

	public abstract void sendSocket(String date) throws IOException;

	/**
	 * 接收到数据允许在子线程中
	 * 
	 * @param obj
	 * @throws IOException
	 */
	public abstract void receiverData(Object obj);

	public SockeThread(Context con, Socket sock) {
		this(con, sock, null);
	}

	protected SockeThread() {
	}

	public SockeThread(Context con, Socket sock, Handler handler) {
		this.sock = sock;
		this.con = con;
		this.handler = handler;
	}

	public BufferedReader getBufferRead() {
		if (getInputStream() != null) {
			if (bufferedReader == null) {
				bufferedReader = new BufferedReader(new InputStreamReader(getInputStream()));
			}
		}
		return bufferedReader;
	}

	public boolean isClosed() {
		if (sock == null) {
			return true;
		}
		return sock.isClosed();
	}

	@Override
	public void run() {
		super.run();
		while (!this.isInterrupted()) {// 代表没有结束
			if (sock == null) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			// bufferRead = getBufferRead();
			// inputStream = getInputStream();
			if (time > 0) {
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					LogUtil.e("socket Thread ", "sleep interrupedexception......   " + e);
				}
			}
			LogUtil.i(TAG, "线程正在执行:读取数据");
			if (this.isInterrupted()) {
				continue;
			}
			String date = readDate();
			LogUtil.i(TAG, "线程正在执行:读取数据结束");
			if ("".equals(date) || null == date) {
				continue;
			}
			if (handler != null) {
				Message obtain = Message.obtain();
				obtain.what = RECEIVER_DATA;
				obtain.obj = date;
				handler.sendMessage(obtain);
			}
			receiverData(date);
			// new ReceiverThread(date).start();
		}

	}

	class ReceiverThread extends Thread {
		String date;

		public ReceiverThread(String date) {
			this.date = date;
		}

		@Override
		public void run() {
			super.run();
			SockeThread.this.receiverData(date);
		}
	}

	public String readBuffer() {
		BufferedReader in = new BufferedReader(new InputStreamReader(getInputStream()));
		String line = null;
		try {
			line = in.readLine();
		} catch (IOException e) {
			LogUtil.e(TAG, e);
			closeSocket();
		}
		return line;
	}

	/**
	 * 读取数据来自后台
	 * 
	 * @return
	 */
	private String readDate() {
//		if (true) {
//			return readBuffer();
//		}
		int datePool;
		byte[] buffer = null;
		String date = null;
		try {
			datePool = getInputStream().available();

			if (datePool > pool) {
				pool = datePool;
			}
			buffer = new byte[pool];
			int len = -1;
			len = getInputStream().read(buffer);
			if (len > 0) {
				date = new String(buffer, 0, len).trim();
			} else if(len < 0){
				// Log.e("Socket Thread", "response services date "+len);
				throw new IOException("与服务端连接失败");
			}
		} catch (IOException e) {
			closeSocket();
		} catch (Exception e) {
		}
		buffer = null;
		return date;
	}

	public InputStream getInputStream() {
		try {
			if (sock == null) {
				return null;
			}
			return sock.getInputStream();
		} catch (IOException e) {
			LogUtil.e("socket Thread ", "socket getinputstream IO.......   " + e);
		}
		return null;
	}

	public OutputStream getOutPutStream() {
		try {
			if (sock == null) {
				return null;
			}
			return sock.getOutputStream();
		} catch (IOException e) {
			LogUtil.e("socket Thread ", "socket getoutputstream  IO .......   " + e);
		}
		return null;
	}

	public void closeSocket() {
		LogUtil.i("Socket thread ", "Socket close end .......... ");
		interrupt();
		InputStream inputStream = getInputStream();
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				LogUtil.e("socket Thread ", "close socket io.......   " + e);
			}
			inputStream = null;
		}
		if (bufferedReader != null) {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				Log.e("socket Thread ", "close socket IOread.......   " + e.getCause());
			}
			bufferedReader = null;
		}
		OutputStream outPutStream = getOutPutStream();
		if (outPutStream != null) {
			try {
				outPutStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			outPutStream = null;
		}
		if (sock != null) {

			try {
				sock.shutdownInput();
				sock.shutdownOutput();

			} catch (IOException e) {
				Log.e("socket Thread ", "close socket ioput.......   " + e.getCause());
			}
			try {
				sock.close();
			} catch (IOException e) {
				Log.e("socket Thread ", "close socket sock.......   " + e.getCause());
				try {
					sock.close();
				} catch (IOException e1) {
					Log.e("socket Thread ", "close socket sock.......   " + e.getCause());
				}
			}
			sock = null;
		}
	}

}
