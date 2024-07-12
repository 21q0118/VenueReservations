package com.example.reservedassistance.Vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ActivityAddVo {

    @ApiModelProperty("管理员id")
    private Integer managerId;

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

    @ApiModelProperty("涉及的展厅")
    private List<Integer> hallIdList;
}
