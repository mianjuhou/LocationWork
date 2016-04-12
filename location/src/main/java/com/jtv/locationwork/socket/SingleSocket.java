package com.jtv.locationwork.socket;

import java.io.IOException;
import java.net.Socket;

import android.util.Log;

public abstract class SingleSocket {
	private static Socket mSock = null;

	protected SingleSocket() {
	}

	public Socket getSingleSocket() {
		if (mSock == null) {
			mSock = creatSocket();
		}
		if (mSock != null && mSock.isClosed()) {
			Log.i("Socket Thread ", "socket by closeed ,need start creat socket.... ");
			closeSocket();
			mSock = creatSocket();
		}
		return mSock;
	}

	public static void closeSocket() {
		if (mSock != null) {
			try {
				mSock.close();
			} catch (IOException e) {
				Log.e("socket Thread ", "close socket sock.......   " + e);
			}
			try {
				mSock.shutdownInput();
			} catch (IOException e) {
				Log.e("socket Thread ", "close socket io.......   " + e);
			}
			try {
				mSock.shutdownOutput();
			} catch (IOException e) {
				Log.e("socket Thread ", "close socket io.......   " + e);
			}
			mSock = null;
		}
	}

	protected abstract Socket creatSocket();
}
