package com.github.chenqimiao.io.net.config;

import com.github.chenqimiao.io.net.client.MetaDataFetchClient;
import com.github.chenqimiao.io.net.client.MetaDataFetchClientCommander;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 16:11
 **/
@Configuration
@Slf4j
public class MetaDataFetchClientConfig implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext applicationContext;

    private static final List<MetaDataFetchClient> metaDataFetchClients = Lists.newArrayList();


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, MetaDataFetchClient> registerJobs = applicationContext.getBeansOfType(MetaDataFetchClient.class);
        for(Map.Entry<String,MetaDataFetchClient> entry : registerJobs.entrySet()){
            try {
                if (entry.getValue() instanceof MetaDataFetchClientCommander) {
                    continue;
                }
                MetaDataFetchClient metaDataFetchClient = entry.getValue();
                metaDataFetchClients.add(metaDataFetchClient);
            }catch (Exception e){
                log.error("注册清理器异常 beanName:[{}]", entry.getKey(), e);
            }
        }


    }

    public static List<MetaDataFetchClient> getMetaDataFetchClients() {
        return Collections.unmodifiableList(metaDataFetchClients);
    }

}
