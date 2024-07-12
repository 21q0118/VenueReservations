package com.example.reservedassistance.Vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("前端发送到用户评价内容")
public class CommentVo {

    @ApiModelProperty("预约id")
    private Integer reserveId;

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("用户评分")
    private Integer score;
}
