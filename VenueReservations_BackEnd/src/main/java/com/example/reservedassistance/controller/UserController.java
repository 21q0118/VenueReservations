package com.example.reservedassistance.controller;

import cn.hutool.core.io.file.FileNameUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.reservedassistance.MsgListener.ReserveListener;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.Vo.CommentVo;
import com.example.reservedassistance.Vo.ReserveAddVo;
import com.example.reservedassistance.Vo.UserRegisterVo;
import com.example.reservedassistance.Vo.VisitorAddVo;
import com.example.reservedassistance.config.FileConfig;
import com.example.reservedassistance.dto.*;
import com.example.reservedassistance.entity.*;
import com.example.reservedassistance.service.*;
import com.example.reservedassistance.strategy.MessageSortStrategy;
import com.example.reservedassistance.strategy.MobileReserveSortStrategy;
import com.example.reservedassistance.strategy.RecommendStrategy;
import com.example.reservedassistance.strategy.ReserveSortStrategy;
import com.example.reservedassistance.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.util.*;

import org.springframework.beans.BeanUtils;

@RestController
@Api(tags = "用户的基本接口")
@RequestMapping(value = "/users")
@CrossOrigin
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private FileConfig fileConfig;

    @Resource
    private ReserveSortStrategy reserveSortStrategy;

    @Resource
    private RedisService redisService;

    @Resource
    private StadiumService stadiumService;

    @Resource
    private ActivityService activityService;

    @Resource
    private RecommendStrategy strategy;

    @Resource
    private VisitorService visitorService;

    @Resource
    private MessageSortStrategy messageSortStrategy;

    @Resource
    private UserVisitorService userVisitorService;

    @Resource
    private ReserveService reserveService;

    @Resource
    private ReserveCancelService reserveCancelService;

    @Resource
    private CommentService commentService;

    @Resource
    private MessageService messageService;

    @Resource
    private ManagerService managerService;

    @Resource
    private MobileReserveSortStrategy mobileReserveSortStrategy;

    @Resource
    private MessageIsReadService messageIsReadService;

    @Resource
    private MessageIsDeleteService messageIsDeleteService;

    @Resource
    AmqpAdmin admin;

    @Resource
    SimpleMessageListenerContainer container;

    @Autowired
    ReserveListener msgListener;

    @Resource
    RabbitTemplate rabbitTemplate;
