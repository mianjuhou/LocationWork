package com.jtv.locationwork.util;

import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;


public class TTSUtil {
	private TextToSpeech tts;

	public TTSUtil(Context ctx) {
		tts = new TextToSpeech(ctx, new OnInitListener() {
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					tts.setLanguage(Locale.CHINESE);
				}
			}
		});
	}

	public void speak(String name) {
		try {
			tts.speak(name, TextToSpeech.QUEUE_FLUSH, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	};

	public boolean isSpeaking() {
		return tts.isSpeaking();
	}

	public void onDestroy() {
		try {

			tts.stop();
			tts.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
