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
@ApiModel("预约表主信息")
@TableName("reserve")
public class Reserve {

    public static final Integer IS_RESERVE = 1;         // 预约

    public static final Integer IS_NOT_RESERVE = 0;     // 取消预约

    public static final String STATUS_PASS_CHECK = "已完成";

    public static final String STATUS_NOT_CHECK = "已逾期";

    public static final String STATUS_PROCESSING = "未开始";

    public static final String STATUS_CANCEL = "已取消";

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("预约的操作时间")
    @TableField("operate_time")
    private Date operateTime;

    @ApiModelProperty("预约或取消预约活动的id")
    @TableField("activityId")
    private Integer activityId;

    @ApiModelProperty("发起的参观者的id")
    @TableField("userVisitorId")
    private Integer userVisitorId;

    @ApiModelProperty("预约还是取消预约")
    @TableField("is_reserve")
    private Integer isReserve;

    @ApiModelProperty("预约活动的开始时间")
    @TableField("reserve_beg_time")
    private Date reserveBegTime;

    @ApiModelProperty("预约活动的结束时间")
    @TableField("reserve_end_time")
    private Date reserveEndTime;

//    @ApiModelProperty("用户是否逻辑移除")
//    @TableField("is_remove")
//    private Integer isRemove;

    @ApiModelProperty("预约的状态")
    @TableField("reserve_status")
    private String reserveStatus;

}
