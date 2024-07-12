package com.example.reservedassistance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "用户的评价信息")
@TableName("comment")
public class Comment {

    public static final Integer IS_COMMENT = 1;

    public static final Integer IS_NOT_COMMENT = 0;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("自增主键")
    private Integer id;

    @ApiModelProperty("评论的活动id")
    @TableField("activityId")
    private Integer activityId;

    @ApiModelProperty("评论的用户id")
    @TableField("userId")
    private Integer userId;

    @ApiModelProperty("用户评论的内容")
    private String content;

    @ApiModelProperty("用户评价的分数吗，为1-5的整数")
    private Integer score;

    @ApiModelProperty("用户评价的时间")
    @TableField("comment_time")
    private Date commentTime;

    @ApiModelProperty("是否评论")
    @TableField("is_comment")
    private Integer isComment;
}
