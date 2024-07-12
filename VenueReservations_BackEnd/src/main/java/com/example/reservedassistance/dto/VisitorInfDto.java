package com.example.reservedassistance.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("访问者内容信息")
public class VisitorInfDto {

    @ApiModelProperty("访问者姓名")
    private String realName;

    @ApiModelProperty("参观者身份证号")
    private String identificationNum;

    @ApiModelProperty("参观者手机号")
    private String telephone;
}
