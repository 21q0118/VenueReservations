package com.example.reservedassistance.controller;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.dto.LoginDto;
import com.example.reservedassistance.entity.Manager;
import com.example.reservedassistance.entity.User;
import com.example.reservedassistance.service.ManagerService;
import com.example.reservedassistance.service.RedisService;
import com.example.reservedassistance.service.UserService;
import com.example.reservedassistance.utils.AESUtils;
import com.example.reservedassistance.utils.JudgeUtils;
import com.example.reservedassistance.utils.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpSession;
import java.util.Random;
import java.util.regex.Pattern;

@RestController
@Api(tags = "登陆接口")
@RequestMapping(value = "/login")
@CrossOrigin
public class LoginController {


    @Resource
    private UserService userService;

    @Resource
    private ManagerService managerService;

    @Resource
    private RedisService redisService;

    @Resource
    private JavaMailSender mailSender;

    @GetMapping("/login")
    @ApiOperation("统一登陆接口")
    public Result<LoginDto> login(@RequestParam("telOrUserName")String telOrUserName, @RequestParam("password") String password) throws Exception {
        LoginDto loginDto = new LoginDto();

        // 如果是用户输入
        if(JudgeUtils.isValidPhoneNumber(telOrUserName)){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("telephone", telOrUserName);
            User user = userService.getOne(queryWrapper);

            if (user == null)
                return Result.fail("输入手机号或密码有误");
            String passwordValid = user.getUserPassword();
            SecretKey secretKey = AESUtils.convertStringToSecretKey(user.getKeyValue());
            String passwordConvert = AESUtils.decrypt(passwordValid, secretKey);
            if(password.equals(passwordConvert)){
                loginDto.setId(user.getId());
                loginDto.setType("用户");
                //
                //
                String sign = TokenUtil.sign(telOrUserName, password, user.getId(), "用户");
                loginDto.setToken(sign);
                //
                //
                return Result.success(loginDto);
            }
        }

        QueryWrapper<Manager> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("manager_user_name", telOrUserName);
        Manager manager = managerService.getOne(queryWrapper);
        if(manager == null)
            return Result.fail("输入手机号或密码有误");
        String passwordValid = manager.getManagerPassword();
        SecretKey secretKey = AESUtils.convertStringToSecretKey(manager.getKeyValue());
        String passwordConvert = AESUtils.decrypt(passwordValid, secretKey);
        if(password.equals(passwordConvert)){
            loginDto.setId(manager.getId());
            if(manager.getIsSuper().equals(1))
                loginDto.setType("超级管理员");
            else
                loginDto.setType("场馆管理员");
            //
            //
            String sign = TokenUtil.sign(telOrUserName, password, loginDto.getId(), loginDto.getType());
            loginDto.setToken(sign);
            //
            //
            return Result.success(loginDto);
        }
        return Result.fail("输入手机号或密码有误");

    }

    private Boolean judgeIsTel(String str){
        Pattern pattern = Pattern.compile("^[0-9]+$");
        return pattern.matcher(str).matches();
    }


    @GetMapping("/loginByJudgeCode")
    @ApiOperation("使用验证码登陆")
    public Result<LoginDto> loginByJudgeCode(@RequestParam("email") String email, @RequestParam("judgeCode") String judgeCode){
        String verificationCode = redisService.getVerificationCode(email);
        System.out.println(verificationCode);
        LoginDto loginDto = new LoginDto();
        if(judgeCode.equals(verificationCode)){
            redisService.removeVerificationCode(email);  // 先确保验证码失效

            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("email", email).select("id");
            User user = userService.getOne(wrapper);
            //
            //
            String sign = TokenUtil.sign(email, judgeCode, user.getId(), "用户");
            loginDto.setToken(sign);
            //
            //
            loginDto.setId(user.getId());
            loginDto.setType("用户");
            return Result.success(loginDto);
        }
        return Result.fail("输入手机号或密码有误");

    }
    @GetMapping("/generateCode")    //完成验证身份证
    @ApiOperation("生成验证码，返回值为验证码")
    public Result<String> generateCode(@RequestParam("email") String email) {

        String judgeCode = getJudgeCode();
        String[] list = email.split("@");
        String context = "用于QQ邮箱身份验证，3分钟内有效。请勿忽略和转发。如非本人操作，请忽略此短信";
//        if(list[1].equals("qq.com")){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("1356267228@qq.com");    //设置邮件发送者
        message.setTo(email);        //设置邮件接收者
        message.setSubject("【智能预约助手】");    //设置邮件主题
        message.setText("验证码为" + judgeCode + " " + context);        //设置邮件内容
        mailSender.send(message);

        redisService.storeVerificationCode(email, judgeCode);
        System.out.println(redisService.getVerificationCode(email));
        //  在这里要把信息存入redis


        return Result.success(judgeCode);
    }

    private String getJudgeCode() {
        Random random = new Random();
        Integer randomNumber = random.nextInt(900000) + 100000;
        return randomNumber.toString();
    }
}
