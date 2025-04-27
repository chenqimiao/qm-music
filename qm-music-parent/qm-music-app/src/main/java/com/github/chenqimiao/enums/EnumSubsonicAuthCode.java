package com.github.chenqimiao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Qimiao Chen
 * @since 2025/3/28
 **/
@AllArgsConstructor
@Getter
public enum EnumSubsonicAuthCode {
    E_0("0", "A generic error"),
    E_10("10", "Required parameter is missing"),
    E_20("20", "Incompatible Subsonic REST protocol version. Client must upgrade"),
    E_30("30", "Incompatible Subsonic REST protocol version. Server must upgrade"),
    E_40("40", "Wrong username or password"),
    E_41("41", "Token authentication not supported for LDAP users"),
    E_50("50", "User is not authorized for the given operation"),
    E_60("60", "The trial period for the Subsonic server is over. Please upgrade to Subsonic Premium. Visit subsonic.org for details"),
    E_70("70", "The requested data was not found"),
    ;

    private final String code;

    private final String message;

    public static EnumSubsonicAuthCode parseObjByCode(String code){
        Optional<EnumSubsonicAuthCode> instance = Arrays.stream(values()).filter(obj -> obj.getCode().equals(code)).findFirst();
        return instance.orElse(null);
    }
}
