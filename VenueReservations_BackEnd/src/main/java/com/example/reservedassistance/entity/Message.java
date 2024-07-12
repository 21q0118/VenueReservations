package com.example.reservedassistance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("消息")
@TableName("message")
public class Message {

    public static final Integer ALL_ACCEPTED_ID = 0;

    public static final Integer SYSTEM_INVOKED_ID = 0;

    public static final Integer IS_CANCEL = 1;    //被删除

    public static final Integer IS_REMOVE = 1;

    public static final Integer IS_NOT_REMOVE = 0;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("自增主键")
    private Integer id;

    @ApiModelProperty("发起者id，为管理员的id")
    @TableField("invokeId")
    private Integer invokeId;

    @ApiModelProperty("接受者id，为用户id")
    @TableField("acceptId")
    private Integer acceptId;

    @ApiModelProperty("发送内容")
    private String content;

    @ApiModelProperty("操作时间")
    private Date operateTime;

    @ApiModelProperty("is_remove")
    private Integer isRemove;

}
