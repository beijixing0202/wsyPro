package com.yxt.server.impl;

import com.yxt.enums.CalculaTypeEnum;
import com.yxt.server.Calculator;
import org.apache.ibatis.jdbc.Null;
import org.springframework.stereotype.Service;

@Service
public class CalculatorImpl implements Calculator {
    @Override
    public float calcula(int type,float a, float b) {
        if (type == CalculaTypeEnum.PLUS.getType() || type == CalculaTypeEnum.subtract.getType()){
            return type == CalculaTypeEnum.PLUS.getType() ?a+b:a-b;
        }else {
            System.out.println("不支持的计算类型");
            return -1;

        }
    }
}
