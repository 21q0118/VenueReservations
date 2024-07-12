package com.example.reservedassistance.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("管理员看到的所有评论信息")
public class CommentInfDto {

    @ApiModelProperty("活动平均分数")
    private double averageScore;

    @ApiModelProperty("单个评价信息")
    private List<CommentDto> commentDtos;
}
