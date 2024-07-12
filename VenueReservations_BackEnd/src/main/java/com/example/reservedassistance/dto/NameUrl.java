package com.example.reservedassistance.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("返回到URL数据")
public class NameUrl {


    @ApiModelProperty("用户姓名")
    private String realName;

    @ApiModelProperty("返回到二维码")
    private String url;
}
