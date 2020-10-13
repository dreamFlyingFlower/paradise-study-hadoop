package com.wy.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.wy.common.HOSObject;
import com.wy.common.HOSObjectSummary;
import com.wy.common.ListObjectRequest;
import com.wy.common.ObjectListResult;
import com.wy.common.PutRequest;
import com.wy.model.Bucket;

/**
 * @apiNote 客户端接口
 * @author ParadiseWY
 * @date 2020年2月12日 下午2:04:01
 */
public interface HosClientService {

    public void createBucket(String bucketName) throws Exception;
    public void createBucket(String bucketName, String detail) throws Exception;
    public void deleteBucket(String bucketName) throws Exception;
    public List<Bucket> listBucket() throws Exception;
    public void putObject(PutRequest putRequest) throws Exception;
    public void putObject(String bucket, String key) throws Exception;
    public void putObject(String bucket, String key, byte[] content, String mediaType) throws Exception;
    public void putObject(String bucket, String key, File content) throws Exception;
    public void putObject(String bucket, String key, byte[] content, String mediaType,
                          String contentEncoding) throws Exception;
    public void putObject(String bucket, String key, File content, String mediaType)
            throws Exception;
    public void putObject(String bucket, String key, File content, String mediaType,
                          String contentEncoding) throws Exception;
    public void deleteObject(String bucket, String key) throws Exception;
    public HOSObjectSummary getObjectSummary(String bucket, String key) throws Exception;
    public ObjectListResult listObject(String bucket, String startKey, String endKey)
            throws IOException;
    public ObjectListResult listObject(ListObjectRequest request) throws IOException;
    public ObjectListResult listObjectByPrefix(String bucket, String dir, String prefix,
                                               String startKey)
            throws IOException;
    public ObjectListResult listObjectByDir(String bucket, String dir, String startKey)
            throws IOException;
    public HOSObject getObject(String bucket, String key) throws IOException;
    public Bucket getBucketInfo(String bucketName) throws IOException;
}