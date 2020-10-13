package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * HBase学习
 * @apiNote 配置类->{@link HBaseConfiguraion},管理Admin类:{@link HBaseAdmin},
 *          操作类:{@link Table},添加:{@link Put}, 查询:{@link Get},查询结果:{@link Scan},
 *          检索:{@link Result},检索结果:{@link ResultScan}
 * @apiNote 过滤器->基于行的过滤器:PrefixFilter:行的前缀过滤器;PageFilter:基于行的分页
 *          基于列的过滤器:ColumnPrefixFilter:列前缀过滤 FirstKeyOnlyFilter:只返回每一行的第一列
 *          基于单元值的过滤:KeyOnlyFilter:返回数据不包含单元值,只包含行键和列;TimestampsFilter:根据数据时间戳版本进行过滤
 *          基于列和单元值的过滤:SingleColumnValueFilter,SingleColumnValueExcludeFilter:对该列的单元值进行过滤
 *          比较过滤器:通常需要一个比较运算符以及一个比较容器来实现,如RowFilter,FamilyFilter,QualifierFilter,ValueFilter等
 *          综合过滤器:FilterList
 * @author ParadiseWY
 * @date 2020年2月9日 下午1:04:40
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}