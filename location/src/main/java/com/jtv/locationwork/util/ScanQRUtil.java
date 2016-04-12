package com.jtv.locationwork.util;

import android.device.ScanManager;
import android.util.Log;

//红外线扫描的util
public class ScanQRUtil {
	private static ScanManager scan = null;
	public static final String TAG = "SCAN";

	public static void openScanQR() {
		try {
			scan = getScanManager();
			if (scan == null) {
				return;
			}

			scan.openScanner();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	// 设置值为焦点框中
	public static void setOutputInFocs() {
		try {
			scan = getScanManager();
			if (scan == null) {
				return;
			}
			scan.switchOutputMode(1);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加一个回车符
	 */
	public static void appendEnter() {
		scan = getScanManager();
		if (scan == null) {
			return;
		}
		try {
			scan.setAppend();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	// 停止触发器扫描，不允许用按钮打开红外线
	public static void stopTrigglerScan() {
		try {
			scan = getScanManager();
			if (scan == null) {
				return;
			}
			scan.lockTriggler();
		} catch (Throwable e) {
		}

	}

	// 开启触发器扫描，允许用按钮打开红外线
	public static void startTrigglerScan() {

		try {
			scan = getScanManager();
			if (scan == null) {
				return;
			}
			scan.unlockTriggler();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public static void closeScanQR() {
		try {
			scan = getScanManager();
			if (scan == null) {
				return;
			}
			scan.closeScanner();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static ScanManager getScanManager() {
		if (scan == null) {
			try {
				scan = new ScanManager();
			} catch (Throwable e) {
				Log.i(TAG, "不支持红外线扫描");
			}

		}
		return scan;
	}
}
