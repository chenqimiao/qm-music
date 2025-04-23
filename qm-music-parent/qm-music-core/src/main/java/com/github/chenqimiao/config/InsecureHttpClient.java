package com.github.chenqimiao.config;

import lombok.SneakyThrows;

import javax.net.ssl.*;
import java.net.http.HttpClient;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 17:55
 **/
public class InsecureHttpClient {
    private static final HttpClient INSTANCE = createInsecureClient();

    public static HttpClient getInstance() {
        return INSTANCE;
    }
    /**
     * 创建完全绕过 SSL 验证的 HttpClient
     */
    @SneakyThrows
    private static HttpClient createInsecureClient() {
        // 1. 创建 TrustManager 信任所有证书
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };

        // 2. 创建 HostnameVerifier 信任所有主机名
        HostnameVerifier allHostsValid = (hostname, session) -> true;

        // 3. 配置 SSLContext 完全开放
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        // 关闭主机校验
        SSLParameters sslParams = new SSLParameters();
        sslParams.setEndpointIdentificationAlgorithm(""); // 关闭主机名校验
        sslParams.setProtocols(new String[]{"SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"}); // 设置支持的协议

        // 5. 构建 HttpClient
        return HttpClient.newBuilder()
                .sslContext(sslContext)
                .sslParameters(getInsecureSSLParameters())  // 禁用协议检查
                .connectTimeout(Duration.ofSeconds(3))
                .version(HttpClient.Version.HTTP_2)
                .sslParameters(sslParams)
                .followRedirects(HttpClient.Redirect.NORMAL)  // 启用自动跟随重定向
                .build();
    }

    /**
     * 获取彻底开放的 SSLParameters
     */
    private static SSLParameters getInsecureSSLParameters() {
        SSLParameters params = new SSLParameters();
        params.setEndpointIdentificationAlgorithm(""); // 关键！禁用域名验证
        params.setProtocols(new String[]{"SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"}); // 允许所有协议
        params.setCipherSuites(new String[]{"TLS_RSA_WITH_AES_256_CBC_SHA256"}); // 允许所有密码套件（可选）
        return params;
    }

}
