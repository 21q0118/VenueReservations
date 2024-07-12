package com.example.reservedassistance.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;

import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.MsgListener.ReserveListener;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.Vo.ActivityAddVo;
import com.example.reservedassistance.Vo.StadiumAlterSuperVo;
import com.example.reservedassistance.Vo.StadiumAlterVo;
import com.example.reservedassistance.config.FileConfig;
import com.example.reservedassistance.db.ActivityScores;
import com.example.reservedassistance.db.StadiumScores;
import com.example.reservedassistance.dto.*;
import com.example.reservedassistance.entity.*;
import com.example.reservedassistance.service.*;
import com.example.reservedassistance.strategy.ActivitySortStrategy;
import com.example.reservedassistance.strategy.MessageSortStrategy;
import com.example.reservedassistance.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Api(tags = "场馆管理员接口")
@RequestMapping(value = "/managers")
@CrossOrigin
public class ManagerController {

    @Resource
    private ActivityService activityService;

    @Resource
    private StadiumService stadiumService;

    @Resource
    private ManagerService managerService;

    @Resource
    private ActivityHallService activityHallService;

    @Resource
    private HallService hallService;

    @Resource
    private ReserveService reserveService;


    @Resource
    private UserVisitorService userVisitorService;

    @Resource
    private VisitorService visitorService;

    @Resource
    private FileConfig fileConfig;

    @Resource
    private MessageService messageService;

    @Resource
    private UserService userService;

    @Resource
    private CommentService commentService;

    @Resource
    AmqpAdmin admin;

    @Resource
    SimpleMessageListenerContainer container;

    @Resource
    ReserveListener message;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    private MessageSortStrategy messageSortStrategy;

    @Resource
    private ActivitySortStrategy activitySortStrategy;
    @PostMapping("/addActivity")
    @ApiOperation("场馆管理员新增活动")
    public Result<String> addActivity(@RequestHeader(name = "token")String token,
                                      @RequestBody ActivityAddVo activityVo){

        Activity activityAdd = new Activity();
        BeanUtils.copyProperties(activityVo, activityAdd);

        String imageURL = activityVo.getImageURL();
        String imagePath = FileUtils.convertURLToPath(imageURL);
        activityAdd.setImagePath(imagePath);
        activityAdd.setStatus(Activity.STATUS_WAITING);
        activityAdd.setIsCore(0);       // 默认发布的活动不是精华活动

        QueryWrapper<Stadium> wrapperStadium = new QueryWrapper<>();
        wrapperStadium.eq("managerId", activityVo.getManagerId());
        Stadium stadium = stadiumService.getOne(wrapperStadium);
        if(stadium == null)
            return Result.fail("输入场馆管理员id有误");

        if(managerService.getById(activityVo.getManagerId()).getIsSuper().equals(1))
            return Result.fail("系统管理员没有新增活动的权限");

        activityAdd.setIsCancel(Activity.IS_NOT_CANCEL);
        activityAdd.setStadiumId(stadium.getId());

        List<Integer> hallIdList = activityVo.getHallIdList();
        List<ActivityHall> activityHallAddList = new ArrayList<>();
        for(Integer hallId: hallIdList){
            Hall hall = hallService.getById(hallId);
            if(hall == null)
                return Result.fail("请输入正确的展厅id");
            if(!hall.getStadiumId().equals(stadium.getId()))
                return Result.fail("请输入正确的展厅id");
        }
        activityService.save(activityAdd);
        for(Integer hallId: hallIdList){

            ActivityHall activityHall = new ActivityHall();
            activityHall.setActivityId(activityAdd.getId());
            activityHall.setHallId(hallId);

            activityHallAddList.add(activityHall);
        }

        activityHallService.saveBatch(activityHallAddList);

        // 创建此活动的预约队列
        String qName = activityAdd.getId().toString() + ".reserve";
        MqUtils mqUtils = new MqUtils(admin, container, message, rabbitTemplate);
        mqUtils.createQueue(qName);

        return Result.success("新增活动成功");
    }

