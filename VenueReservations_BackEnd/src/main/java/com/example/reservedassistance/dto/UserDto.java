package com.example.reservedassistance.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户登录首页获得的个人信息")
public class UserDto {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户人名")
    private String realName;

    @ApiModelProperty("用户身份证号")
    private String identificationNum;

    @ApiModelProperty("用户上传头像的文件URL")
    private String imageURL;

    @ApiModelProperty("用户的邮箱")
    private String email;

    @ApiModelProperty("用户手机号")
    private String telephone;

    @ApiModelProperty("用户昵称")
    private String userName;

    @ApiModelProperty("信誉分")
    private Integer score;
}