//    private String concatHallNames(Activity activity) {
//        QueryWrapper<ActivityHall> activityHallWrapper = new QueryWrapper<>();
//        activityHallWrapper.eq("activityId", activity.getId())
//                .select("hallId");
//        List<ActivityHall> activityHallList = activityHallService.list(activityHallWrapper);
//        String hallNames = "";
//        for (ActivityHall activityHall : activityHallList) {
//            hallNames += hallService.getById(activityHall.getHallId()).getHallName();
//            hallNames += ", ";
//        }
//        return hallNames.substring(0, hallNames.length() - 2);
//
//    }


    @PostMapping("/register")
    @ApiOperation("用户注册")       // 加上参观者
    public Result<User> register(@RequestBody UserRegisterVo userVo) throws Exception {

        User user = new User();

        // 处理头像
        String imageURL = userVo.getImageURL();
        if (!imageURL.isEmpty()) {
            String image = FileUtils.convertURLToPath(imageURL);
            user.setImagePath(image);
        } else {
            user.setImagePath(User.DEFAULT_PATH);
        }

        String verificationCode = redisService.getVerificationCode(userVo.getEmail());
        if (!userVo.getJudgeCode().equals(verificationCode))
            return Result.fail("验证码错误");

        else
            redisService.removeVerificationCode(userVo.getEmail());

        if(!JudgeUtils.isValidPhoneNumber(userVo.getTelephone()))
            return Result.fail("手机号输入有误");
        if(!JudgeUtils.isValidIdCardNumber(userVo.getIdentificationNum()))
            return Result.fail("身份证号输入有误");

        Map<String, String> map = new HashMap<>();
        map.put("userPassword", userVo.getUserPassword());
        map.put("identificationNum", userVo.getIdentificationNum());

        SecretKey secretKey = AESUtils.generateSecretKey();
        Map<String, String> encrypts = AESUtils.encrypt(map, secretKey);

        BeanUtils.copyProperties(userVo, user);

        user.setUserPassword(encrypts.get("userPassword"));
        user.setIdentificationNum(encrypts.get("identificationNum"));
        user.setKeyValue(AESUtils.convertSecretKeyToString(secretKey));

        //  检验手机和邮箱是不是在表里面存在
        QueryWrapper<User> userTelephoneWrapper = new QueryWrapper<>();
        userTelephoneWrapper.eq("telephone", user.getTelephone());
        QueryWrapper<User> userEmailWrapper = new QueryWrapper<>();
        userEmailWrapper.eq("email", user.getEmail());
        if (!userService.list(userTelephoneWrapper).isEmpty())
            return Result.fail("该手机号已被注册");
        if (!userService.list(userEmailWrapper).isEmpty())
            return Result.fail("该邮箱已被注册");
        List<User> userList = userService.list();
        for (User userCheck : userList) {
            SecretKey secretKeyCheck = AESUtils.convertStringToSecretKey(userCheck.getKeyValue());
            String identificationNumCheck = AESUtils.decrypt(userCheck.getIdentificationNum(), secretKeyCheck);
            if (identificationNumCheck.equals(userVo.getIdentificationNum()))
                return Result.fail("您已注册过账号，请勿重复注册");
        }
        //

        user.setScore(User.DEFAULT_SCORE);
        userService.saveOrUpdate(user);

        QueryWrapper<Visitor> visitorWrapper = new QueryWrapper<>();
        visitorWrapper.eq("real_name", user.getRealName())
//                .eq("identification_num", user.getIdentificationNum())
                .eq("telephone", user.getTelephone());
        Visitor visitor = visitorService.getOne(visitorWrapper);
        if (visitor == null) {
            visitor = new Visitor();
            visitor.setRealName(user.getRealName());
            visitor.setTelephone(user.getTelephone());
            visitor.setIdentificationNum(user.getIdentificationNum());
            visitor.setKeyValue(user.getKeyValue());
            visitorService.saveOrUpdate(visitor);
        }

        UserVisitor userVisitor = new UserVisitor();
        userVisitor.setUserId(user.getId());
        userVisitor.setVisitorId(visitor.getId());
        userVisitor.setIsValid(UserVisitor.IS_VALID);
        userVisitorService.saveOrUpdate(userVisitor);

        return Result.success(user);
    }


    @GetMapping("/getHomeInf")
    @ApiOperation("用户登录首页获得的主要信息")
    public Result<UserHomeInfDto> getHomeInf(@RequestHeader(name = "token") String token
            , @RequestParam("userId") Integer userId) throws Exception {
        User user = userService.getById(userId);

        UserDto userDto = new UserDto();

        userDto.setImageURL(FileUtils.convertPathToURL(user.getImagePath()));
        BeanUtils.copyProperties(user, userDto);
        SecretKey secretKey = AESUtils.convertStringToSecretKey(user.getKeyValue());
        String decrypt = AESUtils.decrypt(user.getIdentificationNum(), secretKey);

        userDto.setIdentificationNum(DesensitizeUtils.desensitizeIDCard(decrypt));

        List<Stadium> stadiumAll = stadiumService.list();
        List<StadiumDto> stadiumDtoList = new ArrayList<>();
        for (Stadium stadium : stadiumAll) {
            StadiumDto stadiumDto = new StadiumDto();
            BeanUtils.copyProperties(stadium, stadiumDto);
            stadiumDto.setImageURL(FileUtils.convertPathToURL(stadium.getImagePath()));
            stadiumDtoList.add(stadiumDto);
        }

        UserHomeInfDto userHomeInfDto = new UserHomeInfDto();
        userHomeInfDto.setStadiumDtos(stadiumDtoList);
        userHomeInfDto.setUserDto(userDto);

        UserHomeInfDto userHomeInfDtoSort = strategy.sortStadium(userHomeInfDto);

        return Result.success(userHomeInfDtoSort);
    }


    @GetMapping("/getSingleStadiumInf")
    @ApiOperation("用户选择具体的场馆获得的获得活动信息")     //  这个部分和ManagerController里面的场馆管理员获得场馆内部信息很相似
    public Result<UserClickStadiumDto> getSingleStadiumInf(@RequestHeader(name = "token") String token,
                                                           @RequestParam("stadiumId") Integer stadiumId) {
        Stadium stadium = stadiumService.getById(stadiumId);
        StadiumDto stadiumDto = new StadiumDto();
        BeanUtils.copyProperties(stadium, stadiumDto);
        stadiumDto.setImageURL(FileUtils.convertPathToURL(stadium.getImagePath()));

        List<ActivityDto> activityDtoList = new ArrayList<>();
        QueryWrapper<Activity> activityWrapper = new QueryWrapper<>();
        activityWrapper.eq("stadiumId", stadium.getId())
                .eq("is_cancel", Activity.IS_NOT_CANCEL);
        List<Activity> activityList = activityService.list(activityWrapper);
        for (Activity activity : activityList) {
            if(activity.getStatus().equals(Activity.STATUS_FINISHED))
                continue;
            ActivityDto dto = new ActivityDto();
            BeanUtils.copyProperties(activity, dto);
            String activityImagePath = activity.getImagePath();
            String activityImageURL = FileUtils.convertPathToURL(activityImagePath);
            dto.setImageURL(activityImageURL);
            dto.setStadiumName(stadium.getStadiumName());
            // 将所涉及展厅进行拼接
            String hallNames = activityService.concatHallNames(activity);
            dto.setHallNames(hallNames);
            activityDtoList.add(dto);
        }

        UserClickStadiumDto userClickStadiumDto = new UserClickStadiumDto();
        userClickStadiumDto.setStadiumDto(stadiumDto);
        userClickStadiumDto.setActivityDtos(activityDtoList);

        UserClickStadiumDto userClickStadiumDtoSort = strategy.sortActivity(userClickStadiumDto, stadiumId);
        return Result.success(userClickStadiumDtoSort);
    }

    @GetMapping("/mobileGetSingleStadiumInf")
    @ApiOperation("移动端用户选择具体场馆获得的活动信息")
    public Result<MobileUserClickStadiumDto> mobileGetSingleStadiumInf(@RequestHeader(name = "token") String token,
                                                                       @RequestParam("stadiumId") Integer stadiumId){


        Stadium stadium = stadiumService.getById(stadiumId);
        if(stadium == null)
            return Result.fail("请输入正确的场馆编号");
        StadiumDto stadiumDto = new StadiumDto();
        BeanUtils.copyProperties(stadium, stadiumDto);
        stadiumDto.setImageURL(FileUtils.convertPathToURL(stadium.getImagePath()));

        List<ActivityDto> activityDtoList = new ArrayList<>();
        QueryWrapper<Activity> activityWrapper = new QueryWrapper<>();
        activityWrapper.eq("stadiumId", stadium.getId())
                .eq("is_cancel", Activity.IS_NOT_CANCEL);
        List<Activity> activityList = activityService.list(activityWrapper);
        for (Activity activity : activityList) {
            if(activity.getStatus().equals(Activity.STATUS_FINISHED))
                continue;
            ActivityDto dto = new ActivityDto();
            BeanUtils.copyProperties(activity, dto);
            String activityImagePath = activity.getImagePath();
            String activityImageURL = FileUtils.convertPathToURL(activityImagePath);
            dto.setImageURL(activityImageURL);
            dto.setStadiumName(stadium.getStadiumName());
            // 将所涉及展厅进行拼接
            String hallNames = activityService.concatHallNames(activity);
            dto.setHallNames(hallNames);
            activityDtoList.add(dto);
        }

        MobileUserClickStadiumDto clickStadiumDto = new MobileUserClickStadiumDto();
        clickStadiumDto.setStadiumDto(stadiumDto);
        List<List<ActivityDto>> activityDtos = strategy.mobileSortActivity(activityDtoList, stadiumId);
        clickStadiumDto.setActivityDtos(activityDtos);


        return Result.success(clickStadiumDto);

    }


    @PostMapping("/addVisitor")
    @ApiOperation("新增访问者信息")
    public Result<String> addVisitor(@RequestHeader(name = "token") String token,
                                     @RequestBody VisitorAddVo visitorVo) throws Exception {

        QueryWrapper<Visitor> visitorWrapper = new QueryWrapper<>();
        visitorWrapper.eq("real_name", visitorVo.getRealName())
//                .eq("identification_num", visitorVo.getIdentificationNum())
                .eq("telephone", visitorVo.getTelephone());
        Visitor visitor = visitorService.getOne(visitorWrapper);
        if(!JudgeUtils.isValidIdCardNumber(visitorVo.getIdentificationNum()))
            return Result.fail("输入身份证号有误");
        if(!JudgeUtils.isValidPhoneNumber(visitorVo.getTelephone()))
            return Result.fail("输入手机号有误");

        User user = userService.getById(visitorVo.getUserId());
        SecretKey secretKeyCheck = AESUtils.convertStringToSecretKey(user.getKeyValue());
        String identification = AESUtils.decrypt(user.getIdentificationNum(), secretKeyCheck);
        if(identification.equals(visitorVo.getIdentificationNum()))
            return Result.fail("请勿输入自己的身份证号进行信息绑定");
        if(user.getTelephone().equals(visitorVo.getTelephone()))
            return Result.fail("请勿输入自己的手机号进行信息绑定");

        if (visitor == null) {
            visitor = new Visitor();
            visitor.setRealName(visitorVo.getRealName());
            visitor.setTelephone(visitorVo.getTelephone());

            SecretKey secretKey = AESUtils.generateSecretKey();
            String encryptNum = AESUtils.encrypt(visitorVo.getIdentificationNum(), secretKey);
            visitor.setIdentificationNum(encryptNum);
            visitor.setKeyValue(AESUtils.convertSecretKeyToString(secretKey));

            visitorService.saveOrUpdate(visitor);

        }

        QueryWrapper<UserVisitor> userVisitorWrapper = new QueryWrapper<>();
        userVisitorWrapper.eq("userId", visitorVo.getUserId())
                .eq("visitorId", visitor.getId());
        UserVisitor userVisitor = userVisitorService.getOne(userVisitorWrapper);
        if (userVisitor != null) {
            userVisitor.setIsValid(UserVisitor.IS_VALID);
            userVisitorService.saveOrUpdate(userVisitor);
        } else {
            userVisitor = new UserVisitor();
            userVisitor.setUserId(visitorVo.getUserId());
            userVisitor.setVisitorId(visitor.getId());
            userVisitor.setIsValid(UserVisitor.IS_VALID);
            userVisitorService.saveOrUpdate(userVisitor);
        }

        return Result.success("新增访问者信息成功");
    }

    /*
     *
     * 有一个查询访问者信息的接口没写
     *
     * */

    @GetMapping("/getAllVisitors")
    @ApiOperation("获得用户的所有绑定的访问者信息")
    public Result<List<VisitorDto>> getAllVisitors(@RequestHeader(name = "token") String token,
                                                   @RequestParam("userId") Integer userId) throws Exception {

        QueryWrapper<UserVisitor> userVisitorWrapper = new QueryWrapper<>();
        userVisitorWrapper.eq("userId", userId)
                .eq("is_valid", UserVisitor.IS_VALID)
                .select("visitorId");
        List<UserVisitor> userVisitorList = userVisitorService.list(userVisitorWrapper);
        List<VisitorDto> visitors = new ArrayList<>();
        for (UserVisitor userVisitor : userVisitorList) {
            Visitor visitor = visitorService.getById(userVisitor.getVisitorId());
            SecretKey secretKey = AESUtils.convertStringToSecretKey(visitor.getKeyValue());
            String identificationNum = AESUtils.decrypt(visitor.getIdentificationNum(), secretKey);
            VisitorDto visitorDto = new VisitorDto();
            BeanUtils.copyProperties(visitor, visitorDto);
            visitorDto.setIdentificationNum(identificationNum);
            visitors.add(visitorDto);
        }
        return Result.success(visitors);
    }

    @GetMapping("/deleteVisitor")
    @ApiOperation("用户删除绑定的访问者")     // 不可以删除自己  这个逻辑要加上
    public Result<String> deleteVisitor(@RequestHeader(name = "token") String token,
                                        @RequestParam("userId") Integer userId, @RequestParam("visitorId") Integer visitorId) {

        QueryWrapper<UserVisitor> userVisitorWrapper = new QueryWrapper<>();
        userVisitorWrapper.eq("userId", userId)
                .eq("visitorId", visitorId)
                .eq("is_valid", UserVisitor.IS_VALID);
        UserVisitor userVisitor = userVisitorService.getOne(userVisitorWrapper);
        if (userVisitor != null) {
            //  删除的不能是自己

            String userTelephone = userService.getById(userId).getTelephone();
            String visitorTelephone = visitorService.getById(visitorId).getTelephone();
            if (userTelephone.equals(visitorTelephone))
                return Result.fail("访问者不可以删除自身，删除异常");
            //
            userVisitor.setIsValid(UserVisitor.IS_NOT_VALID);
            userVisitorService.saveOrUpdate(userVisitor);
            return Result.success("删除成功");
        }
        return Result.fail("删除异常");
    }

    @PostMapping("/reserve")
    @ApiOperation("用户使用参观者进行预约")        //  这个地方要判断是否重复预约
    public Result<String> reserve(@RequestHeader(name = "token") String token,
                                  @RequestBody ReserveAddVo reserveVo) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("ReserveAddVo", reserveVo);

        MqUtils mqUtils = new MqUtils(admin, container, msgListener, rabbitTemplate);
        String qName = reserveVo.getActivityId().toString() + ".reserve";
        System.out.println(qName);
        mqUtils.addListener(qName);
        mqUtils.sendMsg(qName, data);
        while (!msgListener.finish) {
            System.out.println(msgListener.finish);
        }
        msgListener.finish = false;
        return msgListener.result;

