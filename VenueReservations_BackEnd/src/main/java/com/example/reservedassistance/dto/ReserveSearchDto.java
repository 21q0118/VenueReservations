package com.example.reservedassistance.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("用户查询的自己的所有预约信息")
public class ReserveSearchDto {

    @ApiModelProperty("预约的信息数据")
    private List<ReserveDto> reserveDtos;

    @ApiModelProperty("取消预约的信息数据")
    private List<ReserveNotDto> reserveNotDtos;

}
