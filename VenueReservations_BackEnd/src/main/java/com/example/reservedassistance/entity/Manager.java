package com.example.reservedassistance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("场馆管理员")
@TableName("manager")
public class Manager {
    public static final String DEFALUT_PASSWORD = "123456";

    public static final Integer IS_SUPER = 1;

    public static final Integer IS_NOT_SUPER = 0;

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("管理员账号用户名")
    @TableField("manager_user_name")
    private String managerUserName;

    @ApiModelProperty("管理员账号密码")
    @TableField("manager_password")
    private String managerPassword;

    @ApiModelProperty("加密密钥")
    @TableField(value = "key_value")
    private String keyValue;

    @ApiModelProperty("是否为系统管理员")
    @TableField("is_super")
    private Integer isSuper;
}
