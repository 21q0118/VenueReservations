package com.example.reservedassistance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.File;

@Data
@ApiModel(description = "普通用户基本信息")
@TableName("user")
public class User {

    public static final Integer DEFAULT_SCORE = 5;

    public static final String DEFAULT_PATH = "E:\\ReservedAssistanceFile" + File.separator + "用户默认头像.png";

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户人名")
    @TableField(value = "real_name")
    private String realName;

    @ApiModelProperty("用户身份证号")
    @TableField(value = "identification_num")
    private String identificationNum;

    @ApiModelProperty("用户上传头像的文件存储路径")
    @TableField(value = "image_path")
    private String imagePath;

    @ApiModelProperty("用户的邮箱")
    private String email;

    @ApiModelProperty("用户手机号")
    private String telephone;

    @ApiModelProperty("用户昵称")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("用户密码")
    @TableField("user_password")
    private String userPassword;

    @ApiModelProperty("加密密钥")
    @TableField("key_value")
    private String keyValue;


    @ApiModelProperty("信誉分")
    private Integer score;
}
