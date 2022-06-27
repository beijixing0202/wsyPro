package com.yxt.mapper;

import com.yxt.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
        void addUser(String username, String password);
        UserDto getUserByName(String username);
        List<UserDto> getAllUser();

}
