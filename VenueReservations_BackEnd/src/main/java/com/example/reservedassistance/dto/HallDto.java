package com.example.reservedassistance.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("展厅信息")
public class HallDto {

    @ApiModelProperty("展厅id")
    private Integer id;

    @ApiModelProperty("展厅名")
    private String hallName;
}
