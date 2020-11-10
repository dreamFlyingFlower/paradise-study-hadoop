package com.wy.example.weibo;

import java.util.List;

import com.wy.model.Message;

/**
 * 
 * 
 * @author ParadiseWY
 * @date 2020-11-10 16:39:53
 * @git {@link https://github.com/mygodness100}
 */
public class TestWeibo {

	public static void main(String[] args) {
		CreateHiveMeta meta = new CreateHiveMeta();
		OperateWeibo operateWeibo = new OperateWeibo();
		meta.initTable();
		TestWeibo weibo = new TestWeibo();
		weibo.testPublishContent(operateWeibo);
		weibo.testAddAttend(operateWeibo);
		weibo.testShowMessage(operateWeibo);
		weibo.testRemoveAttend(operateWeibo);
		weibo.testShowMessage(operateWeibo);
	}

	public void testPublishContent(OperateWeibo wb) {
		wb.publishContent("0001", "今天买了一包空气，送了点薯片，非常开心！！");
		wb.publishContent("0001", "今天天气不错。");
	}

	public void testAddAttend(OperateWeibo wb) {
		wb.publishContent("0008", "准备下课！");
		wb.publishContent("0009", "准备关机！");
		wb.addAttends("0001", "0008", "0009");
	}

	public void testRemoveAttend(OperateWeibo wb) {
		wb.removeAttends("0001", "0008");
	}

	public void testShowMessage(OperateWeibo wb) {
		List<Message> messages = wb.getAttendsContent("0001");
		for (Message message : messages) {
			System.out.println(message);
		}
	}
}