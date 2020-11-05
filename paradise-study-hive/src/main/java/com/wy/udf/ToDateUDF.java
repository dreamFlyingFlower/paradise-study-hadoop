package com.wy.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义将日期字符串格式化为日期对象
 * 
 * @author ParadiseWY
 * @date 2020-11-05 13:46:48
 * @git {@link https://github.com/mygodness100}
 */
@Description(name = "ToDate", value = "toDate()", extended = "toDate_xxxx-ext")
public class ToDateUDF extends GenericUDF {

	public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
		return null;
	}

	public Object evaluate(DeferredObject[] args) throws HiveException {
		if (args != null && args.length != 0) {
			// 指定日期对象的格式化串
			if (args.length == 1) {
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf.applyPattern("yyyy/MM/dd hh:mm:ss");
				try {
					return sdf.parse((String) (args[0].get()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				// 两个参数,Date date,String frt
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf.applyPattern((String) args[1].get());
				try {
					return sdf.parse((String) args[0].get());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} else {
			// 无参,返回系统时间对象
			return new Date();
		}
		return null;
	}

	public String getDisplayString(String[] children) {
		return "toChar_xxx";
	}
}