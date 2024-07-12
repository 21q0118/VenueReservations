package com.example.reservedassistance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "场馆发布活动")
@TableName("activity")
public class Activity {


    public static final String STATUS_WAITING = "未开始";

    public static final String STATUS_PROCESSING = "进行中";

    public static final String STATUS_FINISHED = "已结束";

    public static final Integer IS_CANCEL = 1;

    public static final Integer IS_NOT_CANCEL = 0;


    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("活动名")
    @TableField(value = "activity_name")
    private String activityName;

    @ApiModelProperty("活动开始时间")
    @TableField(value = "begin_time")
    private Date beginTime;

    @ApiModelProperty("活动结束时间")
    @TableField(value = "end_time")
    private Date endTime;

    @ApiModelProperty("可预约人数")
    @TableField("access_num")
    private Integer accessNum;

    @ApiModelProperty("活动简介")
    private String introduction;

    @ApiModelProperty("活动图片存储路径")
    @TableField(value = "image_path")
    private String imagePath;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("是否为精华活动")
    @TableField(value = "is_core")
    private Integer isCore;

    @ApiModelProperty("所属的场馆id")
    @TableField(value = "stadiumId")
    private Integer stadiumId;

    @ApiModelProperty("活动撤销")
    @TableField("is_cancel")
    private Integer isCancel;
}
