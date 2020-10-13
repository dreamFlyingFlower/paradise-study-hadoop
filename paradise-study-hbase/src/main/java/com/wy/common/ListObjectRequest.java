package com.wy.common;

import lombok.Data;

/**
 * @apiNote 
 * @author ParadiseWY
 * @date 2020年2月12日 下午1:39:14
 */
@Data
public class ListObjectRequest {
	private String bucket;
	  private String startKey;
	  private String endKey;
	  private String prefix;
	  private int maxKeyNumber;
	  private String listId;
}