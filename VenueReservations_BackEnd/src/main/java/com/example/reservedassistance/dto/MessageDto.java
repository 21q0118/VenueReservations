package com.example.reservedassistance.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("用户看到的消息信息")
public class MessageDto {

    @ApiModelProperty("消息的id")
    private Integer id;

    @ApiModelProperty("发消息的机构")
    private String invokeName;

    @ApiModelProperty("发消息的内容")
    private String content;

    @ApiModelProperty("操作时间")
    private Date operateTime;

    @ApiModelProperty("是否已读")
    private String isRead;      // 已读、未读
}
