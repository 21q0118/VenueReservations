package com.example.reservedassistance.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("用户登录首页获得的信息")
public class UserHomeInfDto {

    @ApiModelProperty("用户登录首页看到的个人信息")
    private UserDto userDto;

    @ApiModelProperty("用户登录首页看到的所有场馆信息")
    private List<StadiumDto> stadiumDtos;
}
