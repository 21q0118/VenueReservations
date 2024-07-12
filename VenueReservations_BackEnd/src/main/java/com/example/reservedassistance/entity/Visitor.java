package com.example.reservedassistance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "参观者基本信息")
@TableName("visitor")
public class Visitor {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("访问者姓名")
    @TableField("real_name")
    private String realName;

    @ApiModelProperty("参观者身份证号")
    @TableField("identification_num")
    private String identificationNum;

    @ApiModelProperty("参观者手机号")
    private String telephone;

    @ApiModelProperty("加密密钥")
    @TableField("key_value")
    private String keyValue;
}