    @GetMapping("/getHomeInf")
    @ApiOperation("点击编辑界面获得的所有信息")      //  这个部分和UserController里面的用户点击获得场馆内部信息很相似
    public Result<ManagerHomeInfDto> getHome(@RequestHeader(name = "token")String token,
                                             @RequestParam("managerId") Integer managerId){

        StadiumDto stadiumDto = new StadiumDto();

        // 先处理好场馆信息
        QueryWrapper<Stadium> stadiumWrapper = new QueryWrapper<>();
        stadiumWrapper.eq("managerId", managerId);
        Stadium stadium = stadiumService.getOne(stadiumWrapper);
        String stadiumImagePath = stadium.getImagePath();
        String stadiumImageURL = FileUtils.convertPathToURL(stadiumImagePath);

        BeanUtils.copyProperties(stadium, stadiumDto);
        stadiumDto.setImageURL(stadiumImageURL);

        QueryWrapper<Hall> wrapper = new QueryWrapper<>();
        wrapper.eq("stadiumId", stadium.getId());
        List<Hall> hallList = hallService.list(wrapper);
        List<HallDto> hallDtoList = new ArrayList<>();
        for(Hall hall: hallList){
            HallDto hallDto = new HallDto();
            hallDto.setId(hall.getId());
            hallDto.setHallName(hall.getHallName());
            hallDtoList.add(hallDto);
        }
        stadiumDto.setHallDtos(hallDtoList);
        // 处理所有活动信息
        List<ActivityDto> activityDtoList = new ArrayList<>();
        QueryWrapper<Activity> activityWrapper = new QueryWrapper<>();
        activityWrapper.eq("stadiumId", stadium.getId());
        activityWrapper.eq("is_cancel", Activity.IS_NOT_CANCEL);
        List<Activity> activityList = activityService.list(activityWrapper);
        for(Activity activity : activityList){
            ActivityDto dto = new ActivityDto();
            BeanUtils.copyProperties(activity, dto);
            String activityImagePath = activity.getImagePath();
            String activityImageURL = FileUtils.convertPathToURL(activityImagePath);
            dto.setImageURL(activityImageURL);

            // 将所涉及展厅进行拼接
            String hallNames = activityService.concatHallNames(activity);
            dto.setHallNames(hallNames);
            activityDtoList.add(dto);
        }

        ManagerHomeInfDto infDto = new ManagerHomeInfDto();
        //组装数据
        List<ActivityDto> activityDtoListResult = activitySortStrategy.sortActivityDtoList(activityDtoList, stadium.getId());
//        List<ActivityScores> score = activityService.getScore(stadium.getId());
//        // 将分数信息存放到 Map 中，以便后续按照 id 获取分数
//        Map<Integer, Double> scoreMap = score.stream()
//                .collect(Collectors.toMap(ActivityScores::getId, ActivityScores::getScores));
//        for(ActivityDto activityDto:activityDtoList){
//            if(scoreMap.get(activityDto.getId()) == null)
//                activityDto.setScore(0.0);
//            else
//                activityDto.setScore(scoreMap.get(activityDto.getId()));
//        }
//        // 对 stadiumList 进行排序
//        activityDtoList.sort(Comparator.comparingDouble(activityDto ->
//                scoreMap.getOrDefault(((ActivityDto) activityDto).getId(), 0.0)).reversed());

        List<StadiumScores> scoreStadium = stadiumService.getScore();
        // 将分数信息存放到 Map 中，以便后续按照 id 获取分数
        Map<Integer, Double> scoreMapStadium = scoreStadium.stream()
                .collect(Collectors.toMap(StadiumScores::getId, StadiumScores::getScores));
        if(scoreMapStadium.get(stadiumDto.getId()) == null)
            stadiumDto.setScore(0.0);
        else
            stadiumDto.setScore(scoreMapStadium.get(stadium.getId()));
        infDto.setActivityDtos(activityDtoListResult);
        infDto.setStadiumDto(stadiumDto);
        return Result.success(infDto);
    }

