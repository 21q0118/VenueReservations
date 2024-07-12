package com.example.reservedassistance.Vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("新增场馆管理员账号数据")
public class ManagerAddVo {

    @ApiModelProperty("管理员账号用户名")
    private String managerUserName;

}
