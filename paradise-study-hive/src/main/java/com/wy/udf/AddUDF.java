package com.wy.udf;

import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * 自定义函数:必须含有evaluate方法,该方法可重载.倾向于继承GenericUDF方法
 * 
 * @author ParadiseWY
 * @date 2020-11-05 13:43:00
 * @git {@link https://github.com/mygodness100}
 */
public class AddUDF extends UDF {

	public int evaluate(int a, int b) {
		return a + b;
	}

	public int evaluate(int a, int b, int c) {
		return a + b + c;
	}
}