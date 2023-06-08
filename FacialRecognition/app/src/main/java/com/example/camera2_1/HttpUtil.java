package com.example.camera2_1;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * http 工具类
 */
public class HttpUtil {

    private static Gson gson = new GsonBuilder().create();

    public static String post(String requestUrl, String loginToken, Object params)
            throws Exception {
        return HttpUtil.post(requestUrl, loginToken, "application/json", params);
    }

    public static String post(String requestUrl, String loginToken, String contentType, Object params)
            throws Exception {
        return HttpUtil.request(requestUrl, loginToken, contentType, params, "UTF-8","POST", null);
    }

    public static String post4Header(String requestUrl, Map<String,String> headers, Object params)
            throws Exception {
        return HttpUtil.request(requestUrl, null, "application/json", params, "UTF-8","POST",headers);
    }

    public static String get(String requestUrl, String loginToken)
            throws Exception {
        return HttpUtil.request(requestUrl,loginToken, "application/json", null, "UTF-8","GET", null);
    }

    public static byte[] getByte(String requestUrl)
            throws Exception {
        return HttpUtil.request4Byte(requestUrl,null, null, null, null,"GET");
    }

    private static byte[] request4Byte(String generalUrl, String loginToken, String contentType, Object params, String encoding, String method)
            throws Exception {
        URL url = new URL(generalUrl);
        URLConnection c = url.openConnection();
        if("http".equals(url.getProtocol())){
            return getResponseByte(loginToken, contentType, params, encoding, method, (HttpURLConnection)c);
        }else {
            // use ignore host name verifier
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            // 打开和URL之间的连接
            HttpsURLConnection connection = (HttpsURLConnection)c;
            // Prepare SSL Context
            TrustManager[] tm = {manager};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            connection.setSSLSocketFactory(ssf);

            return getResponseByte(loginToken, contentType, params, encoding, method, connection);
        }
    }

    private static byte[] getResponseByte(String loginToken, String contentType, Object params, String encoding, String method, HttpURLConnection connection) throws Exception {
        // 设置通用的请求属性
        byte[] data = null;
        connection.setRequestMethod(method);
        if(contentType!=null && !"".equals(contentType.trim()))
            connection.setRequestProperty("Content-ObjectType", contentType);
        connection.setRequestProperty("Connection", "Keep-Alive");
        if (loginToken!=null && !"".equals(loginToken.trim()))
            connection.setRequestProperty("token", loginToken);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        // 建立实际的连接
        connection.connect();
        // 得到请求的输出流对象
        if("POST".equals(method) && params != null) {
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(gson.toJson(params).getBytes(encoding));
            out.flush();
            out.close();
        }

        InputStream inStream = connection.getInputStream();
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, buff.length)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        data = swapStream.toByteArray();

        swapStream.flush();
        swapStream.close();
        inStream.close();
        return data;
    }

    private static String request(String generalUrl, String loginToken, String contentType, Object params, String encoding, String method,
                                  Map<String, String> headers)
            throws Exception {
        URL url = new URL(generalUrl);
        URLConnection c = url.openConnection();
        if("http".equals(url.getProtocol())){
           return getResponse(loginToken, contentType, params, encoding, method, (HttpURLConnection)c,headers);
        }else {
            // use ignore host name verifier
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            // 打开和URL之间的连接
            HttpsURLConnection connection = (HttpsURLConnection)c;
            // Prepare SSL Context
            TrustManager[] tm = {manager};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            connection.setSSLSocketFactory(ssf);

            return getResponse(loginToken, contentType, params, encoding, method, connection,headers);
        }
    }

    private static String getResponse(String loginToken, String contentType, Object params, String encoding, String method,
                                      HttpURLConnection connection, Map<String, String> headers) throws IOException {
        // 设置通用的请求属性
        connection.setRequestMethod(method);
        if(contentType!=null && !"".equals(contentType.trim()))
            connection.setRequestProperty("Content-Type", contentType);
        if (loginToken!=null && !"".equals(loginToken.trim()))
            connection.setRequestProperty("token", loginToken);
        if (headers!=null && !headers.isEmpty()) {
            headers.forEach(connection::setRequestProperty);
        }
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // 得到请求的输出流对象
        if("POST".equals(method) && params != null) {
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(gson.toJson(params).getBytes(encoding));
            out.flush();
            out.close();
        }

        // 建立实际的连接
        connection.connect();

        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in = null;
        in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), encoding));
        String result = "";
        String getLine;
        while ((getLine = in.readLine()) != null) {
            result += getLine;
        }
        in.close();
        return result;
    }

    // 服务器名校验
    private static HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String arg0, SSLSession arg1) {
            return true;
        }
    };
    // 证书/信任库 管理器
    private static TrustManager manager = new X509TrustManager() {

        private X509Certificate[] certificates;

        @Override
        public void checkClientTrusted(X509Certificate certificates[], String authType) throws CertificateException {
            if (this.certificates == null) {
                this.certificates = certificates;
            }
        }

        @Override
        public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
            if (this.certificates == null) {
                this.certificates = ax509certificate;
            }

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    public static String byteToHexString(byte[] bytes) throws Exception {
        MessageDigest complete = MessageDigest.getInstance("MD5");
        complete.update(bytes, 0, bytes.length);

        byte[] di = complete.digest();
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] ob = new char[2];

        StringBuffer sb = new StringBuffer();
        for (int i=0; i < di.length; i++) {
            ob[0] = Digit[( di[i] >>> 4) & 0X0f];
            ob[1] = Digit[ di[i] & 0X0F];
            sb.append(ob);
        }
        return sb.toString();
    }
}
