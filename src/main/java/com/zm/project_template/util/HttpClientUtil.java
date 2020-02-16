package com.zm.project_template.util;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.*;


@Slf4j
public class HttpClientUtil {

    public static ClassPathResource classPathResource;

    static {
        // 微信支付证书文件
        classPathResource = new ClassPathResource("ssl/wx_apiclient_cert.p12");
    }

    public static String sendPost(String url, Map params, Map headers) throws IOException {

        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        Set<String> set = headers.keySet();
        for (String k : set) {
            postMethod.setRequestHeader(k, headers.get(k).toString());
        }
        List<NameValuePair> data = new ArrayList<NameValuePair>();

        if (Objects.nonNull(params)) {
            for (Iterator iter = params.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String value = String.valueOf(params.get(name));
                data.add(new NameValuePair(name, value));
            }
        }


        NameValuePair[] nvps = new NameValuePair[data.size()];
        for (int i = 0; i < data.size(); i++) {
            nvps[i] = data.get(i);
        }
        // 将表单的值放入postMethod中
        postMethod.setRequestBody(nvps);

        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
        // 链接超时
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(120000);
        // 读取超时
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(120000);
        InputStream soapResponseData = postMethod.getResponseBodyAsStream();
        return new String(getString(soapResponseData).getBytes("UTF-8"), "UTF-8");
    }

    /**
     * @param url
     * @param params
     * @param headers
     * @return 发送json请求
     */
    public static String sendPostJson(String url, JSONObject params, Map headers) {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        if (Objects.nonNull(headers)) {
            Set<String> set = headers.keySet();
            for (String k : set) {
                post.addHeader(k, headers.get(k).toString());
            }
        }
        try {
            StringEntity s = new StringEntity(params.toString());
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");//发送json数据需要设置contentType
            post.setEntity(s);
            HttpResponse res = httpclient.execute(post);
            String result = EntityUtils.toString(res.getEntity());// 返回json格式：
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param url
     * @param jsonString JSONObject.toJOSNString(obj)
     * @param charset
     * @return
     */
    public static String sendPostJson(String url, String jsonString, String charset) {
        try {
            PostMethod postMethod = new PostMethod(url);
            postMethod.setRequestHeader("Content-Type", "application/json; charset=" + charset);
            if (jsonString != null && !jsonString.trim().equals("")) {
                RequestEntity requestEntity = new StringRequestEntity(jsonString, "application/json", charset);
                postMethod.setRequestEntity(requestEntity);
            }
            HttpClient httpClient = new HttpClient();
            httpClient.executeMethod(postMethod);
            // 链接超时
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(120000);
            // 读取超时
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(120000);
            InputStream responseBodyAsStream = postMethod.getResponseBodyAsStream();
            return getString(responseBodyAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param url
     * @param jsonString JSONObject.toJOSNString(obj)
     * @param charset
     * @return
     */
    public static String sendPostJsonSSL(String url, String jsonString, String charset) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream inputStream = classPathResource.getInputStream();
            keyStore.load(inputStream, "1537235131".toCharArray());
            SSLContext sslcontext = SSLContexts.custom()
                    //忽略掉对服务器端证书的校验
                    .loadTrustMaterial((TrustStrategy) (chain, authType) -> true)

                    //加载服务端提供的truststore(如果服务器提供truststore的话就不用忽略对服务器端证书的校验了)
                    .loadKeyMaterial(keyStore, "1537235131".toCharArray())
                    .build();
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1"},
                    null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .build();

            HttpPost postMethod = new HttpPost(url);
            postMethod.setHeader("Content-Type", "application/json; charset=" + charset);
            if (StringUtils.isNotBlank(jsonString)) {
                postMethod.setEntity(new StringEntity(jsonString, charset));
            }
            CloseableHttpResponse response = httpClient.execute(postMethod);
            // 链接超时
//            httpClient.getParams().setParameter("connectionTimeout", 120000);
            // 读取超时
//            httpClient.getParams().setParameter("soTimeout", 120000);
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            return getString(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param url
     * @param jsonString JSONObject.toJOSNString(obj)
     * @param charset
     * @return
     */
    public static byte[] sendPostJsonByte(String url, String jsonString, String charset) {
        try {
            PostMethod postMethod = new PostMethod(url);
            postMethod.setRequestHeader("Content-Type", "application/json; charset=" + charset);
            if (jsonString != null && !jsonString.trim().equals("")) {
                RequestEntity requestEntity = new StringRequestEntity(jsonString, "application/json", charset);
                postMethod.setRequestEntity(requestEntity);
            }
            HttpClient httpClient = new HttpClient();
            httpClient.executeMethod(postMethod);
            // 链接超时
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(120000);
            // 读取超时
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(120000);
            byte[] responseBody = postMethod.getResponseBody();
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sendPost(String url, Map params) throws IOException {

        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        List<NameValuePair> data = new ArrayList<NameValuePair>();


        for (Iterator iter = params.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String value = String.valueOf(params.get(name));
            data.add(new NameValuePair(name, value));
        }

        NameValuePair[] nvps = new NameValuePair[data.size()];
        for (int i = 0; i < data.size(); i++) {
            nvps[i] = data.get(i);
        }
        // 将表单的值放入postMethod中
        postMethod.setRequestBody(nvps);

        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
        // 链接超时
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(120000);
        // 读取超时
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(120000);
        InputStream soapResponseData = postMethod.getResponseBodyAsStream();
        return getString(soapResponseData);
    }

    public static String sendGet(String url) {
        try {
            GetMethod getMethod = new GetMethod(url);
            getMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

            HttpClient httpClient = new HttpClient();
            httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
            httpClient.getParams().setParameter("Host", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
            httpClient.executeMethod(getMethod);
            // 链接超时
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(120000);
            // 读取超时
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(120000);
            InputStream responseBodyAsStream = getMethod.getResponseBodyAsStream();
            return getString(responseBodyAsStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String sendGet(String url, Map<String, Object> headers) {
        try {

            GetMethod getMethod = new GetMethod(url);
            if (headers != null) {
                Set<Map.Entry<String, Object>> entrySet = headers.entrySet();
                entrySet.stream().forEach(o -> getMethod.setRequestHeader(new Header(o.getKey(), o.getValue().toString())));
            } else {
                getMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            }

            HttpClient httpClient = new HttpClient();
            httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, "Apache-HttpClient/4.1.1 (java 1.5)");
            httpClient.executeMethod(getMethod);
            // 链接超时
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(120000);
            // 读取超时
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(120000);
            InputStream responseBodyAsStream = getMethod.getResponseBodyAsStream();
            return getString(responseBodyAsStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getString(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String str = "";
            while ((str = reader.readLine()) != null) {
                stringBuffer.append(str);
            }
            return new String(stringBuffer.toString().trim().getBytes("utf-8"), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}