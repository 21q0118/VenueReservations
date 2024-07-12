package com.example.reservedassistance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("删除消息")
@TableName("message_is_delete")
public class MessageIsDelete {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("消息id")
    @TableField("messageId")
    private Integer messageId;

    @ApiModelProperty("用户id")
    @TableField("userId")
    private Integer userId;
}