    @PostMapping("/alterStadiumInf")
    @ApiOperation("场馆管理员修改场馆信息")        //  这里展厅的更新有很大的问题，先不处理
    public Result<String> alterStadiumInf(@RequestHeader(name = "token")String token,
                                          @RequestBody StadiumAlterVo stadiumVo){

        QueryWrapper<Stadium> stadiumWrapper = new QueryWrapper<>();
        stadiumWrapper.eq("managerId", stadiumVo.getManagerId());
        Stadium stadium = stadiumService.getOne(stadiumWrapper);

        BeanUtils.copyProperties(stadiumVo, stadium);
        stadium.setImagePath(FileUtils.convertURLToPath(stadiumVo.getImageURL()));

        stadiumService.saveOrUpdate(stadium);
        return Result.success("更新成功");
//        hallService
    }

    @GetMapping("/selectActivityByName")
    @ApiOperation("场馆管理员用活动名模糊查询活动")
    public Result<List<ActivityDto>> selectActivityByName(@RequestHeader(name = "token")String token,
                                                          @RequestParam("managerId") Integer managerId, @RequestParam("activityName") String activityName){

        QueryWrapper<Stadium> stadiumWrapper = new QueryWrapper<>();
        stadiumWrapper.eq("managerId", managerId);
        Integer stadiumId = stadiumService.getOne(stadiumWrapper).getId();

        List<ActivityDto> activityDtoList = new ArrayList<>();
        QueryWrapper<Activity> activityWrapper = new QueryWrapper<>();
        activityWrapper.eq("stadiumId", stadiumId)
                .eq("is_cancel", Activity.IS_NOT_CANCEL)
                .like("activity_name", activityName);
        List<Activity> activityList = activityService.list(activityWrapper);
        for(Activity activity : activityList){
            ActivityDto dto = new ActivityDto();
            BeanUtils.copyProperties(activity, dto);
            String activityImagePath = activity.getImagePath();
            String activityImageURL = FileUtils.convertPathToURL(activityImagePath);
            dto.setImageURL(activityImageURL);

            // 将所涉及展厅进行拼接
            String hallNames = activityService.concatHallNames(activity);
            dto.setHallNames(hallNames);
            activityDtoList.add(dto);
        }
        return Result.success(activityDtoList);
    }


    @GetMapping("/searchActivityVisitorInf")
    @ApiOperation("查找活动的具体成功验票的参与者信息，前端要进行脱敏处理")
    public Result<List<VisitorDto>> searchActivityVisitorInf(@RequestHeader(name = "token")String token,
                                                             @RequestParam("activityId") Integer activityId) throws Exception {

//        Activity activity = activityService.getById(activityId);
//        if(!activity.getStatus().equals("已结束"))
//            return Result.fail("活动还未结束");
        QueryWrapper<Reserve> reserveWrapper = new QueryWrapper<>();
        reserveWrapper.eq("activityId", activityId);
        List<Reserve> reserveList = reserveService.list(reserveWrapper);        // 获得该活动的所有预约信息

        List<VisitorDto> visitors = new ArrayList<>();

        for(Reserve reserve : reserveList){
            if( reserve.getReserveStatus().equals(Reserve.STATUS_NOT_CHECK)
            || reserve.getReserveStatus().equals(Reserve.STATUS_CANCEL))
                continue;
            Integer visitorId = userVisitorService.getById(reserve.getUserVisitorId()).getVisitorId();
            Visitor visitor = visitorService.getById(visitorId);
            VisitorDto visitorDto = new VisitorDto();
            BeanUtils.copyProperties(visitor, visitorDto);
            SecretKey secretKey = AESUtils.convertStringToSecretKey(visitor.getKeyValue());
            String identificationNum = AESUtils.decrypt(visitor.getIdentificationNum(), secretKey);
//            visitorDto.setIdentificationNum(identificationNum);
            visitorDto.setIdentificationNum(DesensitizeUtils.desensitizeIDCard(identificationNum));
            visitorDto.setTelephone(DesensitizeUtils.desensitizePhoneNumber(visitorDto.getTelephone()));
            visitorDto.setRealName(DesensitizeUtils.desensitizeName(visitorDto.getRealName()));
            visitors.add(visitorDto);
        }

        return Result.success(visitors);
    }

