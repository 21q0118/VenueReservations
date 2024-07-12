package com.example.reservedassistance.dto;

import com.example.reservedassistance.Vo.ManagerAddVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("管理员查看主页的场馆信息")
public class StadiumDto {

    @ApiModelProperty("场馆id")
    private Integer id;

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

    @ApiModelProperty("展厅信息")
    private List<HallDto> hallDtos;

    @ApiModelProperty("场馆总体得分")
    private Double score;
}
