package com.example.reservedassistance.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("访问者信息")
public class VisitorDto {

    @ApiModelProperty("自增主键")
    private Integer id;

    @ApiModelProperty("访问者姓名")
    private String realName;

    @ApiModelProperty("参观者身份证号")
    private String identificationNum;

    @ApiModelProperty("参观者手机号")
    private String telephone;

}
