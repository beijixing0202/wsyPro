package com.yxt.enums;

public enum CalculaTypeEnum {
    PLUS(1,"加法"),
    subtract(0,"减法");

    private int type;
    private String desc;
    CalculaTypeEnum(int type,String desc){
        this.type = type;
        this.desc = desc;
    }
    public int getType(){
        return type;
    }
    public String getDesc(){
        return desc;
    }

}
