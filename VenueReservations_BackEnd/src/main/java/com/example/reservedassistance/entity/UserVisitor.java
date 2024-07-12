package com.example.reservedassistance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "用户访问者基本表")
@TableName("user_visitor")
public class UserVisitor {

    public static final Integer IS_VALID = 1;       // 有效

    public static final Integer IS_NOT_VALID = 0;

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户id")
    @TableField("userId")
    private Integer userId;

    @ApiModelProperty("访问者id")
    @TableField("visitorId")
    private Integer visitorId;

    @ApiModelProperty("是否有效")
    @TableField("is_valid")
    private Integer isValid;
}
