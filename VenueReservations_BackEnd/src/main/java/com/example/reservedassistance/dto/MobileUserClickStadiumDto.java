package com.example.reservedassistance.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("移动端点击具体场馆获得的活动信息")
public class MobileUserClickStadiumDto {

    @ApiModelProperty("获得的场馆信息")
    private StadiumDto stadiumDto;

    @ApiModelProperty("所有的活动信息")
    private List<List<ActivityDto>> activityDtos;

}
