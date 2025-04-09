package com.wufeiqun.zeus.biz.cicd.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RuntimeVersion Enum
 */
@Getter
public enum RuntimeVersionEnum {
    JDK8("JDK","8"),
    JDK17("JDK","17"),
    JDK21("JDK","21"),
    NODEJS18("NODEJS","18"),
    NODEJS20("NODEJS","20"),
    NODEJS22("NODEJS","22");

    private final String language;
    private final String version;

    private static final Map<String, List<RuntimeVersionEnum>> languageToSupportedVersionListMap;

    static{
        languageToSupportedVersionListMap = new HashMap<>(RuntimeVersionEnum.values().length);
        for (RuntimeVersionEnum item : RuntimeVersionEnum.values()) {
            if (languageToSupportedVersionListMap.containsKey(item.getLanguage())){
                languageToSupportedVersionListMap.get(item.getLanguage()).add(item);
            } else {
                List<RuntimeVersionEnum> list = new ArrayList<>();
                list.add(item);
                languageToSupportedVersionListMap.put(item.getLanguage(), list);
            }
        }
    }

    RuntimeVersionEnum(String language, String version) {
        this.language = language;
        this.version = version;
    }

    public static List<RuntimeVersionEnum> getJDKList(){return languageToSupportedVersionListMap.get("JDK");}

    public static List<RuntimeVersionEnum> getNODEJSList(){return languageToSupportedVersionListMap.get("NODEJS");}
}
