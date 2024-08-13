package com.spcrey.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;

public class AliyunOssUtil {

    private static final String ENDPOINT = "https://oss-cn-beijing.aliyuncs.com";

    private static final String BUCKET_NAME = "firefly-whisper";

    public static String uploadFile(String objectName, InputStream in) throws Exception {
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, credentialsProvider);
        String url = "";
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, objectName, in);
            ossClient.putObject(putObjectRequest);  
            url = "https://" + BUCKET_NAME + "." + ENDPOINT.substring(ENDPOINT.lastIndexOf("/") + 1) + "/" + objectName;
        } 
        catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } 
        catch (ClientException ce) {
            System.out.println("""
                    Caught an ClientException, which means the client encountered \
                    a serious internal problem while trying to communicate with OSS, \
                    such as not being able to access the network.\
                    """);
            System.out.println("Error Message:" + ce.getMessage());
        } 
        finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return url;
    }

    public static void main(String[] args) throws Exception {
        String filepath = "/home/crey/firefly-whisper-server/src/main/resources/image/orange.jpg";
        String filename = UUID.randomUUID().toString() + filepath.substring(filepath.lastIndexOf("."));
        try (InputStream inputStream = new FileInputStream(filepath)) {   
            String url = uploadFile(filename, inputStream);
            System.out.println("url: " + url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}   