//        // 在这里前端传过来的是visitor的id，在这里要处理成user_visitor的id
//        List<Reserve> reserves = new ArrayList<>();
//        List<Integer> visitorIds = reserveVo.getVisitorIds();
//        Date date = new Date();
//        for (Integer visitorId : visitorIds) {
//            Reserve reserve = new Reserve();
//            BeanUtils.copyProperties(reserveVo, reserve);
//            reserve.setOperateTime(date);
//            reserve.setIsReserve(Reserve.IS_RESERVE);                //  预约
//            reserve.setReserveStatus(Reserve.STATUS_PROCESSING);      //  默认活动未开始
//            QueryWrapper<UserVisitor> wrapper = new QueryWrapper<>();
//            wrapper.eq("userId", reserveVo.getUserId()).eq("visitorId", visitorId);
//            Integer userVisitorId = userVisitorService.getOne(wrapper, false).getId();
//            reserve.setUserVisitorId(userVisitorId);
//            String status = activityService.getById(reserve.getActivityId()).getStatus();
////            if (status.equals(Activity.STATUS_PROCESSING))
////                return Result.fail("活动进行中，不可进行预约");
//
//            if (status.equals(Activity.STATUS_FINISHED))
//                return Result.fail("活动已结束，不可进行预约");
//            /*
//             *
//             * 判断逻辑：
//             *        看reserve表中有没有userVisitorId和activityId都有的记录
//             *        且is_reserve为1的数据和is_reserve为0的数据个数不相等
//             *        有这条数据就直接中止插入，返回具体成员的信息
//             * */
//            QueryWrapper<Reserve> checkWrapperReserve = new QueryWrapper<>();
//            checkWrapperReserve.eq("activityId", reserveVo.getActivityId()).eq("userVisitorId", userVisitorId);
//            List<Reserve> checkReserves = reserveService.list(checkWrapperReserve);
//            int reserveNum = 0;
//            for (Reserve checkReserve : checkReserves) {
//                if (checkReserve.getIsReserve().equals(Reserve.IS_RESERVE))
//                    reserveNum++;
//            }
//            if (!(checkReserves.size() == (2 * reserveNum))) {
//                String realName = visitorService.getById(visitorId).getRealName();
//                return Result.fail(realName + "已预约过该活动");
//            }
//            reserves.add(reserve);
//        }
//        Activity activity = activityService.getById(reserveVo.getActivityId());
//        if (activity.getAccessNum() >= reserves.size()) {
//            activity.setAccessNum(activity.getAccessNum() - reserves.size());
//            activityService.saveOrUpdate(activity);
//        } else
//            return Result.fail("当前预约人数不足以预约" + reserves.size() + "人");
//        reserveService.saveBatch(reserves);
//
//        return Result.success("预约成功");
    }

    @GetMapping("/logicRemoveReserve")
    @ApiOperation("用户逻辑删除预约记录")
    public Result<String> logicRemoveReserve(@RequestHeader(name = "token") String token,
                                             @RequestParam("reserveId") Integer reserveId, @RequestParam("userId") Integer userId) {

        QueryWrapper<ReserveCancel> wrapper = new QueryWrapper<>();
        wrapper.eq("reserve_id", reserveId)
                .eq("user_id", userId);

        Reserve reserve = reserveService.getById(reserveId);
        if(reserve == null)
            return Result.fail("请输入正确的预约编号");
        Integer visitorId = userVisitorService.getById(reserve.getUserVisitorId()).getVisitorId();
        if(!visitorService.getUserId(visitorId).equals(userId))
            return Result.fail("您不具备删除该预约的权力");
        ReserveCancel search = reserveCancelService.getOne(wrapper);
        if (search != null) {
            return Result.fail("请勿重复删除");
        }

        ReserveCancel reserveCancel = new ReserveCancel();
        reserveCancel.setReserveId(reserveId);
        reserveCancel.setUserId(userId);

        reserveCancelService.saveOrUpdate(reserveCancel);
        return Result.success("删除成功");
    }

    @GetMapping("/revokeReserveSingle")
    @ApiOperation("用户在活动开始之前对预约进行撤销")
    public Result<String> revokeReserveSingle(@RequestHeader(name = "token") String token,
                                              @RequestParam("reserveId") Integer reserveId) {

        /*
         *  怎么撤销
         *     对已有的预约记录进行点击撤销
         *  用户查到的信息应该是什么样的
         *     所有预约记录 + 是否取消（状态）
         *     所有取消记录
         * */

        //  重复撤销预约 ？？？
        //  哪些状态能取消预约

        Reserve reserveOrigional = reserveService.getById(reserveId);
        // 先找到visitor对应的user的id

        Integer visitorId = userVisitorService.getById(reserveOrigional.getUserVisitorId()).getVisitorId();
        String telephone = visitorService.getById(visitorId).getTelephone();
        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.eq("telephone", telephone);
        Integer userId = userService.getOne(userWrapper).getId();

        if (!reserveService.haveReservedActivity(userId, reserveOrigional.getActivityId()))
            return Result.fail("您还未预约过此活动");
        if (!reserveOrigional.getReserveStatus().equals(Reserve.STATUS_PROCESSING))
            return Result.fail("活动已结束");

        reserveOrigional.setReserveStatus(Reserve.STATUS_CANCEL);

        Activity activity = activityService.getById(reserveOrigional.getActivityId());
        activity.setAccessNum(activity.getAccessNum() + 1);
        activityService.saveOrUpdate(activity);


        Reserve reserveRevoke = new Reserve();
        BeanUtils.copyProperties(reserveOrigional, reserveRevoke);
        reserveRevoke.setOperateTime(new Date());
        reserveRevoke.setIsReserve(Reserve.IS_NOT_RESERVE);
        reserveRevoke.setReserveStatus(Reserve.STATUS_CANCEL);
        reserveRevoke.setId(null);

        long time = new Date().getTime();
        long timeBegin = reserveOrigional.getReserveBegTime().getTime();

        long minutes = (timeBegin - time) / 1000 / 60;
        if(minutes < 30) {
            User user = userService.getById(userId);
            user.setScore(Math.max(user.getScore() - 1, 0));
            userService.saveOrUpdate(user);
            String stadiumName = stadiumService.getById(activity.getStadiumId()).getStadiumName();
            String content = messageService.overTimeRevoke(user.getRealName(), activity.getActivityName(), stadiumName, user.getScore());
            Message message = new Message();
            message.setIsRemove(Message.IS_NOT_REMOVE);
            message.setContent(content);
            message.setOperateTime(new Date());
            message.setInvokeId(Message.SYSTEM_INVOKED_ID);
            message.setAcceptId(userId);
            messageService.save(message);
        }
        reserveService.saveOrUpdate(reserveRevoke);
        reserveService.saveOrUpdate(reserveOrigional);


        return Result.success("撤销成功");
    }


    // 判断该预约是否被取消
