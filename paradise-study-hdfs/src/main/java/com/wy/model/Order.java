package com.wy.model;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order implements WritableComparable<Order> {

	private String orderId;

	private Double price;

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(orderId);
		out.writeDouble(price);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.orderId = in.readUTF();
		this.price = in.readDouble();
	}

	@Override
	public int compareTo(Order o) {
		// 两次排序
		// 1 按照id号排序
		int comResult = this.orderId.compareTo(o.getOrderId());
		if (comResult == 0) {
			// 2 按照价格倒序排序
			comResult = this.price > o.getPrice() ? -1 : 1;
		}
		return comResult;
	}

	@Override
	public String toString() {
		return orderId + "\t" + price;
	}
}