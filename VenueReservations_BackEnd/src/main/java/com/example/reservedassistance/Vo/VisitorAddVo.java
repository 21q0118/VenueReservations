package com.example.reservedassistance.Vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("参观者新增信息")
public class VisitorAddVo {

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("访问者真实姓名")
    private String realName;

    @ApiModelProperty("访问者身份证号")
    private String identificationNum;

    @ApiModelProperty("访问者电话号码")
    private String telephone;
}
