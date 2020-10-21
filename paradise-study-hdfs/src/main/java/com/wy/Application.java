package com.wy;

/**
 * 主要是实现Mapper,Reducer接口,使用FileInputFormat及其实现类对文件进行操作
 * 
 * @apiNote FileInputFormat有多种实现类:TextInputFormat,KeyValueTextInputFormat,NLineInputFormat,
 *          CombineTextInputFormat以及自定义的InputFormat等
 * 
 * @apiNote NLineInputFormat:每个map进程处理的InputFormat不再按Block块去划分,而是按照指定的行数N划分.
 *          即输入文件的总行数/N=切片数,若不整除,向上取整
 * 
 * @author ParadiseWY
 * @date 2020-10-21 16:45:38
 * @git {@link https://github.com/mygodness100}
 */
public class Application {

	public static void main(String[] args) {
	}
}