package com.wy.Table;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import com.wy.model.Table;

public class TableReduce extends Reducer<Text, Table, Table, NullWritable> {

	@Override
	protected void reduce(Text key, Iterable<Table> values, Context context) throws IOException, InterruptedException {
		// 0 准备存储数据的缓存
		Table pdbean = new Table();
		ArrayList<Table> orderBeans = new ArrayList<>();
		// 根据文件的不同分别处理数据
		for (Table bean : values) {
			if ("0".equals(bean.getFlag())) {
				// 订单表数据处理
				// 1001 1
				// 1001 1
				Table orBean = new Table();
				try {
					BeanUtils.copyProperties(orBean, bean);
				} catch (Exception e) {
					e.printStackTrace();
				}
				orderBeans.add(orBean);
			} else {
				// 产品表处理 01 小米
				try {
					BeanUtils.copyProperties(pdbean, bean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// 数据拼接
		for (Table bean : orderBeans) {
			// 更新产品名称字段
			bean.setPname(pdbean.getPname());

			context.write(bean, NullWritable.get());
		}
	}
}