package com.example.reservedassistance.Vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("向全体用户发布消息的通知信息")
public class MessageVo {

    @ApiModelProperty("发布者id, 为管理员id")
    private Integer invokedId;

    @ApiModelProperty("发布的信息内容")
    private String content;

}
