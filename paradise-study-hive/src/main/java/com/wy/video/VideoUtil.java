package com.wy.video;

/**
 * 数据拆分,将数据拆分为Hive可是被形式
 * 
 * @author ParadiseWY
 * @date 2020-11-05 09:43:03
 * @git {@link https://github.com/mygodness100}
 */
public class VideoUtil {

	public static String ori2ETL(String original) {
		StringBuilder etlString = new StringBuilder();
		String[] splits = original.split("\t");
		if (splits.length < 9) {
			return null;
		}
		splits[3] = splits[3].replace("  ", "");
		for (int i = 0; i < splits.length; i++) {
			if (i < 9) {
				if (i == splits.length - 1) {
					etlString.append(splits[i]);
				} else {
					etlString.append(splits[i] + "\t");
				}
			} else {
				if (i == splits.length - 1) {
					etlString.append(splits[i]);
				} else {
					etlString.append(splits[i] + "&");
				}
			}
		}
		return etlString.toString();
	}
}