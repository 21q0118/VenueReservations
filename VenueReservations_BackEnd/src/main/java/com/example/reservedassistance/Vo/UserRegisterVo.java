package com.example.reservedassistance.Vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;


@Data
@ApiModel("用户注册时要传入的参数")
public class UserRegisterVo {

    @ApiModelProperty("用户人名")
    private String realName;

    @ApiModelProperty("用户身份证号")
    private String identificationNum;

    @ApiModelProperty("上传文件后获得的URL")
    private String imageURL;

    @ApiModelProperty("用户的邮箱")
    private String email;

    @ApiModelProperty("用户手机号")
    private String telephone;

    @ApiModelProperty("用户昵称")
    private String userName;

    @ApiModelProperty("用户密码")
    private String userPassword;

    @ApiModelProperty("验证码")
    private String judgeCode;

}