    @GetMapping("/generateVisitorFile")
    @ApiOperation("生成参加该活动的人员信息")
    public Result<String> generateVisitorFile(@RequestHeader(name = "token")String token,
                                              @RequestParam("activityId") Integer activityId) throws Exception {
        Result<List<VisitorDto>> result = searchActivityVisitorInf(token, activityId);
        List<VisitorDto> visitors = result.getData();
        List<VisitorExcel> visitorExcels = new ArrayList<>();
        for(VisitorDto visitorDto :visitors){
            VisitorExcel visitorExcel = new VisitorExcel();
            BeanUtils.copyProperties(visitorDto, visitorExcel);
            visitorExcels.add(visitorExcel);
        }
        String activityName = activityService.getById(activityId).getActivityName();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = sdf.format(new Date());
        String destPath = fileConfig.getBasePath() + File.separator + activityName + "_" + dateFormat + ".xlsx";

        System.out.println(visitorExcels);
        ExcelWriter excelWriter = EasyExcel.write(destPath, VisitorExcel.class).build();

        // 创建 WriteSheet 对象
        WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").build();

        // 将数据写入 Excel
        excelWriter.write(visitorExcels, writeSheet);

        // 关闭 ExcelWriter
        excelWriter.finish();

        return Result.success(FileUtils.convertPathToURL(destPath));
    }

    @GetMapping("/sendMessage")
    @ApiOperation("场馆管理员发布消息, 此处指的是向全体用户发信息")
    public Result<String> sendMessage(@RequestHeader(name = "token")String token,
                                      @RequestParam("managerId") Integer managerId, @RequestParam("String") String content){

        Message message = new Message();
        message.setAcceptId(Message.ALL_ACCEPTED_ID);
        message.setInvokeId(managerId);
        message.setContent(content);
        message.setOperateTime(new Date());
        message.setIsRemove(Message.IS_NOT_REMOVE);
        messageService.save(message);
        return Result.success("发布成功");
    }

