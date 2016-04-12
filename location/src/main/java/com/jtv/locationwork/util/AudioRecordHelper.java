package com.jtv.locationwork.util;

import java.io.IOException;

import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;

public class AudioRecordHelper {

	protected static MediaRecorder mr;
	protected String path = "";

	public MediaRecorder getMediaRecorder(String path) {
		stop();
		release();
		mr = new MediaRecorder();
		init(path);
		return mr;
	}

	public void initial(String path) {
		this.path = path;
		getMediaRecorder(path);
	}

	public void stop() {

		if (mr != null) {
			try {
				mr.stop();
			} catch (Exception e) {
			}
		
			mr=null;
		}
	}

	public void prepare() throws IllegalStateException, IOException {
		if (mr != null) {
			// try {
			mr.prepare();
			// } catch (IllegalStateException e) {
			// mr.prepare();
			// }
		}
	}

	public void start() {
		if (mr != null) {
			mr.start();
		}
	}

	public void release() {
		if (mr != null) {
			mr.release();
			mr=null;
		}
	}

	protected void init(String audioPath) {

		mr.setAudioSource(AudioSource.MIC);
		mr.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB); // 设置声音编码的格式
		mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		mr.setOutputFile(audioPath);
	}
}
