package com.yxt.controller;

import com.yxt.server.Calculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculatorController {
    @Autowired
    private Calculator calculator;

    @PostMapping("/calcu")
    public float calcultor(int type,float a,float b){
        return calculator.calcula(type, a, b);
    }

}
