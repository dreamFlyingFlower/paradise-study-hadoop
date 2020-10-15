package com.wy.recommand;

import java.util.List;

/**
 * 电商商品推荐系统相关
 * 
 * @author ParadiseWY
 * @date 2020-10-15 13:58:27
 * @git {@link https://github.com/mygodness100}
 */
public class Recommend {

	public static void main(String[] args) {

	}

	/**
	 * 猜你喜欢推荐接口,输入广告位编号,用户编号,用户当前会话浏览的商品
	 * 
	 * @param adId 广告位的编号
	 * @param userId 用户编号
	 * @param views 用户当前会话浏览的商品
	 * @return 返回混合推荐的商品
	 */
	public static List<Product> recommend(Integer adId, Integer userId, String views) {
		// 判断当前广告位是否有对应的推荐模型,如果没有推荐模型就返回null
		// 获取推荐对应的规则,计算需要推荐的商品数量
		// 根据广告位使用推荐模型计算的结果,每个广告位都有独立的一个或多个模型计算支撑
		// 对硬推广告进行设置
		return null;
	}

	/**
	 * 为猜你喜欢广告位进行推荐,分析猜你喜欢的推荐思路
	 * 
	 * @param adId 广告位编号
	 * @param needNum 广告所需数量
	 * @param userId 用户编号
	 * @param views 用户当前会话浏览页面
	 * @return 广告位
	 */
	public static List<Product> recommend(Integer adId, int needNum, Integer userId, String views) {
		// 猜你喜欢有两个维度的分析:猜与你相似的用户喜欢什么,猜你当前感兴趣的物品相似的物品
		// 猜与你相似的用户是离线计算,通过批处理计算基于用户的协同过滤推荐
		// 猜物品的相似度:
		// 1根据上一次浏览或下单的基于物品进行推荐->浏览次数越多的商品,推荐的权重越高
		// 2根据你本次浏览的商品,实时的获取基于物品推荐的结果->浏览次数越多的商品,推荐的权重越高
		// 3根据你本次浏览的商品,实时基于物品内容的推荐结果进行推荐,浏览次数越多的商品,根据共同属性推荐的商品比重越高
		// 以上推荐结果进行混合推荐,去重,排序,截取多余的商品,产生推荐效果
		// 补全或添加强推的广告位

		// 基于业务需求的推荐规则的算法
		// 对推荐结果进行排序,排序算法根据需求来
		// 1:基于物品的实时推荐结果
		// 2:基于用户的离线推荐结果
		// 3:基于物品的离线推荐结果
		// 4:基于内容的实时推荐结果
		// 5:基于物品的实时推荐结果
		// 6:基于用户的离线推荐结果
		return null;
	}
}