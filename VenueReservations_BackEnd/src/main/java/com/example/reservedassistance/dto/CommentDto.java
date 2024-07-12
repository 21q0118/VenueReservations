package com.example.reservedassistance.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("管理员看到的评论单个信息")
public class CommentDto {

    @ApiModelProperty("用户的昵称")
    private String userName;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("评论时间")
    private Date commentTime;

    @ApiModelProperty("评论分数")
    private Integer score;
}
