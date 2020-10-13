package com.wy.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wy.service.HosClientService;
import com.wy.service.impl.HosClientServiceImpl;

/**
 * @apiNote 创建HOSClient的工厂类
 * @author ParadiseWY
 * @date 2020年2月12日 下午2:03:13
 */
public class HosClientFactory {
	private static Map<String, HosClientService> clientCache = new ConcurrentHashMap<>();

    public static HosClientService getOrCreateHOSClient(String endPoint, String token) {
        String key = endPoint + "_" + token;
        // 判断clientCache是否含有
        if (clientCache.containsKey(key)) {
            // 包含
            return clientCache.get(key);
        }
        HosClientService client = new HosClientServiceImpl(endPoint, token);
        clientCache.put(key, client);
        return client;
    }
}
