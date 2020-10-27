package com.wy.model;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

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
public class Table implements Writable {

	/**
	 * 订单编号
	 */
	private String order_id;

	/**
	 * 产品编号
	 */
	private String p_id;

	/**
	 * 产品数量
	 */
	private int amount;

	/**
	 * 产品名称
	 */
	private String pname;

	/**
	 * 表的标记
	 */
	private String flag;

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(order_id);
		out.writeUTF(p_id);
		out.writeInt(amount);
		out.writeUTF(pname);
		out.writeUTF(flag);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.order_id = in.readUTF();
		this.p_id = in.readUTF();
		this.amount = in.readInt();
		this.pname = in.readUTF();
		this.flag = in.readUTF();
	}

	@Override
	public String toString() {
		return order_id + "\t" + p_id + "\t" + amount + "\t" + pname;
	}
}