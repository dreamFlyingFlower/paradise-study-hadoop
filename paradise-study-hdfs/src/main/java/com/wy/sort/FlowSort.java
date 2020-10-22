package com.wy.sort;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowSort implements WritableComparable<FlowSort> {

	private long upFlow;

	private long downFlow;

	private long sumFlow;

	public FlowSort(long upFlow, long downFlow) {
		super();
		this.upFlow = upFlow;
		this.downFlow = downFlow;
		sumFlow = upFlow + downFlow;
	}

	/**
	 * 序列化
	 * 
	 * @param out 数输出
	 * @throws IOException
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(upFlow);
		out.writeLong(downFlow);
		out.writeLong(sumFlow);
	}

	/**
	 * 反序列化
	 * 
	 * @param in 数据输入
	 * @throws IOException
	 */
	@Override
	public void readFields(DataInput in) throws IOException {
		upFlow = in.readLong();
		downFlow = in.readLong();
		sumFlow = in.readLong();
	}

	/**
	 * 重写排序的比较方法
	 * 
	 * @param bean 排序实例
	 * @return 大小0
	 */
	@Override
	public int compareTo(FlowSort bean) {
		int result;
		// 核心比较条件判断
		if (sumFlow > bean.getSumFlow()) {
			result = -1;
		} else if (sumFlow < bean.getSumFlow()) {
			result = 1;
		} else {
			result = 0;
		}
		return result;
	}

	@Override
	public String toString() {
		return upFlow + "\t" + downFlow + "\t" + sumFlow;
	}
}