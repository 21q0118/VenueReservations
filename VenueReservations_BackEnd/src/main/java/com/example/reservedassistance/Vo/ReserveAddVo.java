package com.example.reservedassistance.Vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("预约新增信息")
public class ReserveAddVo {

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("预约或取消预约活动的id")
    private Integer activityId;

    @ApiModelProperty("发起的参观者的id")
    private List<Integer> visitorIds;

    @ApiModelProperty("预约活动的开始时间")
    private Date reserveBegTime;

    @ApiModelProperty("预约活动的结束时间")
    private Date reserveEndTime;

//    @ApiModelProperty("预约的状态")
//    @TableField("reserve_status")
//    private String reserveStatus;

}
