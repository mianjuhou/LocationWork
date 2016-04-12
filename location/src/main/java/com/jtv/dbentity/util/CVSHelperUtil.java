package com.jtv.dbentity.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.database.Cursor;

public class CVSHelperUtil {

	/**
	 * 导出为cvs文件
	 * 
	 * @param cusor
	 *            内容
	 * @param strPath
	 *            存储的地址
	 * @return 是否操作成功
	 * @throws IOException
	 *             报错
	 */
	public static boolean exportCVS(Cursor cusor, String strPath) throws IOException {

		if (cusor == null || strPath == null) {
			return false;
		}

		int columnCount = cusor.getColumnCount();
		String[] columnNames = cusor.getColumnNames();

		File file = new File(strPath);
		
		if (file.exists()) {
			file.delete();
		}
		
		StringBuilder strColu = new StringBuilder();
		StringBuilder strValue = null;
		FileWriter write = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(write);
		
		for (int j = 0; j < columnCount; j++) {
			strColu.append(columnNames[j]);
			if (j < columnCount - 1)
				strColu.append(",");
		}
		
		// strColu.delete(strColu.length() - 1, 1);// 移出掉最后一个,字符
		bw.write(strColu.toString());
		bw.newLine();

		while (cusor.moveToNext()) {
			strValue = new StringBuilder();
			for (int j = 0; j < columnCount; j++) {
				String value = cusor.getString(cusor.getColumnIndex(columnNames[j]));
				strValue.append(value);
				if (j < columnCount - 1)
					strValue.append(",");
			}
			bw.write(strValue.toString());
			bw.newLine();
		}
		
		bw.close();
		write.close();
		cusor.close();
		strColu = null;
		strValue = null;
		return true;
	}
}