//    private Boolean isInvoked(Reserve reserveSearch, List<Reserve> checkingReserves){
////        QueryWrapper<Reserve> wrapper = new QueryWrapper<>();
////        wrapper.eq("activityId", reserveSearch.getActivityId())
////                .eq("userVisitorId", reserveSearch.getUserVisitorId())
////                .select("operate_time");
////        List<Reserve> checkReserves = reserveService.list(wrapper);
//        if(checkingReserves == null)
//            return false;
//        for(Reserve reserve : checkingReserves){
//            if(reserve.getActivityId().equals(reserveSearch.getActivityId())
//            && reserve.getUserVisitorId().equals(reserveSearch.getUserVisitorId())
//            && reserve.getOperateTime().after(reserveSearch.getOperateTime())){
//                return true;
//            }
//        }
//        return false;
//    }


    //    private ActivityDto getDtoByActivity(Integer activityId){
//        Activity activity = activityService.getById(activityId);
//        ActivityDto activityDto = new ActivityDto();
//        BeanUtils.copyProperties(activity, activityDto);
//        activityDto.setImageURL(FileUtils.convertPathToURL(activity.getImagePath()));
//        activityDto.setHallNames(concatHallNames(activity));
//        return activityDto;
//    }
    @GetMapping("/getAllReserves")
    @ApiOperation("用户获得所有预约信息")     // 没有加上逻辑撤销的部分
    public Result<ReserveSearchDto> getAllReserves(@RequestHeader(name = "token") String token,
                                                   @RequestParam("userId") Integer userId) {
        User user = userService.getById(userId);
        if(user == null)
            return Result.fail("请输入正确的用户id");
        String telephone = user.getTelephone();

        QueryWrapper<Visitor> visitorWrapper = new QueryWrapper<>();
        visitorWrapper.eq("telephone", telephone).select("id");
        Integer visitorId = visitorService.getOne(visitorWrapper).getId();  // 获得visitorId

        QueryWrapper<UserVisitor> userVisitorWrapper = new QueryWrapper<>();
        userVisitorWrapper.eq("visitorId", visitorId).select("id");
        List<UserVisitor> userVisitorList = userVisitorService.list(userVisitorWrapper);

        List<ReserveDto> reserveDtos = new ArrayList<>();
        List<ReserveNotDto> reserveNotDtos = new ArrayList<>();

        for (UserVisitor userVisitor : userVisitorList) {

            Integer userVisitorId = userVisitor.getId();
            QueryWrapper<Reserve> reserveWrapper = new QueryWrapper<>();
            reserveWrapper.eq("userVisitorId", userVisitorId);
            // 查询一个uservisitor对应的id的所有预约和取消预约记录
            List<Reserve> reserveList = reserveService.list(reserveWrapper);

            for (Reserve reserveSearch : reserveList) {
                Integer isReserve = reserveSearch.getIsReserve();

                QueryWrapper<ReserveCancel> reserveCancelWrapper = new QueryWrapper<>();
                reserveCancelWrapper.eq("reserve_id", reserveSearch.getId());
                if (reserveCancelService.getOne(reserveCancelWrapper, false) != null)
                    continue;

                //   在这里加上对reserve被取消预约状态的改变
//                if(reserveService.isInvoked(reserveSearch, reserveList))
                if (isReserve.equals(Reserve.IS_NOT_RESERVE)) {    // 为取消预约信息
                    ReserveNotDto reserveNotDto = new ReserveNotDto();
                    BeanUtils.copyProperties(reserveSearch, reserveNotDto);
                    ActivityDto activityDto = activityService.getDtoByActivity(reserveSearch.getActivityId());
                    reserveNotDto.setActivity(activityDto);
                    reserveNotDtos.add(reserveNotDto);
                } else {
                    //  判断逻辑
                    //  使用活动id查询，看是否有operate_time 更大的，有点话就设置为已取消预约
                    ReserveDto reserveDto = new ReserveDto();
                    BeanUtils.copyProperties(reserveSearch, reserveDto);

                    if (reserveSearch.getReserveStatus().equals(Reserve.STATUS_CANCEL)) {  //被取消
                        reserveDto.setReserveStatus(Reserve.STATUS_CANCEL);
                    }
                    ActivityDto activityDto = activityService.getDtoByActivity(reserveSearch.getActivityId());
                    reserveDto.setActivity(activityDto);
                    reserveDtos.add(reserveDto);
                }
            }
        }

        /*
         *   还要对用户逻辑撤销进行处理和检查
         *
         * */

        ReserveSearchDto reserveSearchDto = new ReserveSearchDto();
        reserveSearchDto.setReserveDtos(reserveSortStrategy.sortReserveDtos(reserveDtos));
        reserveSearchDto.setReserveNotDtos(reserveSortStrategy.sortReserveNotDtos(reserveNotDtos));
        return Result.success(reserveSearchDto);
    }

    @GetMapping("/mobilegetAllReserves")
    @ApiOperation("移动端获取所有预约信息")
    public Result<List<List<ReserveDto>>> mobilegetAllReserves(@RequestHeader(name = "token") String token,
                                                               @RequestParam("userId") Integer userId) {
        User user = userService.getById(userId);
        if(user == null)
            return Result.fail("请输入正确的用户id");
        String telephone = user.getTelephone();

        QueryWrapper<Visitor> visitorWrapper = new QueryWrapper<>();
        visitorWrapper.eq("telephone", telephone).select("id");
        Integer visitorId = visitorService.getOne(visitorWrapper).getId();  // 获得visitorId

        QueryWrapper<UserVisitor> userVisitorWrapper = new QueryWrapper<>();
        userVisitorWrapper.eq("visitorId", visitorId).select("id");
        List<UserVisitor> userVisitorList = userVisitorService.list(userVisitorWrapper);

        List<ReserveDto> reserveDtos = new ArrayList<>();
        for (UserVisitor userVisitor : userVisitorList) {

            Integer userVisitorId = userVisitor.getId();
            QueryWrapper<Reserve> reserveWrapper = new QueryWrapper<>();
            reserveWrapper.eq("userVisitorId", userVisitorId);
            // 查询一个uservisitor对应的id的所有预约和取消预约记录
            List<Reserve> reserveList = reserveService.list(reserveWrapper);

            for (Reserve reserveSearch : reserveList) {
                Integer isReserve = reserveSearch.getIsReserve();

                QueryWrapper<ReserveCancel> reserveCancelWrapper = new QueryWrapper<>();
                reserveCancelWrapper.eq("reserve_id", reserveSearch.getId());
                if (reserveCancelService.getOne(reserveCancelWrapper) != null)
                    continue;

                //   在这里加上对reserve被取消预约状态的改变
//                if(reserveService.isInvoked(reserveSearch, reserveList))
                if (!isReserve.equals(Reserve.IS_NOT_RESERVE)) {   // 为取消预约信息

                    //  判断逻辑
                    //  使用活动id查询，看是否有operate_time 更大的，有点话就设置为已取消预约
                    ReserveDto reserveDto = new ReserveDto();
                    BeanUtils.copyProperties(reserveSearch, reserveDto);

                    if (reserveSearch.getReserveStatus().equals(Reserve.STATUS_CANCEL)) {  //被取消
                        reserveDto.setReserveStatus(Reserve.STATUS_CANCEL);
                    }
                    ActivityDto activityDto = activityService.getDtoByActivity(reserveSearch.getActivityId());
                    reserveDto.setActivity(activityDto);


                    QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("activityId", activityDto.getId())
                                    .eq("userId", userId);
                    if(commentService.getOne(queryWrapper, false) == null)
                        reserveDto.setIsComment("未评论");
                    else
                        reserveDto.setIsComment("已评论");
                    reserveDtos.add(reserveDto);
                }
            }
        }

        /*
         *   还要对用户逻辑撤销进行处理和检查
         *
         * */

        return Result.success(mobileReserveSortStrategy.sortReserveDtos(reserveDtos));

    }


    @PostMapping("/commentByUser")
    @ApiOperation("用户评价已经参加的活动")
    public Result<String> commentByUser(@RequestHeader(name = "token") String token,
                                        @RequestBody CommentVo commentVo) {
        /*
             这里暂时忽略活动已被取消的问题
        * */
        Reserve reserve = reserveService.getById(commentVo.getReserveId());
        if (reserve.getReserveStatus().equals(Reserve.STATUS_CANCEL))
            return Result.fail("您已取消该预约");

        if (!(reserve.getReserveStatus().equals(Reserve.STATUS_PASS_CHECK)))
            return Result.fail("您并未完成活动检票，无法进行活动评价");

        Activity activity = activityService.getById(reserve.getActivityId());
        if ((activity.getStatus().equals(Activity.STATUS_WAITING)))
            return Result.fail("该活动未开始");

//        if (reserveService.haveReservedActivity(commentVo.getUserId(), reserve.getActivityId()))
//            return Result.fail("您未参加过此活动");
        if (reserve.getReserveStatus().equals(Reserve.STATUS_CANCEL))
            return Result.fail("此条记录为取消预约");

        QueryWrapper<Comment> commentWrapper = new QueryWrapper<>();
        commentWrapper.eq("activityId", reserve.getActivityId())
                .eq("userId", commentVo.getUserId()).select("id");
        if (!commentService.list(commentWrapper).isEmpty())
            return Result.fail("您已评价过此活动");

        // 先判断这个评价是否被取消，且是否已完成预约，活动状态是否是已结束
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentVo, comment);
        comment.setActivityId(reserve.getActivityId());
        Date commentTime = new Date();
        comment.setCommentTime(commentTime);
        comment.setIsComment(Comment.IS_COMMENT);
        commentService.saveOrUpdate(comment);
        return Result.success("用户成功完成评价");
    }


    @GetMapping("/getAllMessage")
    @ApiOperation("获得所有消息")
    public Result<List<MessageDto>> getAllMessage(@RequestHeader(name = "token") String token,
                                                  @RequestParam("userId") Integer userId) {
        User user = userService.getById(userId);
        if(user == null)
            return Result.fail("请输入正确的用户id");
        QueryWrapper<Message> sendToUserWrapper = new QueryWrapper<>();
        sendToUserWrapper.eq("acceptId", userId)
                .eq("is_remove", Message.IS_NOT_REMOVE);

        QueryWrapper<Message> sendToAllWrapper = new QueryWrapper<>();
        sendToAllWrapper.eq("acceptId", Message.ALL_ACCEPTED_ID)
                .eq("is_remove", Message.IS_NOT_REMOVE);

        List<MessageDto> messageDtoList = new ArrayList<>();

        List<Message> messagesToUser = messageService.list(sendToUserWrapper);
        List<Message> messagesToAll = messageService.list(sendToAllWrapper);

        getMessageList(messageDtoList, messagesToUser, userId);

        getMessageList(messageDtoList, messagesToAll, userId);

        // 接下来做已读和未读的排序
        List<MessageDto> messageDtos = messageSortStrategy.sortMessageDtos(messageDtoList);
        return Result.success(messageSortStrategy.sortTimeAndIsRead(messageDtos));

    }

    private void getMessageList(List<MessageDto> messageDtoList, List<Message> messagesToAll, Integer userId) {
        for (Message message : messagesToAll) {

            QueryWrapper<MessageIsDelete> wrapperDelete = new QueryWrapper<>();
            wrapperDelete.eq("messageId", message.getId())
                    .eq("userId", userId);
            MessageIsDelete delete = messageIsDeleteService.getOne(wrapperDelete, false);
            if(delete != null)
                continue;
            MessageDto messageDto = new MessageDto();
            BeanUtils.copyProperties(message, messageDto);
            if (message.getInvokeId().equals(Message.SYSTEM_INVOKED_ID))
                messageDto.setInvokeName("系统发布");
            else {
                Integer managerId = managerService.getById(message.getInvokeId()).getId();
                QueryWrapper<Stadium> stadiumWrapper = new QueryWrapper<>();
                stadiumWrapper.eq("managerId", managerId);
                Stadium stadium = stadiumService.getOne(stadiumWrapper);
                if (stadium == null)
                    messageDto.setInvokeName("系统发布");
                else
                    messageDto.setInvokeName(stadium.getStadiumName());
            }


            Integer messageId = message.getId();
            QueryWrapper<MessageIsRead> wrapper = new QueryWrapper<>();
            wrapper.eq("userId", userId)
                            .eq("messageId", messageId);
            MessageIsRead detail = messageIsReadService.getOne(wrapper, false);
            if(detail == null)
                messageDto.setIsRead("未读");
            else
                messageDto.setIsRead("已读");
            messageDtoList.add(messageDto);
        }
    }

    @GetMapping("/generateQRCode")
    @ApiOperation("生成二维码")
    public Result<List<NameUrl>> generateQRCode(@RequestHeader(name = "token") String token,
                                               @RequestParam("reserveId") Integer reserveId) throws Exception {

        Reserve reserve = reserveService.getById(reserveId);

//        if (reserve.getIsReserve().equals(Reserve.IS_NOT_RESERVE))
//            return Result.fail("该预约已被取消");

        if (reserve.getReserveStatus().equals(Reserve.STATUS_PASS_CHECK))
            return Result.fail("您已检票，请勿重复生成二维码");
        if (reserve.getReserveStatus().equals(Reserve.STATUS_NOT_CHECK))
            return Result.fail("该预约已逾期或活动已结束");
        if (reserve.getReserveStatus().equals(Reserve.STATUS_CANCEL))
            return Result.fail("该预约已取消");
        Activity activity = activityService.getById(reserve.getActivityId());
        String stadiumName = stadiumService.getById(activity.getStadiumId()).getStadiumName();
        Integer userVisitorId = reserve.getUserVisitorId();

        UserVisitor userVisitor = userVisitorService.getById(userVisitorId);

        Integer userId = userVisitor.getUserId();           // userId为预约发起人的用户id
        Integer visitorId = userVisitor.getVisitorId();     // visitorId为被预约人的访问者id

        User user = userService.getById(userId);

        Visitor visitor = visitorService.getById(visitorId);

        List<NameUrl> ERCodeURLs = new ArrayList<>();

        if (user.getTelephone().equals(visitor.getTelephone())) {     // 发现是自己发起的预约
            //  接下来找出所有由自己发出的与预约时间相同的活动的记录
            QueryWrapper<Reserve> reserveWrapper = new QueryWrapper<>();
            reserveWrapper.eq("activityId", reserve.getActivityId())
                    .eq("operate_time", reserve.getOperateTime());
            List<Reserve> reserveList = reserveService.list(reserveWrapper);    //  获取同一时间预约同意活动的所有记录
            List<VisitorDto> visitorDtos = new ArrayList<>();

            //  检车这里面的uservisitorID是不是以userId为开头的
            for (Reserve reserveCheck : reserveList) {
                UserVisitor userVisitorCheck = userVisitorService.getById(reserveCheck.getUserVisitorId());
                if (userVisitorCheck.getUserId().equals(userId)) {    // 如果是本人发起的预约
                    Visitor visitorById = visitorService.getById(userVisitorCheck.getVisitorId());
                    VisitorDto visitorDto = new VisitorDto();
                    BeanUtils.copyProperties(visitorById, visitorDto);
                    SecretKey secretKey = AESUtils.convertStringToSecretKey(visitorById.getKeyValue());
                    String identificationNum = AESUtils.decrypt(visitorDto.getIdentificationNum(), secretKey);

//                    visitorDto.setTelephone(DesensitizeUtils.desensitizePhoneNumber(visitorDto.getTelephone()));
//                    visitorDto.setRealName(DesensitizeUtils.desensitizeName(visitorDto.getRealName()));
                    visitorDto.setIdentificationNum(DesensitizeUtils.desensitizeIDCard(identificationNum));

                    visitorDtos.add(visitorDto);
                }
            }

            for (VisitorDto visitorDto : visitorDtos) {


                String filePath = QRCodeGenerateUtils.generateQRCode(visitorDto, activity, stadiumName);
                Map<String, String> map = new HashMap<>();
                map.put(visitorDto.getRealName(), FileUtils.convertPathToURL(filePath));
                NameUrl nameUrl = new NameUrl();
                nameUrl.setUrl( FileUtils.convertPathToURL(filePath));
                nameUrl.setRealName(visitorDto.getRealName());
                ERCodeURLs.add(nameUrl);
            }

        } else {           //  被别人进行预约,一定只展示自己的二维码信息
            VisitorDto visitorDto = new VisitorDto();
            BeanUtils.copyProperties(visitor, visitorDto);
            SecretKey secretKey = AESUtils.convertStringToSecretKey(visitor.getKeyValue());
            String identificationNum = AESUtils.decrypt(visitorDto.getIdentificationNum(), secretKey);
            visitorDto.setTelephone(DesensitizeUtils.desensitizePhoneNumber(visitorDto.getTelephone()));
//            visitorDto.setRealName(DesensitizeUtils.desensitizeName(visitorDto.getRealName()));
            visitorDto.setIdentificationNum(DesensitizeUtils.desensitizeIDCard(identificationNum));
            System.out.println(visitorDto);
            String filePath = QRCodeGenerateUtils.generateQRCode(visitorDto, activity, stadiumName);
            Map<String, String> map = new HashMap<>();
            map.put(visitor.getRealName(), FileUtils.convertPathToURL(filePath));

            NameUrl nameUrl = new NameUrl();
            nameUrl.setUrl( FileUtils.convertPathToURL(filePath));
            nameUrl.setRealName(visitorDto.getRealName());
            ERCodeURLs.add(nameUrl);
        }
        return Result.success(ERCodeURLs);
    }


