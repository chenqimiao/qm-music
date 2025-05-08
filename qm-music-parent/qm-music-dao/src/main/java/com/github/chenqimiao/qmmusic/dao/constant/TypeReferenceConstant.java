package com.github.chenqimiao.qmmusic.dao.constant;

import com.alibaba.fastjson2.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 16:32
 **/
public class TypeReferenceConstant {

    public static final TypeReference<List<Map<String, String>>> TYPE_LIST_MAP_STRING_STRING =  new TypeReference<>(){};

}
