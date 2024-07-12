package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.entity.*;
import com.example.reservedassistance.service.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static java.lang.Math.max;

@Service
public class ScheduledServiceImpl {

    @Resource
    private ActivityService activityService;

    @Resource
    private ReserveService reserveService;

    @Resource
    private UserVisitorService userVisitorService;

    @Resource
    private UserService userService;

    @Resource
    private VisitorService visitorService;

    @Resource
    private StadiumService stadiumService;

    @Resource
    private MessageService messageService;


    @Resource
    private JavaMailSender mailSender;
    /*
        一个是自动更新活动和预约状态
        一个是撤销时自动发布通知消息
        一个是通过验证码和预约成功后自动发布消息和短信
    * */

    @Scheduled(fixedRate = 60000 * 10)  // 60000-每分钟执行一次,这里显示的就是10min
    @ApiOperation("定时更新活动和预约状态")
    public void updateActivityAndReverseStatus() {
        List<Activity> activities = activityService.list();
        for (Activity activity : activities) {
            Date beginTime = activity.getBeginTime();
            Date endTime = activity.getEndTime();
            Date date = new Date();
            if (beginTime.after(date))
                activity.setStatus(Activity.STATUS_WAITING);
            else if (beginTime.before(date) && endTime.after(date))
                activity.setStatus(Activity.STATUS_PROCESSING);
            else
                activity.setStatus(Activity.STATUS_FINISHED);
            activityService.saveOrUpdate(activity);
        }


        //  更新逾期状态和扣除信誉分
        List<Reserve> reserves = reserveService.list();
        for (Reserve reserve : reserves) {
            if (reserve.getReserveStatus().equals(Reserve.STATUS_PROCESSING)) {
                Date date = new Date();
                if (reserve.getReserveEndTime().before(date)) {
                    reserve.setReserveStatus(Reserve.STATUS_NOT_CHECK);
                    Integer visitorId = userVisitorService.getById(reserve.getUserVisitorId()).getVisitorId();
                    // 扣信誉分
                    String telephone = visitorService.getById(visitorId).getTelephone();
                    QueryWrapper<User> wrapper = new QueryWrapper<>();
                    wrapper.eq("telephone", telephone);
                    User user = userService.getOne(wrapper);
                    if (user != null) {
                        user.setScore(max((user.getScore() - 2), 0));
                        userService.saveOrUpdate(user);

                        String realName = visitorService.getById(visitorId).getRealName();
                        Activity activity = activityService.getById(reserve.getActivityId());
                        String stadiumName = stadiumService.getById(activity.getStadiumId()).getStadiumName();

                        Message message = new Message();
                        message.setAcceptId(user.getId());
                        message.setOperateTime(new Date());
                        message.setInvokeId(Message.SYSTEM_INVOKED_ID);
                        message.setContent(messageService.notCheckMessagingUser(realName, activity.getActivityName(), stadiumName, user.getScore()));
                        message.setIsRemove(Message.IS_NOT_REMOVE);
                        messageService.save(message);
                    }
                    reserveService.saveOrUpdate(reserve);

                }
            }
        }
//        List<Reserve> reserves = reserveService.list();
//        for(Reserve reserve : reserves){
//            if(reserve.getReserveStatus().equals("已完成") || reserve.getReserveStatus().equals("已逾期")
//            || reserve.getReserveStatus().equals("已结束"))
//                continue;
////            Date reserveBegTime = reserve.getReserveBegTime();
//            Date reserveEndTime = reserve.getReserveEndTime();
////            Activity activity = activityService.getById(reserve.getActivityId());
//
//            Date date = new Date();
//            if(reserveEndTime.before(date) && reserveService.isInvoked(reserve)){
//                reserve.setReserveStatus("已逾期");
//                Integer visitorId = userVisitorService.getById(reserve.getUserVisitorId()).getVisitorId();
//                // 扣信誉分
//                String telephone = visitorService.getById(visitorId).getTelephone();
//                QueryWrapper<User> wrapper = new QueryWrapper<>();
//                wrapper.eq("telephone" , telephone);
//                User user = userService.getOne(wrapper);
//                if (user != null){
//                    user.setScore(Math.max((user.getScore() - 1), 0));
//                    userService.saveOrUpdate(user);
//                }
//                /*
//                * 该预约是否取消 要判断
//                * */
//            }
//            reserveService.saveOrUpdate(reserve);
    }

    @Scheduled(fixedRate = 60000 * 60 * 24 * 60)  // 60000-每分钟执行一次,这里显示的就是60天
    @ApiOperation("定时增加用户分数")
    public void updateUserScore() {
        List<User> userList = userService.list();
        for (User user : userList) {
            user.setScore(Math.min(user.getScore() + 1, User.DEFAULT_SCORE));
        }

        userService.saveOrUpdateBatch(userList);
    }

    // 推荐算法
    // 评分

    @Scheduled(fixedRate = 60000 * 15)           // 15 min运行一次
    @ApiOperation("定时发送提示参观信息")
    public void remindMessaging() {
        List<Reserve> reserveList = reserveService.list();

        Date date = new Date();

        for (Reserve reserve : reserveList) {
            if (reserve.getReserveStatus().equals(Reserve.STATUS_PROCESSING)) {     // 该预约未被取消且还未开始
                long diffInMinutes = (int) ((reserve.getReserveBegTime().getTime() - date.getTime()) / (60 * 1000));            // 计算预约开始的时间和当前时间相差的多少分钟

                if (diffInMinutes < 20 && diffInMinutes >=0 ) {       // 时间小于20min
                    // 发送信息
                    UserVisitor userVisitor = userVisitorService.getById(reserve.getUserVisitorId());

                    Integer visitorId = userVisitor.getVisitorId();
                    String realName = visitorService.getById(visitorId).getRealName();
                    Activity activity = activityService.getById(reserve.getActivityId());
                    String stadiumName = stadiumService.getById(activity.getStadiumId()).getStadiumName();

                    Integer userId = visitorService.getUserId(visitorId);           // 获得对应的用户id，可能为null
                    // 发消息是要在系统内对有账户的用户发， 没有用户的就不会发相关信息
                    if (userId != null) {
                        // 系统内发信息
                        Message message = new Message();
                        message.setContent(messageService.autoRemindMessagingUser(realName, diffInMinutes, activity.getActivityName(), stadiumName));
                        message.setInvokeId(Message.SYSTEM_INVOKED_ID);
                        message.setAcceptId(userId);
                        message.setOperateTime(new Date());
                        message.setIsRemove(Message.IS_NOT_REMOVE);
                        messageService.save(message);

                        // 发邮箱信息代替发短信
                        String email = userService.getById(userId).getEmail();
                        SimpleMailMessage mailMessage = new SimpleMailMessage();
                        mailMessage.setFrom("1356267228@qq.com");    //设置邮件发送者
                        mailMessage.setTo(email);        //设置邮件接收者
                        mailMessage.setSubject("【智能预约助手】");    //设置邮件主题
                        mailMessage.setText(messageService.autoRemindMessagingUser(realName, diffInMinutes, activity.getActivityName(), stadiumName));  //设置邮件内容
                        mailSender.send(mailMessage);
                    }


                }

            }


        }
    }
}