    @GetMapping("/reverseActivity")
    @ApiOperation("管理员撤回还未开始的活动")
    public Result<String> reverseActivity(@RequestHeader(name = "token")String token,
                                          @RequestParam("activityId") Integer activityId, @RequestParam("reason") String reason){
        Activity activity = activityService.getById(activityId);
        if(!activity.getStatus().equals(Activity.STATUS_WAITING))
            return Result.fail("该活动已结束，无法撤销");

        Integer stadiumId = activity.getStadiumId();
        Stadium stadium = stadiumService.getById(stadiumId);
        Integer managerId = stadium.getManagerId();

        List<Integer> userIds = reserveService.getUserIdsByActivity(activityId);
        System.out.println(userIds);
        List<Message> messages = new ArrayList<>();
        for(Integer id: userIds){
            Message message = new Message();
            message.setInvokeId(managerId);
            message.setAcceptId(id);
            String realName = userService.getById(id).getRealName();
            message.setContent(messageService.managerReverseActivityMethod(realName, activity.getActivityName(), stadium.getStadiumName(), reason));
            message.setOperateTime(new Date());
            message.setIsRemove(Message.IS_NOT_REMOVE);
            messages.add(message);
        }
        System.out.println(messages);
        messageService.saveBatch(messages);


        // 怎么进行活动撤销
        // 删除最近的一条操作记录
        // 自动补一条取消预约记录
        QueryWrapper<Reserve> reserveWrapper = new QueryWrapper<>();
        reserveWrapper.eq("activityId", activityId);
        List<Reserve> reserveList = reserveService.list(reserveWrapper);
        System.out.println(reserveList);

        List<Reserve> addReserveList = new ArrayList<>();
        List<Integer> userVisitorIds = reserveService.getUserVisitorIdsByActivity(activityId);
        for(Integer userVisitorId : userVisitorIds){
            int reserveNum = 0;
            int reserveNotNum = 0;
            Reserve reserveCopy = new Reserve();
            for(Reserve reserve :reserveList){
                if(reserve.getUserVisitorId().equals(userVisitorId)){
                    if(!(reserveCopy.getId() != null && reserveCopy.getOperateTime().after(reserve.getOperateTime())))
                        BeanUtils.copyProperties(reserve, reserveCopy);
//                        if(reserveCopy.getOperateTime().before(reserve.getOperateTime()))
//                            BeanUtils.copyProperties(reserve, reserveCopy);

                    if(reserve.getIsReserve().equals(Reserve.IS_RESERVE))
                        reserveNum++;
                    else
                        reserveNotNum++;
                }
            }
            if(reserveNum != reserveNotNum) {
                Reserve reserveAdd = new Reserve();
                BeanUtils.copyProperties(reserveCopy, reserveAdd);
                reserveAdd.setId(null);
                reserveAdd.setIsReserve(Reserve.IS_NOT_RESERVE);
                reserveAdd.setOperateTime(new Date());
                reserveAdd.setReserveStatus(Reserve.STATUS_CANCEL);

                reserveCopy.setReserveStatus(Reserve.STATUS_CANCEL);
                addReserveList.add(reserveAdd);
                addReserveList.add(reserveCopy);
            }
        }
        activity.setIsCancel(Activity.IS_CANCEL);
        activityService.saveOrUpdate(activity);
        reserveService.saveOrUpdateBatch(addReserveList);
        return Result.success("撤回成功");

    }


    @GetMapping("/getCommentByActivity")
    @ApiOperation("从活动获得评价信息")
    public Result<CommentInfDto> getCommentByActivity(@RequestHeader(name = "token")String token,
                                                      @RequestParam("activityId") Integer activityId){

        Activity activity = activityService.getById(activityId);
        if(activity.getIsCancel().equals(Activity.IS_CANCEL))
            return Result.fail("不存在该活动或活动已被撤销");

        CommentInfDto commentInfDto = new CommentInfDto();

        QueryWrapper<Comment> commentWrapper = new QueryWrapper<>();
        commentWrapper.eq("activityId", activityId);
        List<Comment> commentList = commentService.list(commentWrapper);

        double score = 0;

        List<CommentDto> commentDtos = new ArrayList<>();
        for(Comment comment :commentList){
            CommentDto commentDto = new CommentDto();
            BeanUtils.copyProperties(comment, commentDto);
            commentDto.setUserName(userService.getById(comment.getUserId()).getUserName());

            score += comment.getScore();

            commentDtos.add(commentDto);
        }

        double averageScore = score / commentList.size();

        commentInfDto.setCommentDtos(commentDtos);
        commentInfDto.setAverageScore(averageScore);
        return Result.success(commentInfDto);
    }

    @GetMapping("/getHalls")
    @ApiOperation("获取展厅信息")
    public Result<List<Hall>> getHalls(@RequestHeader(name = "token") String token,
                                       @RequestParam("stadiumId") Integer stadiumId){

        QueryWrapper<Hall> hallQueryWrapper = new QueryWrapper<>();
        hallQueryWrapper.eq("stadiumId", stadiumId);
        List<Hall> halls = hallService.list(hallQueryWrapper);

        return Result.success(halls);
    }

