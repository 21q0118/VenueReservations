package com.example.reservedassistance.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("场馆管理员查看到的活动信息")
public class ActivityDto {

    @ApiModelProperty("活动id")
    private Integer id;

    @ApiModelProperty("活动名")
    private String activityName;

    @ApiModelProperty("活动开始时间")
    private Date beginTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("可预约人数")
    private Integer accessNum;

    @ApiModelProperty("活动简介")
    private String introduction;

    @ApiModelProperty("活动图片存储路径")
    private String imageURL;

    @ApiModelProperty("涉及的所有展厅名")
    private String hallNames;

    @ApiModelProperty("活动状态")
    private String status;

    @ApiModelProperty("场馆名称")
    private String stadiumName;

    @ApiModelProperty("活动分数")
    private Double score;
}
