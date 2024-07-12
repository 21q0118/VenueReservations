package com.example.reservedassistance.controller;

import cn.hutool.core.io.file.FileNameUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.Vo.UserFileVo;
import com.example.reservedassistance.config.FileConfig;
import com.example.reservedassistance.entity.Stadium;
import com.example.reservedassistance.entity.User;
import com.example.reservedassistance.service.StadiumService;
import com.example.reservedassistance.service.UserService;
import com.example.reservedassistance.utils.FileUtils;
import com.example.reservedassistance.utils.ImageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@RestController
@Api(tags = "上传文件的基本接口")
@RequestMapping(value = "/uploadfile")
@CrossOrigin
public class UploadFileController {

    @Resource
    private FileConfig fileConfig;

    @Resource
    private UserService userService;

    @Resource
    private StadiumService stadiumService;

    @PostMapping("/uploadfile")
    @ApiOperation("上传文件")
    public Result<String> uploadFile(@RequestBody MultipartFile file) {
        try {
            long l = System.currentTimeMillis();
            String suffix = FileNameUtil.getSuffix(file.getOriginalFilename());
            String destPath = fileConfig.getBasePath() + File.separator + l + "." + suffix;
            file.transferTo(new File(destPath));
            String url = fileConfig.getPrefix() + l + "." + suffix;
            return Result.success(url);
        } catch (IOException e) {
            return Result.fail("上传文件失败");
        }
    }

    @PostMapping(path = "/userImageSaveOrUpdate")
    @ApiOperation("用户上传或更新头像")
    public Result<String> userImageSaveOrUpdate(@RequestParam("userId") Integer userId,
                                                @RequestParam("multipartFile") MultipartFile multipartFile,
                                                @RequestHeader(name = "token")String token) {

        User user = userService.getById(userId);
        if (user == null) {
            return Result.fail("你TMD传个对的id行不行");
        }

        try {
            long l = System.currentTimeMillis();
            String suffix = FileNameUtil.getSuffix(multipartFile.getOriginalFilename());

            String destPath = fileConfig.getBasePath() + File.separator + l + "." + suffix;
            if (!ImageUtils.isImage(destPath))
                return Result.fail("请确保您输入的文件格式为jpg、png、jpeg、gif、bmp、webp、svg这几种格式的一种");
            multipartFile.transferTo(new File(destPath));
            user.setImagePath(destPath);
            userService.saveOrUpdate(user);
            return Result.success(FileUtils.convertPathToURL(destPath));
        } catch (IOException e) {
            return Result.fail("上传文件失败");
        }
    }

    @PostMapping("/managerImageSaveOrUpdate")
    @ApiOperation("管理员上传或更新图片")
    public Result<String> managerImageSaveOrUpdate(@RequestParam("managerId") Integer managerId,
                                                   @RequestParam("multipartFile") MultipartFile multipartFile,
                                                   @RequestHeader(name = "token")String token) {

        QueryWrapper<Stadium> wrapper = new QueryWrapper<>();
        wrapper.eq("managerId", managerId);
        Stadium stadium = stadiumService.getOne(wrapper);
        try {
            long l = System.currentTimeMillis();
            String suffix = FileNameUtil.getSuffix(multipartFile.getOriginalFilename());

            String destPath = fileConfig.getBasePath() + File.separator + l + "." + suffix;
            if (!ImageUtils.isImage(destPath))
                return Result.fail("请确保您输入的文件格式为jpg、png、jpeg、gif、bmp、webp、svg这几种格式的一种");
            multipartFile.transferTo(new File(destPath));
//            stadium.setImagePath(destPath);
//            stadiumService.saveOrUpdate(stadium);
            return Result.success(FileUtils.convertPathToURL(destPath));
        } catch (IOException e) {
            return Result.fail("上传问价失败");
        }

    }


}
