package com.example.reservedassistance.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VisitorExcel {

    @ApiModelProperty("真实姓名")
    @ExcelProperty("姓名")
    private String realName;

    @ApiModelProperty("身份证号")
    @ExcelProperty("身份证号")
    private String identificationNum;

    @ApiModelProperty("电话号码")
    @ExcelProperty("电话号码")
    private String telephone;
}
