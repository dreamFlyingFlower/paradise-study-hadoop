package com.wy.order;

import org.apache.hadoop.io.WritableComparator;

import com.wy.model.Order;

public class OrderGroupingCompartor extends WritableComparator {

	// 写一个空参构造
	public OrderGroupingCompartor() {
		super(Order.class, true);
	}

	// 重写比较的方法
	@Override
	public int compare(Object a, Object b) {
		Order aBean = (Order) a;
		Order bBean = (Order) b;
		// 根据订单id号比较,判断是否是一组
		return aBean.getOrderId().compareTo(bBean.getOrderId());
	}
}