package com.example.reservedassistance.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("登陆时要返回给前端的数据")
public class LoginDto {

    @ApiModelProperty("用户或者是管理员的id")
    private Integer id;

    @ApiModelProperty("表明是管理员或者是用户")
    private String type;

    @ApiModelProperty("token字符串")
    private String token;

}
