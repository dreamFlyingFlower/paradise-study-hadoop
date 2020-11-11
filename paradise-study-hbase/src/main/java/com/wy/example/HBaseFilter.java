package com.wy.example;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.ColumnPaginationFilter;
import org.apache.hadoop.hbase.filter.DependentColumnFilter;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueExcludeFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.filter.ValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

/**
 * 
 * 
 * @author ParadiseWY
 * @date 2020-11-11 11:10:41
 * @git {@link https://github.com/mygodness100}
 */
public class HBaseFilter {

	Configuration conf = HBaseConfiguration.create();

	/**
	 * RowFilter过滤器
	 */
	public void testRowFilter() throws IOException {
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t1");
		Scan scan = new Scan();
		RowFilter rowFilter = new RowFilter(CompareOperator.LESS_OR_EQUAL,
				new BinaryComparator(Bytes.toBytes("row0100")));
		scan.setFilter(rowFilter);
		Table t = conn.getTable(tname);
		ResultScanner rs = t.getScanner(scan);
		Iterator<Result> it = rs.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			System.out.println(Bytes.toString(r.getRow()));
		}
	}

	/**
	 * FamilyFilter过滤器
	 */
	public void testFamilyFilter() throws IOException {

		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t7");
		Scan scan = new Scan();
		FamilyFilter filter = new FamilyFilter(CompareOperator.LESS, new BinaryComparator(Bytes.toBytes("f2")));
		scan.setFilter(filter);
		Table t = conn.getTable(tname);
		ResultScanner rs = t.getScanner(scan);
		Iterator<Result> it = rs.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
			byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
			System.out.println(f1id + " : " + f2id);
		}
	}

	/**
	 * QualifierFilter(列过滤器)
	 */
	public void testColFilter() throws IOException {
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t7");
		Scan scan = new Scan();
		QualifierFilter colfilter = new QualifierFilter(CompareOperator.EQUAL,
				new BinaryComparator(Bytes.toBytes("id")));
		scan.setFilter(colfilter);
		Table t = conn.getTable(tname);
		ResultScanner rs = t.getScanner(scan);
		Iterator<Result> it = rs.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
			byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
			byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
			System.out.println(f1id + " : " + f2id + " : " + f2name);
		}
	}

	/**
	 * ValueFilter(值过滤器),过滤value的值,含有指定的字符子串
	 */
	public void testValueFilter() throws IOException {
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t7");
		Scan scan = new Scan();
		ValueFilter filter = new ValueFilter(CompareOperator.EQUAL, new SubstringComparator("to"));
		scan.setFilter(filter);
		Table t = conn.getTable(tname);
		ResultScanner rs = t.getScanner(scan);
		Iterator<Result> it = rs.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
			byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
			byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
			byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
			System.out.println(f1id + " : " + f2id + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
		}
	}

	/**
	 * 依赖列过滤器
	 */
	public void testDepFilter() throws IOException {
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t7");
		Scan scan = new Scan();
		DependentColumnFilter filter = new DependentColumnFilter(Bytes.toBytes("f2"), Bytes.toBytes("name"), false,
				CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("tom2.2")));
		// ValueFilter filter = new ValueFilter(CompareFilter.CompareOp.EQUAL, new
		// SubstringComparator("to"));
		scan.setFilter(filter);
		Table t = conn.getTable(tname);
		ResultScanner rs = t.getScanner(scan);
		Iterator<Result> it = rs.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
			byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
			byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
			byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
			System.out.println(f1id + " : " + f2id + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
		}
	}

	/**
	 * 单列值value过滤,对列上的value进行过滤,不符合整行删除
	 */
	@Test
	public void testSingleColumValueFilter() throws IOException {

		Configuration conf = HBaseConfiguration.create();
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t7");
		Scan scan = new Scan();
		SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes("f2"), Bytes.toBytes("name"),
				CompareOperator.NOT_EQUAL, new BinaryComparator(Bytes.toBytes("tom2.2")));

		scan.setFilter(filter);
		Table t = conn.getTable(tname);
		ResultScanner rs = t.getScanner(scan);
		Iterator<Result> it = rs.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
			byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
			byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
			byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
			System.out.println(f1id + " : " + f2id + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
		}
	}

	/**
	 * 单列值排除过滤器,去掉过滤使用的列,对列的值进行过滤
	 */
	public void testSingleColumValueExcludeFilter() throws IOException {
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t7");
		Scan scan = new Scan();
		SingleColumnValueExcludeFilter filter = new SingleColumnValueExcludeFilter(Bytes.toBytes("f2"),
				Bytes.toBytes("name"), CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("tom2.2")));
		scan.setFilter(filter);
		Table t = conn.getTable(tname);
		ResultScanner rs = t.getScanner(scan);
		Iterator<Result> it = rs.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
			byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
			byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
			byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
			System.out.println(f1id + " : " + f2id + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
		}
	}

	/**
	 * 前缀过滤,是rowkey过滤.where rowkey like 'row22%'
	 */
	public void testPrefixFilter() throws IOException {
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t1");
		Scan scan = new Scan();
		PrefixFilter filter = new PrefixFilter(Bytes.toBytes("row222"));
		scan.setFilter(filter);
		Table t = conn.getTable(tname);
		ResultScanner rs = t.getScanner(scan);
		Iterator<Result> it = rs.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
			byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
			byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
			byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
			System.out.println(f1id + " : " + f2id + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
		}
	}

	/**
	 * 分页过滤,是rowkey过滤,在region上扫描时,对每次page设置的大小.返回到到client,设计到每个Region结果的合并
	 */
	@Test
	public void testPageFilter() throws IOException {
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t1");
		Scan scan = new Scan();
		PageFilter filter = new PageFilter(10);
		scan.setFilter(filter);
		Table t = conn.getTable(tname);
		ResultScanner rs = t.getScanner(scan);
		Iterator<Result> it = rs.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
			byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
			byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
			byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
			System.out.println(f1id + " : " + f2id + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
		}
	}

	/**
	 * keyOnly过滤器,只提取key,丢弃value
	 * 
	 * @throws IOException
	 */
	@Test
	public void testKeyOnlyFilter() throws IOException {
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t1");
		Scan scan = new Scan();
		KeyOnlyFilter filter = new KeyOnlyFilter();
		scan.setFilter(filter);
		Table t = conn.getTable(tname);
		ResultScanner rs = t.getScanner(scan);
		Iterator<Result> it = rs.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
			byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
			byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
			byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
			System.out.println(f1id + " : " + f2id + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
		}
	}

	/**
	 * ColumnPageFilter,列分页过滤器,过滤指定范围列 select a,b from ns1:t7
	 */
	@Test
	public void testColumnPageFilter() throws IOException {
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t7");
		Scan scan = new Scan();
		ColumnPaginationFilter filter = new ColumnPaginationFilter(2, 2);
		scan.setFilter(filter);
		Table t = conn.getTable(tname);
		ResultScanner rs = t.getScanner(scan);
		Iterator<Result> it = rs.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
			byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
			byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
			byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
			System.out.println(f1id + " : " + f2id + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
		}
	}

	public void testLike() throws IOException {
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t7");
		Scan scan = new Scan();
		ValueFilter filter = new ValueFilter(CompareOperator.EQUAL, new RegexStringComparator("^tom2"));
		scan.setFilter(filter);
		Table t = conn.getTable(tname);
		ResultScanner rs = t.getScanner(scan);
		Iterator<Result> it = rs.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
			byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
			byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
			byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
			System.out.println(f1id + " : " + f2id + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
		}
	}

	public void testComboFilter() throws IOException {
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t7");
		Scan scan = new Scan();
		// where ... f2:age <= 13
		SingleColumnValueFilter ftl = new SingleColumnValueFilter(Bytes.toBytes("f2"), Bytes.toBytes("age"),
				CompareOperator.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes("13")));

		// where ... f2:name like %t
		SingleColumnValueFilter ftr = new SingleColumnValueFilter(Bytes.toBytes("f2"), Bytes.toBytes("name"),
				CompareOperator.EQUAL, new RegexStringComparator("^t"));
		// ft
		FilterList ft = new FilterList(FilterList.Operator.MUST_PASS_ALL);
		ft.addFilter(ftl);
		ft.addFilter(ftr);

		// where ... f2:age > 13
		SingleColumnValueFilter fbl = new SingleColumnValueFilter(Bytes.toBytes("f2"), Bytes.toBytes("age"),
				CompareOperator.GREATER, new BinaryComparator(Bytes.toBytes("13")));

		// where ... f2:name like %t
		SingleColumnValueFilter fbr = new SingleColumnValueFilter(Bytes.toBytes("f2"), Bytes.toBytes("name"),
				CompareOperator.EQUAL, new RegexStringComparator("t$"));
		// ft
		FilterList fb = new FilterList(FilterList.Operator.MUST_PASS_ALL);
		fb.addFilter(fbl);
		fb.addFilter(fbr);
		FilterList fall = new FilterList(FilterList.Operator.MUST_PASS_ONE);
		fall.addFilter(ft);
		fall.addFilter(fb);
		scan.setFilter(fall);
		Table t = conn.getTable(tname);
		ResultScanner rs = t.getScanner(scan);
		Iterator<Result> it = rs.iterator();
		while (it.hasNext()) {
			Result r = it.next();
			byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
			byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
			byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
			byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
			System.out.println(f1id + " : " + f2id + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
		}
	}
}