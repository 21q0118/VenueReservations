package com.example.reservedassistance.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.entity.*;
import com.example.reservedassistance.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@Api(tags = "扫码验证接口")
@RequestMapping(value = "/check")
@CrossOrigin
public class CheckTicketController {

    @Resource
    private VisitorService visitorService;

    @Resource
    private UserService userService;

    @Resource
    private UserVisitorService userVisitorService;

    @Resource
    private ReserveService reserveService;

    @Resource
    private MessageService messageService;

    @Resource
    private ActivityService activityService;

    @Resource
    private StadiumService stadiumService;

    @Resource
    private JavaMailSender mailSender;

    @GetMapping("/checkPass")
    @ApiOperation("验票接口")
    public Result<String> checkPass(@RequestParam("telephone") String telephone, @RequestParam("activityId") Integer activityId
                        ,@RequestParam("userName") String userName){

        // 根据活动id和手机号定位到具体的uservisitorId



        Reserve reserve = reserveService.getReserveInf(telephone, activityId);
        if(reserve == null)
            return Result.fail("预约检票异常");
        if(reserve.getReserveStatus().equals(Reserve.STATUS_PASS_CHECK))
            return Result.fail("请勿重复扫码");
        reserve.setReserveStatus(Reserve.STATUS_PASS_CHECK);
        reserveService.saveOrUpdate(reserve);

        String url = "http://127.0.0.1:5000" + "/show_inf/" + userName;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String result = responseEntity.getBody();
        Activity activity = activityService.getById(activityId);

        String stadiumName = stadiumService.getById(activity.getStadiumId()).getStadiumName();
        String inf = messageService.checkMessagingUser(userName, activity.getActivityName(), stadiumName);

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("telephone", telephone);
        User user = userService.getOne(wrapper);
        if(user != null) {
            Message message = new Message();
            message.setInvokeId(Message.SYSTEM_INVOKED_ID);
            message.setAcceptId(user.getId());
            message.setOperateTime(new Date());
            message.setContent(inf);
            message.setIsRemove(Message.IS_NOT_REMOVE);
            messageService.saveOrUpdate(message);
        }
        // 发邮箱信息代替发短信
        String email = user.getEmail();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("1356267228@qq.com");    //设置邮件发送者
        mailMessage.setTo(email);        //设置邮件接收者
        mailMessage.setSubject("【智能预约助手】");    //设置邮件主题
        mailMessage.setText(inf);  //设置邮件内容
        mailSender.send(mailMessage);

        System.out.println("lalalla");
        return Result.success(userName + "已完成验票");

    }

}

