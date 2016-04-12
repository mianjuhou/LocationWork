package com.jtv.video.recorder.helper;

import java.io.File;

import com.jtv.video.recorder.util.FileUtil;
import com.yixia.weibo.sdk.VCamera;

import android.content.Context;

public class VCameraUtil {

	public static void init(Context con) {

		File dir = con.getDir("vcamera", Context.MODE_PRIVATE);

		FileUtil.delete(dir);

		dir = con.getDir("vcamera", Context.MODE_PRIVATE);

		// 设置拍摄视频缓存路径
		VCamera.setVideoCachePath(dir.getAbsolutePath() + File.separator);

		// 开启log输出,ffmpeg输出到logcat
		VCamera.setDebugMode(true);

		// 初始化拍摄SDK，必须
		VCamera.initialize(con);
	}
}
