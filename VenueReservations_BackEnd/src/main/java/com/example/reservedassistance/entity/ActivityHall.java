package com.example.reservedassistance.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "活动涉及展厅")
@TableName("activity_hall")
public class ActivityHall {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("活动id")
    @TableField("activityId")
    private Integer activityId;

    @ApiModelProperty("展厅id")
    @TableField("hallId")
    private Integer hallId;
}
