package com.example.reservedassistance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.entity.User;
import com.example.reservedassistance.service.RedisService;
import com.example.reservedassistance.service.UserService;
import com.example.reservedassistance.utils.AESUtils;
import com.example.reservedassistance.utils.JudgeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@RestController
@Api(tags = "邮箱重置密码接口")
@RequestMapping(value = "/reset")
@CrossOrigin
public class ResetPasswordController {

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private RedisService redisService;

    @Resource
    private UserService userService;
//    @PostMapping("/resetpassword")
//    @ApiOperation("修改密码，返回值为布尔型数据")
//    public Result<String> resetPassword(@RequestBody PasswordReset passwordReset) throws NoSuchAlgorithmException, InvalidKeyException {
//        String email = passwordReset.getEmail();
//        String password = passwordReset.getPassword();
//        Integer id = residentInformationService.searchIdByEmail(email);
//
//        LambdaQueryWrapper<Login> wrapper = new LambdaQueryWrapper<Login>();
//        wrapper.eq(Login::getResidentID,id);
//        Login login = loginService.getOne(wrapper);
//
//        System.out.println(password);
//        loginService.updatePassword(id,password);
//        //修改密码
//        return Result.success("密码修改成功");
//    }



    @GetMapping("/resetPassword")
    @ApiOperation("重置密码")
    public Result<String> resetPassword(@RequestParam("email") String email, @RequestParam("passwordNew") String passwordNew,
                                        @RequestParam("judgeCode") String judgeCode) throws Exception {

        String verificationCode = redisService.getVerificationCode(email);

        if(judgeCode.equals(verificationCode)){
            redisService.removeVerificationCode(email);
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("email", email);
            User user = userService.getOne(wrapper);

            SecretKey secretKey = AESUtils.generateSecretKey();

            String passwordEncrypt = AESUtils.encrypt(passwordNew, secretKey);
            String identificationNum = user.getIdentificationNum();
            String identificationNumEncrypt = AESUtils.encrypt(identificationNum, secretKey);
            user.setKeyValue(AESUtils.convertSecretKeyToString(secretKey));
            user.setUserPassword(passwordEncrypt);
            user.setIdentificationNum(identificationNumEncrypt);
            userService.saveOrUpdate(user);
            return Result.success("重置密码成功");
        }

        return Result.fail("验证码错误或失效");
    }

    private String getJudgeCode() {
        Random random = new Random();
        Integer randomNumber = random.nextInt(900000) + 100000;
        return randomNumber.toString();
    }
}
