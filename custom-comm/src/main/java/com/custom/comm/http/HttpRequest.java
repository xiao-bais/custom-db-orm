package com.custom.comm.http;

import com.alibaba.fastjson.JSON;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/27 10:57
 * @Desc： 发送请求工具类
 **/
public class HttpRequest {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);



    public static String get(String requestUrl, Map<String, Object> requestMap) {
        StringJoiner paramStr = new StringJoiner("&");
        if (requestMap.size() > 0) {
            requestMap.forEach((key, value) -> paramStr.add(String.format("%s=%s", key, value)));
        }
        return get(requestUrl + "?" + paramStr);
    }


    public static String get(String requestUrl)  {

        InputStream is = null;
        String result = SymbolConstant.EMPTY;
        try {
            URL url = new URL(requestUrl);
            //获取连接对象
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("GET");

            // 建立连接
            connection.connect();

            is = connection.getInputStream();
            if (is == null) return SymbolConstant.EMPTY;
            result = copyToString(is);
        }catch (IOException e) {
            logger.error(e.toString(), e);
        }finally {
            if(is != null) {
                try {
                    is.close();
                }catch (IOException e){
                    logger.error(e.toString(), e);
                }
            }
        }
        return result;
    }


    private static String copyToString (InputStream is) throws IOException {
        StringBuilder out = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
        char[] buffer = new char[4096];

        int bytesRead;
        while ((bytesRead = reader.read(buffer)) != -1) {
            out.append(buffer, 0, bytesRead);
        }
        return out.toString();
    }

    /**
     * post请求
     * @param requestUrl
     * @param paramStr
     * @return
     */
    public static String post(String requestUrl, String paramStr) {
        InputStream is = null;
        OutputStream os = null;

        String result = SymbolConstant.EMPTY;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(requestUrl).openConnection();

            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            os = connection.getOutputStream();

            //设置请求参数
            if(JudgeUtil.isNotEmpty(paramStr)) {
                os.write(paramStr.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
            is = connection.getInputStream();
            result = copyToString(is);
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }finally {
            if(is != null) {
                try {
                    is.close();
                }catch (IOException e){
                    logger.error(e.toString(), e);
                }
            }
        }
        return result;
    }

    public static String post(String requestUrl, Map<String, Object> paramMap) {
        return post(requestUrl, JSON.toJSONString(paramMap));
    }


}
