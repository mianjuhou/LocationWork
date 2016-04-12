package com.jtv.locationwork.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;


/**
 * @author: zn E-mail:zhangn@jtv.com.cn 
 * @version:2015-3-2
 * 类说明:录制PCM音频，播放PCM音频
 */

public class MediaUtil {
	private String TAG="MediaUtil";
	public static MediaUtil instance=null;
	private  AudioRecord mRecord;  //录音
    /**音频获取源  */
    private int audioSource = MediaRecorder.AudioSource.MIC;
    /**设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025  */
    private static int sampleRateInHz = 16000;// 44100;  
    /**设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道   */
    private static int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;// AudioFormat.CHANNEL_IN_STEREO;  
    /**音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。  */
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;  
    // 音频大小  
    private int minBufSize;  
    private boolean isRecording = false;
    private PlayMediaThread playRecordThread=null;
    private byte[] data =null;
	/***
	 * 单例模式
	 * @return MediaUtil
	 */
	public synchronized static MediaUtil getInstance() {
		if (instance == null) {
			instance = new MediaUtil();
		}
		return instance;
	}
	/**
	 * 录音音频
	 * @author:zn
	 * @version:2015-3-2
	 * @param pathName(文件夹路径)
	 * @param mediaName(音频存放路径)
	 */
	public void startRecord(String mediaName){
		minBufSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
		mRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, minBufSize);
		isRecording =true;
		Log.i("media", mediaName);
		File mediaFile=new File(mediaName);
		if(mediaFile.exists())
			mediaFile.delete();
		writeFile2SD(mediaName);
	}
	
	/**
	 * 停止录制音频
	 * @author:zn
	 * @version:2015-2-26
	 * @param isRecording
	 */
	public void stopRecord(boolean isRecording){
		//isRecording = false;
		this.isRecording=isRecording;
	}
	
	/**
	 * 播放录制音频
	 * @author:zn
	 * @version:2015-3-2
	 * @param pathName
	 * @param handler
	 */
	public void playRecord(String pathName){
		data = getPCMData(pathName);//获取PCM音频数据
		// MyAudioTrack:   对AudioTrack进行简单封装的类
		playRecordThread=new PlayMediaThread(data);
		playRecordThread.start();
	}
	
	/**
	 * 停止播放录制音频
	 * @author:zn
	 * @version:2015-3-2
	 */
	public void stopPlayRecord(){
		if (playRecordThread != null){
			playRecordThread.interrupt();
			playRecordThread = null;
			data=null;
    	}
	}
	

	/**
	 * 将音频写入SD卡
	 * @author:zn
	 * @version:2015-3-2
	 * @param pathName
	 * @param mediaName
	 */
	private void writeFile2SD(final String mediaName) {
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					mRecord.startRecording();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
				
		        // new一个byte数组用来存一些字节数据，大小为缓冲区大小  
		        byte[] audiodata = new byte[minBufSize];  
		        FileOutputStream fos = null;  
		        int readsize = 0;  
		        try {  
		        	File file = createDirs(mediaName);  
		            fos = new FileOutputStream(file);// 建立一个可存取字节的文件  
		            while (isRecording == true) {  
			            readsize = mRecord.read(audiodata, 0, minBufSize);  
			            Log.i("采集大小", String.valueOf(readsize));  
			            if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {  
			                try {  
			                	if(audiodata!=null)
			                		fos.write(audiodata);  
			                } catch (IOException e) {  
			                    Log.e(TAG, e.getMessage());
			                }  
			            }  
			        }  
		            if(!isRecording){
		            	mRecord.stop();
		            }
		        } catch (Exception e) {  
		        	Log.e(TAG, e.getMessage());
		        }finally{
		        	 try {  
				            fos.close();// 关闭写入流  
				        } catch (IOException e) {  
				        	Log.e(TAG, e.getMessage());
				        }  
		        }
			}
		}).start();
	}
	
	/**
	 * 创建文件夹
	 * @author:zn
	 * @version:2015-3-2
	 * @param pathName
	 * @param mediaName
	 * @return
	 */
	private File createDirs(String mediaName) {
		File file = new File(mediaName);  
		
		String pathName = file.getParent();
		File file_up = new File(pathName);
		if(!file_up.exists())
			file_up.mkdirs();
		
		if (file.exists()) {  
		    file.delete();  
		}
		return file;
	}
	
	/**
	 * 获取PCM音频数据
	 * @author:zn
	 * @version:2015-3-2
	 * @param filePath(PCM音频存档路径)
	 * @return
	 */
	@SuppressWarnings("resource")
	public byte[] getPCMData(String filePath){
    	File file = new File(filePath);
    	FileInputStream inStream=null;
    	byte[] data_pack = null;
		try {
			inStream = new FileInputStream(file);
	    	if (inStream != null){
	    		long size = file.length();
	    		data_pack = new byte[(int) size];
	    		inStream.read(data_pack);
	    	}
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
    	return data_pack;
    }
	
	/**
	 * 播放音频线程
	 * @author:zn
	 * @version:2015-3-2
	 */
	class PlayMediaThread extends Thread{
		byte[] data=null;
		Handler mHandler=null;
		public PlayMediaThread(Handler handler,byte[] mdata){
			this.mHandler=handler;
			data=mdata;
		}
		public PlayMediaThread(byte[] mdata){
			data=mdata;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (data == null || data.length == 0){
				return ;
			}
			MyAudioTrack myAudioTrack = new MyAudioTrack(sampleRateInHz,channelConfig, AudioFormat.ENCODING_PCM_16BIT);
			myAudioTrack.init();
			int playSize = myAudioTrack.getPrimePlaySize();
			
			Log.i("MyThread", "total data size = " + data.length + ", playSize = " + playSize);
			
			int index = 0;
			int offset = 0;
			while(true){
				try {
					Thread.sleep(0);//便于中断
					offset = index * playSize;
					// 这里是真正播放音频数据的地方
					myAudioTrack.playAudioTrack(data, offset, playSize);
				} catch (Exception e) {
					break;
				}
				
				index++;
				if (index >= data.length){
					break;
				}
			}
			myAudioTrack.release();
//			Message msg =mHandler.obtainMessage();
//			msg.arg1=11;
//			mHandler.sendMessage(msg);
		}
	}
	
}