    @PostMapping("/updateStadium")
    @ApiOperation("场馆管理员更新场馆")
    public Result<String> updateStadium(@RequestHeader(name = "token")String token,
                                        @RequestBody StadiumAlterSuperVo stadiumVo){

//        Manager manager = managerService.getById(stadiumVo.getSuperManagerId());

        Stadium stadium = stadiumService.getById(stadiumVo.getStadiumId());

        Integer stadiumId = stadium.getId();
        Integer managerId = stadium.getManagerId();

        Stadium stadiumAlter = new Stadium();
        BeanUtils.copyProperties(stadiumVo, stadiumAlter);
        stadiumAlter.setId(stadiumId);
        stadiumAlter.setManagerId(managerId);

        stadiumAlter.setImagePath(FileUtils.convertURLToPath(stadiumVo.getImageURL()));

        List<Hall> halls = stadiumVo.getHalls();
        for(Hall hall : halls){
            hall.setStadiumId(stadiumId);
        }

        hallService.saveOrUpdateBatch(halls);

        stadiumService.saveOrUpdate(stadiumAlter);
        return Result.success("更新场馆成功");

    }



    @GetMapping("/getMessageSendByOwn")
    @ApiOperation("场馆管理员查询自己发布的通知")
    public Result<List<MessageDto>> getMessageSendByOwn(@RequestHeader(name = "token") String token,
                                                        @RequestParam("managerId") Integer managerId) {
        Manager manager = managerService.getById(managerId);
        if (manager == null)
            return Result.fail("传入id有误");

        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("invokeId", manager.getId())
                .eq("is_remove", Message.IS_NOT_REMOVE);
        List<Message> messageList = messageService.list(wrapper);
        List<MessageDto> messageListResult = new ArrayList<>();
        for (Message message : messageList) {
            if (MatchTemplateUtils.matchesAny(message.getContent()))
                continue;
            else {
                MessageDto messageDto = new MessageDto();
                BeanUtils.copyProperties(message, messageDto);
                messageListResult.add(messageDto);
            }
        }
        List<MessageDto> messageDtos = messageSortStrategy.sortMessageDtos(messageListResult);
        return Result.success(messageDtos);
    }


    @GetMapping("/getMessageSendByOwnTime")
    @ApiOperation("场馆管理员通过时间段查询自己发布的通知")
    public Result<List<MessageDto>> getMessageSendByOwnTime(@RequestHeader(name = "token") String token,
                                                        @RequestParam("managerId") Integer managerId,
                                                            @RequestParam("beginTime") @DateTimeFormat(pattern = "yyyy-MM-dd") Date beginTime,
                                                            @RequestParam("endTime") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        Manager manager = managerService.getById(managerId);
        if (manager == null)
            return Result.fail("传入id有误");

        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("invokeId", manager.getId())
                .eq("is_remove", Message.IS_NOT_REMOVE)
                .between("operate_time", beginTime, endTime);
        List<Message> messageList = messageService.list(wrapper);
        List<MessageDto> messageListResult = new ArrayList<>();
        for (Message message : messageList) {
            if (MatchTemplateUtils.matchesAny(message.getContent()))
                continue;
            else {
                MessageDto messageDto = new MessageDto();
                BeanUtils.copyProperties(message, messageDto);
                messageListResult.add(messageDto);
            }
        }
        List<MessageDto> messageDtos = messageSortStrategy.sortMessageDtos(messageListResult);
        return Result.success(messageDtos);
    }



    @GetMapping("/deleteMessageSendByOwn")
    @ApiOperation("撤回发布的通知")
    public Result<String> deleteMessageSendByOwn(@RequestHeader(name = "token") String token,
                                                 @RequestParam("managerId") Integer managerId,
                                                 @RequestParam("messageId") Integer messageId) {
        Manager manager = managerService.getById(managerId);
        if (manager == null)
            return Result.fail("传入id有误");

        Message message = messageService.getById(messageId);

        if (message.getIsRemove().equals(Message.IS_REMOVE))
            return Result.fail("该通知已撤回");

        if(MatchTemplateUtils.matchesAny(message.getContent())){
            return Result.fail("不可删除模板信息");
        }
        if(!message.getInvokeId().equals(managerId))
            return Result.fail("您不具有删除该通知的权限");
        message.setIsRemove(Message.IS_REMOVE);
        messageService.saveOrUpdate(message);
        return Result.success("撤回通知成功");
    }
}