//    @GetMapping("/getReserveDetails")
//    @ApiOperation("lsx-获取具体预约信息")
//    public Result<ReserveDetailsDto> getReserveDetails(@RequestParam("reserveId") Integer reserveId) throws Exception {
//        ReserveDetailsDto reserveDetailsDto = new ReserveDetailsDto();
//
//        Reserve reserve = reserveService.getById(reserveId);
//
////        if (reserve.getIsReserve().equals(Reserve.IS_NOT_RESERVE))
////            return Result.fail("该预约已被取消");
//
//        if(reserve.getReserveStatus().equals(Reserve.STATUS_PASS_CHECK))
//            return Result.fail("您已检票，请勿重复生成二维码");
//        if(reserve.getReserveStatus().equals(Reserve.STATUS_NOT_CHECK))
//            return Result.fail("该预约已逾期或活动已结束");
//        if(reserve.getReserveStatus().equals(Reserve.STATUS_CANCEL))
//            return Result.fail("该预约已取消");
//        Activity activity = activityService.getById(reserve.getActivityId());
//        String stadiumName = stadiumService.getById(activity.getStadiumId()).getStadiumName();
//        Integer userVisitorId = reserve.getUserVisitorId();
//
//        UserVisitor userVisitor = userVisitorService.getById(userVisitorId);
//
//        Integer userId = userVisitor.getUserId();           // userId为预约发起人的用户id
//        Integer visitorId = userVisitor.getVisitorId();     // visitorId为被预约人的访问者id
//
//        User user = userService.getById(userId);
//
//        Visitor visitor = visitorService.getById(visitorId);
//
//        List<String> ERCodeURLs = new ArrayList<>();
//
//        if (user.getTelephone().equals(visitor.getTelephone())) {     // 发现是自己发起的预约
//            //  接下来找出所有由自己发出的与预约时间相同的活动的记录
//            QueryWrapper<Reserve> reserveWrapper = new QueryWrapper<>();
//            reserveWrapper.eq("activityId", reserve.getActivityId())
//                    .eq("operate_time", reserve.getOperateTime());
//            List<Reserve> reserveList = reserveService.list(reserveWrapper);    //  获取同一时间预约同意活动的所有记录
//            List<VisitorDto> visitorDtos = new ArrayList<>();
//
//            //  检车这里面的uservisitorID是不是以userId为开头的
//            for (Reserve reserveCheck : reserveList) {
//                UserVisitor userVisitorCheck = userVisitorService.getById(reserveCheck.getUserVisitorId());
//                if (userVisitorCheck.getUserId().equals(userId)) {    // 如果是本人发起的预约
//                    Visitor visitorById = visitorService.getById(userVisitorCheck.getVisitorId());
//                    VisitorDto visitorDto = new VisitorDto();
//                    BeanUtils.copyProperties(visitorById, visitorDto);
//                    SecretKey secretKey = AESUtils.convertStringToSecretKey(visitorById.getKeyValue());
//                    String identificationNum = AESUtils.decrypt(visitorDto.getIdentificationNum(), secretKey);
//
////                    visitorDto.setTelephone(DesensitizeUtils.desensitizePhoneNumber(visitorDto.getTelephone()));
////                    visitorDto.setRealName(DesensitizeUtils.desensitizeName(visitorDto.getRealName()));
//                    visitorDto.setIdentificationNum(DesensitizeUtils.desensitizeIDCard(identificationNum));
//
//                    visitorDtos.add(visitorDto);
//                }
//            }
//            List<VisitorInfDto> visitorInfDtos = new ArrayList<>();
//
//            for(VisitorDto visitorDto : visitorDtos){
//                VisitorInfDto visitorInfDto = new VisitorInfDto();
//                BeanUtils.copyProperties(visitorDto, visitorInfDto);
//                visitorInfDtos.add(visitorInfDto);
//            }
//
//            reserveDetailsDto.setVisitors(visitorInfDtos);
//
//            for (VisitorDto visitorDto : visitorDtos) {
//
//
//
//                String filePath = QRCodeGenerateUtils.generateQRCode(visitorDto, activity, stadiumName);
//                Map<String, String> map = new HashMap<>();
//                map.put(visitorDto.getRealName(), FileUtils.convertPathToURL(filePath));
//
//                ERCodeURLs.add(map.toString().replace('=', '：'));
//            }
//
//        } else {           //  被别人进行预约,一定只展示自己的二维码信息
//            VisitorDto visitorDto = new VisitorDto();
//            BeanUtils.copyProperties(visitor, visitorDto);
//            SecretKey secretKey = AESUtils.convertStringToSecretKey(visitor.getKeyValue());
//            String identificationNum = AESUtils.decrypt(visitorDto.getIdentificationNum(), secretKey);
//            visitorDto.setTelephone(DesensitizeUtils.desensitizePhoneNumber(visitorDto.getTelephone()));
////            visitorDto.setRealName(DesensitizeUtils.desensitizeName(visitorDto.getRealName()));
//            visitorDto.setIdentificationNum(DesensitizeUtils.desensitizeIDCard(identificationNum));
//            System.out.println(visitorDto);
//
//            VisitorInfDto visitorInfDto = new VisitorInfDto();
//            BeanUtils.copyProperties(visitorDto, visitorInfDto);
//            List<VisitorInfDto> visitorInfDtoList = new ArrayList<>();
//            visitorInfDtoList.add(visitorInfDto);
//            reserveDetailsDto.setVisitors(visitorInfDtoList);
//
//            String filePath = QRCodeGenerateUtils.generateQRCode(visitorDto, activity, stadiumName);
//            Map<String, String> map = new HashMap<>();
//            map.put(visitor.getRealName(), FileUtils.convertPathToURL(filePath));
//
//            ERCodeURLs.add(map.toString().replace('=', '：'));
//        }
//        reserveDetailsDto.setURLs(ERCodeURLs);
//        return Result.success(reserveDetailsDto);
//    }


    @GetMapping("/searchActivity")
    @ApiOperation("点击具体活动获得的活动信息")
    public Result<ActivityDto> searchActivity(@RequestHeader(name = "token") String token,
                                              @RequestParam("activityId") Integer activityId) {
        Activity activity = activityService.getById(activityId);
        if(activity == null)
            return Result.fail("请输入正确的活动id");
        if(activity.getIsCancel().equals(Activity.IS_CANCEL))
            return Result.fail("该活动已被取消");
        ActivityDto activityDto = new ActivityDto();
        BeanUtils.copyProperties(activity, activityDto);
        activityDto.setHallNames(activityService.concatHallNames(activity));
        activityDto.setImageURL(FileUtils.convertPathToURL(activity.getImagePath()));
        return Result.success(activityDto);
    }

    @GetMapping("/updateUserName")
    @ApiOperation("用户更新用户名")
    public Result<String> updateUserName(@RequestParam("userId")Integer userId, @RequestParam("userName")String userName
    ,@RequestHeader(name = "token") String token){
        User user = userService.getById(userId);

        if(user == null)
            return Result.fail("传入用户id有误");
        user.setUserName(userName);
        userService.saveOrUpdate(user);
        return Result.success("更新成功");
    }




}
