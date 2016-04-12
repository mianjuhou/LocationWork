package com.jtv.locationwork.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ct.au;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;


/**
 * @author: zn E-mail:zhangn@jtv.com.cn 
 * @version:2014-11-11
 * ������Ƶ�ļ�(����������raw��asset�ļ����µ���Ƶ�ļ��Լ�sd���е���Ƶ�ļ�)
 * 1.�첽���� ��Ƶ�ļ�
 * 2.����ͨ���ı��ϳ����������غ󱣴��ڱ��غ� ���ڽ�����Ƶ�ļ��Ĳ���
 * 3.�������غϳ��������첽����
 */
public class MediaPlayHelper {
	
	public static  MediaPlayHelper mediaPlayHelper=null;
	public  MediaPlayer mediaPlayer=null;
	String filepath=null;//sd���������Ƶ·��
	public final static int RAWFLAG=0;//raw�ļ����µ���Ƶ��ʶ
	public final static int ASSETFLAG=1;//asset�ļ����µ���Ƶ��ʶ
	public final static int SDFLAG=2;//sd�µ���Ƶ��ʶ
	
	
	public static MediaPlayHelper getMediaPlayHelper(){
		mediaPlayHelper=new MediaPlayHelper();
		return mediaPlayHelper;
	}
	
	/**
	 * ������raw�ļ����µ���Ƶ�ļ�
	 * @param mContext
	 * @param resourceMedia(��Ƶ��ԴID)
	 */
	/**
	 * ������raw�ļ����µ���Ƶ�ļ�
	 * @author:zn
	 * @version:2014-11-12
	 * @param mContext
	 * @param resMediaName(��Ƶ��ԴID)
	 */
	public void startRawMedia(Context mContext,int resMediaName){
		mediaPlayer = MediaPlayer.create(mContext,resMediaName);
		// ׼������
		try {
			// ����
			mediaPlayer.start();
			
			if(!mediaPlayer.isPlaying() && mediaPlayer!=null){
				stopMedia(mediaPlayer);
			}
		} catch (IllegalStateException e) {
			Log.e("error", "startRawMedia:"+e.getMessage());
		}
	}
	
	/**
	 * ������asset�ļ����µ���Ƶ�ļ�
	 * @author:zn
	 * @version:2014-11-12
	 * @param mContext
	 * @param resMediaName(��Ƶ����)
	 */
	public void startAssetMedia(Context mContext,String resMediaName){
		AssetManager assetManager = mContext.getAssets();
		try {
			//��ȡָ���ļ���Ӧ��AssetFileDescriptor
			AssetFileDescriptor fileDescriptor = assetManager.openFd(resMediaName);
			mediaPlayer = new MediaPlayer();
			//ʹ��MediaPlayer����ָ���������ļ�
			mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),fileDescriptor.getLength());
			mediaPlayer.prepare();
			mediaPlayer.start();
			if(!mediaPlayer.isPlaying() && mediaPlayer!=null){
				stopMedia(mediaPlayer);
			}
			
		} catch (IOException e) {
			assetManager=null;
			Log.e("error", "startAssetMedia:"+e.getMessage());
		}
	}

	/**
	 * ����ָ��SD������Ƶ
	 * @author:zn
	 * @version:2014-11-12
	 * @param mContext 
	 * @param filepath(��Ҫ������Ƶ·��)
	 */
	public void startSDMedia(Context mContext, String filepath){
		setAutioVolum(mContext);
		
		mediaPlayer = new MediaPlayer();
		if(listener!=null){
			mediaPlayer.setOnCompletionListener(listener);
		}
		
		if(filepath.length()>0){
			mediaPlayer = new MediaPlayer();
			
			if(listener!=null){
				mediaPlayer.setOnCompletionListener(listener);
			}
			//����
			mediaPlayer.reset();
			//��������Դ
			try {
				
				File file = new File(filepath);
				 FileInputStream fis = new FileInputStream(file);
				 mediaPlayer.setDataSource(fis.getFD());
				// ׼������
				mediaPlayer.prepare();
				// ����
				mediaPlayer.start();
				//�����Ƶ������ɻ�����Դ
				if(!mediaPlayer.isPlaying() && mediaPlayer!=null){
					stopMedia(mediaPlayer);
				}
			} catch (IllegalArgumentException e) {
				Log.e("error", "startMedia:"+e.getMessage());
			} catch (IllegalStateException e) {
				Log.e("error", "startMedia:"+e.getMessage());
			} catch (IOException e) {
				Log.e("error", "startMedia:"+e.getMessage());
			}
		}
	}

	private void setAutioVolum(Context mContext) {
		AudioManager audioMgr=(AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		int maxValum=audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int curValum=(int) (maxValum*0.8);
		audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curValum, AudioManager.FLAG_PLAY_SOUND);
	}

	/**
	 * ��ͣ������Ƶ
	 * @author:zn
	 * @version:2014-11-12
	 * @param player
	 */
	public void pauseMedia(MediaPlayer player){
		if (player != null) {
			player.pause();
		}
	}
	private OnCompletionListener listener;
	public void setPlayCompleListener(OnCompletionListener listener){
		
		this.listener= listener;
		
		if(mediaPlayer!=null){
			mediaPlayer.setOnCompletionListener(listener);
		}
		
	}

	/**
	 * ֹͣ��Ƶ
	 * @author:zn
	 * @version:2014-11-12
	 * @param player
	 */
	public void stopMedia(MediaPlayer player){
		if (player != null) {
			player.stop();
			//�ͷ���Դ
			player.release();
			player = null;
		}
	}
	
	/**
	 * �жϵ�ǰ����Ƶ�Ĵ��λ��
	 * @author:zn
	 * @version:2014-11-12
	 * @param mContext
	 * @param flag
	 * 		0:������Դ�ļ���raw�е���Ƶ�ļ�
	 * 		1������assect����Ƶ�ļ�
	 * 		2.����ָ��Ŀ¼��sd���е���Ƶ�ļ�
	 * @param resID(���ŵ���ԴID)
	 * @param filefullpath(����SD������Ƶ�ļ�������·�������ǲ���raw�ļ��е���Ƶ����)
	 */
	public void selectMediaSource(Context mContext,int flag,int resID,String filefullpath){
		//setMediaPlayerVolume();
		switch (flag) {
		case MediaPlayHelper.RAWFLAG:
			startRawMedia(mContext,resID);
			break;
		case MediaPlayHelper.ASSETFLAG:
			startAssetMedia(mContext, filefullpath);
			break;
		case MediaPlayHelper.SDFLAG:
			startSDMedia(mContext,filefullpath);
			break;

		default:
			break;
		}
	}
	/**
	 * �첽������Ƶ(ͨ������·�������첽����mp3�ļ�)
	 * @author:zn
	 * @version:2014-11-12
	 * @param mContext
	 * @param flag(��ʶ)
	 * 		0:������Դ�ļ���raw�е���Ƶ�ļ�
	 * 		1������assect����Ƶ�ļ�
	 * 		2.����ָ��Ŀ¼��sd���е���Ƶ�ļ�
	 * @param resID(��ԴID)
	 * @param filefullpath(Ҫ������Ƶ������·��)
	 */
	public void playMedia(final Context mContext,final int flag,final int resID,final String filefullpath){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				selectMediaSource(mContext,flag,resID,filefullpath);
			}
		}).start();
	}
}
