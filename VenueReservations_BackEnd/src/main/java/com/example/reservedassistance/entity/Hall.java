package com.example.reservedassistance.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "场馆涉及展厅")
@TableName("hall")
public class Hall {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("展厅名")
    @TableField(value = "hall_name")
    private String HallName;

    @ApiModelProperty("场馆id")
    @TableField(value = "stadiumId")
    private Integer stadiumId;
}
