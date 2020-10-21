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

/**
 * 序列化实体类,必须有空构造,否则后续反射会出问题
 * 
 * @author ParadiseWY
 * @date 2020-10-21 16:20:03
 * @git {@link https://github.com/mygodness100}
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flow implements Writable {

	/**
	 * 上行流量
	 */
	private long upFlow;

	/**
	 * 下行流量
	 */
	private long downFlow;

	/**
	 * 总流量
	 */
	private long sumFlow;

	public Flow(long upFlow, long downFlow) {
		super();
		this.upFlow = upFlow;
		this.downFlow = downFlow;
		sumFlow = upFlow + downFlow;
	}

	/**
	 * 序列化方法
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(upFlow);
		out.writeLong(downFlow);
		out.writeLong(sumFlow);
	}

	/**
	 * 反序列化方法,必须要求和序列化方法顺序一致
	 */
	@Override
	public void readFields(DataInput in) throws IOException {
		upFlow = in.readLong();
		downFlow = in.readLong();
		sumFlow = in.readLong();
	}

	@Override
	public String toString() {
		return upFlow + "\t" + downFlow + "\t" + sumFlow;
	}

	public void set(long upFlow2, long downFlow2) {
		upFlow = upFlow2;
		downFlow = downFlow2;
		sumFlow = upFlow2 + downFlow2;
	}
}