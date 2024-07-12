package com.example.reservedassistance.Vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("场馆管理员要修改场馆信息所要传递的数据")
public class StadiumAlterVo {

    @ApiModelProperty("管理员id")
    private Integer managerId;

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

//    @ApiModelProperty("展厅名")
//    private List<String> halls;

}
