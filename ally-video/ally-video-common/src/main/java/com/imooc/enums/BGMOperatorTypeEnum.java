package com.imooc.enums;

/**
 * @author allycoding
 * @Date: 2020/11/3 19:38
 */
public enum BGMOperatorTypeEnum {

    ADD("1","添加bgm"),
    DELETE("2","删除bgm");

    public final String type;
    public final String value;

    BGMOperatorTypeEnum(String type, String value){
        this.type = type;
        this.value = value;
    }

    public String getType(){
        return type;
    }

    public String getValue(){
        return value;
    }

    public static String getValueByKeys(String key){
        for(BGMOperatorTypeEnum typeEnum: BGMOperatorTypeEnum.values()){
            if(typeEnum.getType().equals(key)){
                return typeEnum.value;
            }
        }
        return null;
    }

}
