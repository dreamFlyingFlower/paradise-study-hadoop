package com.wy.common;

/**
 * @apiNote 错误码
 * @author ParadiseWY
 * @date 2020年2月12日 下午1:41:07
 */
public interface ErrorCode {

    public static final int ERROR_PERMISSION_DENIED = 403;
    public static final int ERROR_FILE_NOT_FOUND = 404;
    public static final int ERROR_HBASE = 500;
    public static final int ERROR_HDFS = 501;
}