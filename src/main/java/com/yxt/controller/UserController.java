package com.yxt.controller;

import com.yxt.dto.UserDto;
import com.yxt.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserMapper userMapper;
    @RequestMapping("/")
    public String hello(){
        return "测试";
    }

    //注册用户
    @PostMapping("/add")
    public Map<String,Object> registerUser(String username, String password){
        Map<String,Object> map = new HashMap<>();
        if (username.isEmpty()){
            System.out.println("用户名不能为空");
            map.put("status",2001);
            map.put("msg","用户名不能为空");
        }else{
            UserDto userDto = userMapper.getUserByName(username);
            if (userDto.getUsername().isEmpty()){
                userMapper.addUser(username,password);
                map.put("status",200);
                map.put("msg","用户添加成功");
            }else {
                map.put("status",2002);
                map.put("msg","用户已存在，请使用新的用户名注册");
            }
        }
        return map;
    }

    @PostMapping("/login")
    public Map<String,Object> login(String username,String password){
        Map<String,Object> map = new HashMap<>();
        UserDto userDto = userMapper.getUserByName(username);
        if (userDto == null){
            map.put("status",2003);
            map.put("msg","用户不存在");
        }else if (userDto.getPassword().equals(password)){
            map.put("status",200);
            map.put("msg","用户登录成功");
        }else if (password.isEmpty()){
            map.put("status",204);
            map.put("msg","用户登录成功");
            return map;
        }

        return map;
    }

    @GetMapping("/getAllUser")
    public Map<String,Object> selectAll(){
        Map<String,Object> map = new HashMap<>();
        List<UserDto> user = userMapper.getAllUser();
        map.put("status",200);
        map.put("msg","查询所有用户");
        map.put("user",user);
        return map;
    }


}
