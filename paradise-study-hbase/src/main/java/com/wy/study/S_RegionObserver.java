package com.wy.study;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WALEdit;

import com.wy.utils.ListUtils;

/**
 * @apiNote HBase的RegionObserver协处理器,另外的协处理器百度,同时协处理器写完后需要加载,百度
 * @author ParadiseWY
 * @date 2020年2月10日 下午11:34:53
 */
public class S_RegionObserver implements RegionObserver {

	private byte[] cf = Bytes.toBytes("cf");

	private byte[] count = Bytes.toBytes("count");

	private byte[] unDeleteCol = Bytes.toBytes("unDeleteCol");

	private RegionCoprocessorEnvironment environment;

	/**
	 * 打开region前执行
	 */
	@Override
	public void preOpen(ObserverContext<RegionCoprocessorEnvironment> c) throws IOException {
		environment = c.getEnvironment();
	}

	/**
	 * 关闭region前执行
	 */
	@Override
	public void preClose(ObserverContext<RegionCoprocessorEnvironment> c, boolean abortRequested) throws IOException {
		environment = c.getEnvironment();
	}

	/**
	 * 1.cf:count,进行累加操作,每次插入的时候都要与之前的值相加
	 */
	@Override
	public void prePut(ObserverContext<RegionCoprocessorEnvironment> c, Put put, WALEdit edit, Durability durability)
			throws IOException {
		if (put.has(cf, count)) {
			// 获得旧的count的值
			Result result = environment.getRegion().get(new Get(put.getRow()));
			int preNum = 0;
			for (Cell cell : result.rawCells()) {
				if (CellUtil.matchingColumn(cell, cf, count)) {
					preNum = Integer.parseInt(Bytes.toString(CellUtil.cloneValue(cell)));
				}
			}
			// 获得新的count的值
			List<Cell> cells = put.get(cf, count);
			int nextNum = 0;
			for (Cell cell : cells) {
				if (CellUtil.matchingColumn(cell, cf, count)) {
					nextNum = Integer.parseInt(Bytes.toString(CellUtil.cloneValue(cell)));
				}
			}
			put.addColumn(cf, count, Bytes.toBytes(String.valueOf(preNum + nextNum)));
		}
	}

	/**
	 * 2.不能直接删除undeletecol,删除count的时候将undeletecol一同删除
	 */
	@Override
	public void preDelete(ObserverContext<RegionCoprocessorEnvironment> c, Delete delete, WALEdit edit,
			Durability durability) throws IOException {
		// 判断是否操作cf列簇
		List<Cell> cells = delete.getFamilyCellMap().get(cf);
		if (ListUtils.isBlank(cells)) {
			return;
		}

		boolean flag = false;
		for (Cell cell : cells) {
			byte[] cq = CellUtil.cloneQualifier(cell);
			if (Arrays.equals(cq, unDeleteCol)) {
				throw new IOException("can not delete undelcol");
			}
			if (Arrays.equals(cq, count)) {
				flag = true;
			}
			if (flag) {
				delete.addColumn(cf, unDeleteCol);
			}
		}
	}
}