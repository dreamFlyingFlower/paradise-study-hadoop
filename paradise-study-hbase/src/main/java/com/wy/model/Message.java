package com.wy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 微博信息表
 * 
 * @author ParadiseWY
 * @date 2020-11-10 16:31:24
 * @git {@link https://github.com/mygodness100}
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

	private String uid;

	private String timestamp;

	private String content;
}