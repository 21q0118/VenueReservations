package com.example.reservedassistance.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("场馆管理员获得的首页信息")
public class ManagerHomeInfDto {

    @ApiModelProperty("获得的场馆信息")
    private StadiumDto stadiumDto;

    @ApiModelProperty("获得的所有活动信息")
    private List<ActivityDto> activityDtos;
}
