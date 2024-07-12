package com.example.reservedassistance.Vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserFileVo {

    @ApiModelProperty("上传文件")
    private MultipartFile multipartFile;

    @ApiModelProperty("用户id")
    private Integer userId;

}
