package com.example.reservedassistance.Vo;

import com.example.reservedassistance.entity.Hall;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StadiumAlterSuperVo {

    @ApiModelProperty("系统管理员id")
    private Integer superManagerId;

    @ApiModelProperty("场馆id")
    private Integer stadiumId;

    @ApiModelProperty("场馆名")
    private String stadiumName;

    @ApiModelProperty("场馆位置")
    private String position;

    @ApiModelProperty("场馆联系电话")
    private String telephone;

    @ApiModelProperty("简介")
    private String introduction;

    @ApiModelProperty("场馆图像的存储路径")
    private String imageURL;

    @ApiModelProperty("场馆开馆时间")
    private Date beginTime;

    @ApiModelProperty("场馆闭馆时间")
    private Date endTime;

    @ApiModelProperty("调整的展厅信息")
    private List<Hall> halls;
}
