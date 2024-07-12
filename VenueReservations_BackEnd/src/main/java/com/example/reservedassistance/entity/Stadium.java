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
@ApiModel("场馆信息")
@TableName("stadium")
public class Stadium {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("场馆名")
    @TableField("stadium_name")
    private String stadiumName;

    @ApiModelProperty("场馆位置")
    private String position;

    @ApiModelProperty("场馆联系电话")
    private String telephone;

    @ApiModelProperty("简介")
    private String introduction;

    @ApiModelProperty("场馆图像的存储路径")
    @TableField(value = "image_path")
    private String imagePath;

    @ApiModelProperty("管理员账号id")
    @TableField(value = "managerId")
    private Integer managerId;

    @ApiModelProperty("绑定的可修改密码的电话号码")
    @TableField(value = "manager_telephone")
    private String managerTelephone;

    @ApiModelProperty("场馆开馆时间")
    @TableField(value = "begin_time")
    private Date beginTime;

    @ApiModelProperty("场馆闭馆时间")
    @TableField(value = "end_time")
    private Date endTime;
}
