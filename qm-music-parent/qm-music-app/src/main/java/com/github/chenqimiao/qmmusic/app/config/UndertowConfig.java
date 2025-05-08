package com.github.chenqimiao.qmmusic.app.config;

import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * @author Qimiao Chen
 * @since 2025/4/1 10:43
 **/
@Configuration
public class UndertowConfig implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {
    @Override
    public void customize(UndertowServletWebServerFactory factory) {
        factory.addDeploymentInfoCustomizers(deploymentInfo -> {
            WebSocketDeploymentInfo wsInfo = new WebSocketDeploymentInfo();
            // 配置缓冲池（参数：direct buffers, buffer 大小）
            wsInfo.setBuffers(new DefaultByteBufferPool(true, 8192));
            deploymentInfo.addServletContextAttribute(
                    WebSocketDeploymentInfo.ATTRIBUTE_NAME, wsInfo
            );
        });
    }

}