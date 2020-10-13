package com.wy.common;

import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * @apiNote 固定值
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:02:51
 */
public class HOSConst {

	// 超级管理员
	public final static String SYSTEM_USER = "SuperAdmin";

	// 目录表前缀
	public final static String DIR_TABLE_PREFIX = "hos_dir_";

	// 对象表前缀
	public final static String OBJ_TABLE_PREFIX = "hos_obj_";

	// 目录表meta信息列族名
	public final static String DIR_META_CF = "cf";

	public final static byte[] DIR_META_CF_BYTES = DIR_META_CF.getBytes();

	// 目录表sub信息列族名
	public final static String DIR_SUB_CF = "sub";

	public final static byte[] DIR_SUB_CF_BYTES = DIR_SUB_CF.getBytes();

	// 文件表meta信息列族名
	public final static String OBJ_META_CF = "cf";

	public final static byte[] OBJ_META_CF_BYTES = OBJ_META_CF.getBytes();

	// 文件表content信息列族名
	public final static String OBJ_CONTENT_CF = "c";

	public final static byte[] OBJ_CONTENT_CF_BYTES = OBJ_CONTENT_CF.getBytes();

	// 目录表seqId列名
	public final static byte[] DIR_SEQID_QUALIFIER = "u".getBytes();

	// 文件表content列名
	public final static byte[] OBJ_CONTENT_QUALIFIER = "c".getBytes();

	// 文件表length列名
	public final static byte[] OBJ_LENGTH_QUALIFIER = "l".getBytes();

	// 文件表property列名
	public final static byte[] OBJ_PROPERTY_QUALIFIER = "p".getBytes();

	// 文件表mediatype列名
	public final static byte[] OBJ_MEDIATYPE_QUALIFIER = "m".getBytes();

	public static final FilterList OBJ_META_SCAN_FILTER = new FilterList(FilterList.Operator.MUST_PASS_ONE);

	// 文件根目录
	public final static String FILE_STORE_ROOT = "/hos";

	// 文件大小阈值 20M,小于20M存储在hbase,大于20存储与hdfs
	public final static int FILE_STORE_THRESHOLD = 20 * 1024 * 1024;

	// 存储hbase目录表的seqId的表,协助生成目录的SequenceID的表
	public final static String BUCKET_DIR_SEQ_TABLE = "hos_dir_seq";

	public final static String BUCKET_DIR_SEQ_CF = "s";

	public final static byte[] BUCKET_DIR_SEQ_CF_BYTES = BUCKET_DIR_SEQ_CF.getBytes();

	public final static String BUCKET_DIR_SEQ_COLUMN_QUALIFIER = "s";

	public final static byte[] BUCKET_DIR_SEQ_COLUMN_QUALIFIER_BYTES = BUCKET_DIR_SEQ_COLUMN_QUALIFIER.getBytes();

	public static final String REQUEST_RANGE = "x-hos-range";

	public static final String COMMON_OBJ_ID = "x-hos-id";

	public static final String COMMON_BUCKET_NAME = "x-hos-bucket";

	public static final String COMMON_ATTR_PREFIX = "x-hos-attr_";

	public static final String RESPONSE_OBJ_LENGTH = "x-hos-length";

	public static final String COMMON_OBJ_KEY = "x-hos-key";

	public static final String COMMON_OBJ_BUCKET = "x-hos-bucket";

	static {
		try {
			byte[][] qualifiers = new byte[][] { HOSConst.DIR_SEQID_QUALIFIER, HOSConst.OBJ_LENGTH_QUALIFIER,
					HOSConst.OBJ_MEDIATYPE_QUALIFIER };
			for (byte[] b : qualifiers) {
				Filter filter = new QualifierFilter(CompareOperator.EQUAL, new BinaryComparator(b));
				OBJ_META_SCAN_FILTER.addFilter(filter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final static byte[][] OBJ_REGIONS = new byte[][] { Bytes.toBytes("1"), Bytes.toBytes("4"),
			Bytes.toBytes("7"), };

	public static String getDirTableName(String bucketName) {
		return DIR_TABLE_PREFIX + bucketName;
	}

	public static String getObjTableName(String bucketName) {
		return OBJ_TABLE_PREFIX + bucketName;
	}

	public static String[] getDirColumnFamilies() {
		return new String[] { DIR_META_CF, DIR_SUB_CF };
	}

	public static String[] getObjColumnFamilies() {
		return new String[] { OBJ_META_CF, OBJ_CONTENT_CF };
	}
}