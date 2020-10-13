package com.wy.common;

import java.util.List;

import lombok.Data;

/**
 * @apiNote 对象列表
 * @author ParadiseWY
 * @date 2020年2月10日 上午12:12:00
 */
@Data
public class ObjectListResult {
	private String bucket;

	private String maxKey;

	private String minKey;

	private String nextMarker;

	private int maxKeyNumber;

	private int objectCount;

	private String listId;

	private List<HOSObjectSummary> objectSummaryList;
}