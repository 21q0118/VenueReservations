package com.example.reservedassistance.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("预约取消表")
@TableName("reserve_cancel")
public class ReserveCancel {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("预约id")
    @TableField("reserve_id")
    private Integer reserveId;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    private Integer userId;
}
