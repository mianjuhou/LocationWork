package com.spore.jni;

public class ImageUtilEngine {

	static {
		System.loadLibrary("yuv2rgb");
		//System.loadLibrary("JNITest");
		                    
	}
	//public native byte[] decodeYUV420SP(byte[] buf, int width, int height);
	public static native void decodeYUV420SP2RGB24( byte[] rgb, byte[] buf, int width, int height);

}
