package com.example.reservedassistance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;

@Data
@TableName("message_is_read")
@ApiModel("消息是否已读")
public class MessageIsRead {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("messageId")
    @ApiModelProperty("消息id")
    private Integer messageId;

    @TableField("userId")
    @ApiModelProperty("用户id")
    private Integer userId;
}

