package com.wy.common;

import lombok.Data;
import okhttp3.Response;

import java.io.InputStream;

/**
 * @apiNote 对外SDK接口返回数据 
 * @author ParadiseWY
 * @date 2020年2月12日 上午12:23:25
 */
@Data
public class HOSObject {

    private ObjectMetaData metaData;
    private InputStream content;
    private Response response;

    public HOSObject() {
    }

    public HOSObject(Response response) {
        this.response = response;
    }

    // 释放资源
    public void close() {
        try {
            if (this.content != null) {
                this.content.close();
            }
            if (this.response != null) {
                this.response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}