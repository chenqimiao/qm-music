package com.github.chenqimiao.qmmusic.core.util;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

/**
 * @author Qimiao Chen
 * @since 2025/5/1
 **/
public abstract class OrderUtils {

    // 获取Bean的Order值的方法
    public static int getOrderValue(Object client) {
        // 优先检查是否实现了Ordered接口
        if (client instanceof Ordered) {
            return ((Ordered) client).getOrder();
        }
        // 获取目标类（处理可能的AOP代理）
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(client);
        // 查找@Order注解
        Order order = AnnotationUtils.findAnnotation(targetClass, Order.class);
        return (order != null) ? order.value() : Ordered.LOWEST_PRECEDENCE;
    }

}
