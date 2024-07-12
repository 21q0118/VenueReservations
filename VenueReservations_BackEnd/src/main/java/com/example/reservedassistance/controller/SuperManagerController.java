package com.example.reservedassistance.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.Vo.StadiumAddVo;
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
import com.example.reservedassistance.utils.AESUtils;
import com.example.reservedassistance.utils.DesensitizeUtils;
import com.example.reservedassistance.utils.FileUtils;
import com.example.reservedassistance.utils.MatchTemplateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.crypto.SecretKey;

import org.springframework.beans.BeanUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Api(tags = "系统管理员的基本接口")
@RequestMapping(value = "/superManager")
@CrossOrigin
public class SuperManagerController {


    @Resource
    private StadiumService stadiumService;

    @Resource
    private ManagerService managerService;

    @Resource
    private HallService hallService;

    @Resource
    private ActivityService activityService;

    @Resource
    private MessageService messageService;

    @Resource
    private CommentService commentService;

    @Resource
    private UserService userService;

    @Resource
    private VisitorService visitorService;

    @Resource
    private ReserveService reserveService;

    @Resource
    private UserVisitorService userVisitorService;

    @Resource
    private FileConfig fileConfig;

    @Resource
    private MessageSortStrategy messageSortStrategy;

    @Resource
    private ActivitySortStrategy activitySortStrategy;

    @PostMapping("/addStadium")
    @ApiOperation("系统管理员新增场馆")
    public Result<String> addStadium(@RequestHeader(name = "token") String token,
                                     @RequestBody StadiumAddVo stadiumVo) throws Exception {
        Stadium stadiumAdd = new Stadium();
        Manager managerAdd = new Manager();

        /*
            优先处理管理员信息，最后一起update
        */
        BeanUtils.copyProperties(stadiumVo.getManager(), managerAdd);
        managerAdd.setIsSuper(Manager.IS_NOT_SUPER);       // 场馆管理员
        SecretKey secretKeyManager = AESUtils.generateSecretKey();
        String encryptPassword = AESUtils.encrypt(Manager.DEFALUT_PASSWORD, secretKeyManager);
        managerAdd.setManagerPassword(encryptPassword);
        managerAdd.setKeyValue(AESUtils.convertSecretKeyToString(secretKeyManager));

        /*
            处理场馆
        */
        BeanUtils.copyProperties(stadiumVo, stadiumAdd);
        String imageURL = stadiumVo.getImageURL();
        String imagePath = FileUtils.convertURLToPath(imageURL);    //  获得场馆的图片存储路径
        stadiumAdd.setImagePath(imagePath);


        managerService.save(managerAdd);
        stadiumAdd.setManagerId(managerAdd.getId());
        stadiumService.save(stadiumAdd);
        /*
            处理展厅
        */

        List<String> hallNameList = stadiumVo.getHalls();
        List<Hall> hallAddList = new ArrayList<>();
        for (String hallName : hallNameList) {
            Hall hall = new Hall();
            hall.setHallName(hallName);
            hall.setStadiumId(stadiumAdd.getId());
            hallAddList.add(hall);
        }
        hallService.saveBatch(hallAddList);

        return Result.success("新增成功");
    }

    @GetMapping("/getAllStadiumInf")
    @ApiOperation("系统管理员获得所有场馆信息")
    public Result<List<StadiumDto>> getAllStadiumInf(@RequestHeader(name = "token") String token,
                                                     @RequestParam("managerId") Integer managerId) {

        Manager manager = managerService.getById(managerId);

        if (!manager.getIsSuper().equals(Manager.IS_SUPER))
            return Result.fail("您不是系统管理员，不具有相关权限");

        List<Stadium> stadiumList = stadiumService.list();
        List<StadiumDto> stadiumDtoList = new ArrayList<>();
        for (Stadium stadium : stadiumList) {

            StadiumDto stadiumDto = new StadiumDto();
            BeanUtils.copyProperties(stadium, stadiumDto);
            stadiumDto.setImageURL(FileUtils.convertPathToURL(stadium.getImagePath()));
            stadiumDtoList.add(stadiumDto);
        }

        List<StadiumScores> scoreStadium = stadiumService.getScore();
        // 将分数信息存放到 Map 中，以便后续按照 id 获取分数
        Map<Integer, Double> scoreMapStadium = scoreStadium.stream()
                .collect(Collectors.toMap(StadiumScores::getId, StadiumScores::getScores));
        for (StadiumDto stadiumDto : stadiumDtoList) {
            if (scoreMapStadium.get(stadiumDto.getId()) == null)
                stadiumDto.setScore(0.0);
            else
                stadiumDto.setScore(scoreMapStadium.get(stadiumDto.getId()));
        }
        stadiumDtoList.sort(Comparator.comparingDouble(stadiumDto ->
                scoreMapStadium.getOrDefault(((StadiumDto) stadiumDto).getId(), 0.0)).reversed());

        return Result.success(stadiumDtoList);
    }

    //   这个部分是一样的，可以进行提取，暂时动让前端看的简单一些
    @GetMapping("/getSingleStadiumInf")
    @ApiOperation("系统管理员选择具体的场馆获得的获得活动信息")     //  这个部分和ManagerController里面的场馆管理员获得场馆内部信息很相似
    public Result<UserClickStadiumDto> getSingleStadiumInf(@RequestHeader(name = "token") String token,
                                                           @RequestParam("stadiumId") Integer stadiumId,
                                                           @RequestParam("managerId") Integer managerId) {
        Manager manager = managerService.getById(managerId);
        if (manager == null)
            return Result.fail("管理员id异常");
        if (!manager.getIsSuper().equals(Manager.IS_SUPER))
            return Result.fail("您不是系统管理员，不具有该权限");

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
            ActivityDto dto = new ActivityDto();
            BeanUtils.copyProperties(activity, dto);
            String activityImagePath = activity.getImagePath();
            String activityImageURL = FileUtils.convertPathToURL(activityImagePath);
            dto.setImageURL(activityImageURL);

            // 将所涉及展厅进行拼接
            String hallNames = activityService.concatHallNames(activity);
            dto.setHallNames(hallNames);
            dto.setStadiumName(stadium.getStadiumName());
            activityDtoList.add(dto);
        }
//组装数据
        List<ActivityDto> activityDtoListResult = activitySortStrategy.sortActivityDtoList(activityDtoList, stadiumId);
//        List<ActivityScores> score = activityService.getScore(stadium.getId());
//        // 将分数信息存放到 Map 中，以便后续按照 id 获取分数
//        Map<Integer, Double> scoreMap = score.stream()
//                .collect(Collectors.toMap(ActivityScores::getId, ActivityScores::getScores));
//        for (ActivityDto activityDto : activityDtoList) {
//            if (scoreMap.get(activityDto.getId()) == null)
//                activityDto.setScore(0.0);
//            else
//                activityDto.setScore(scoreMap.get(activityDto.getId()));
//        }
//        // 对 stadiumList 进行排序
//        activityDtoList.sort(Comparator.comparingDouble(activityDto ->
//                scoreMap.getOrDefault(((ActivityDto) activityDto).getId(), 0.0)).reversed());

        List<StadiumScores> scoreStadium = stadiumService.getScore();
//        System.out.println(score);
        // 将分数信息存放到 Map 中，以便后续按照 id 获取分数
        Map<Integer, Double> scoreMapStadium = scoreStadium.stream()
                .collect(Collectors.toMap(StadiumScores::getId, StadiumScores::getScores));
        if (scoreMapStadium.get(stadiumDto.getId()) == null)
            stadiumDto.setScore(0.0);
        else
            stadiumDto.setScore(scoreMapStadium.get(stadium.getId()));
        UserClickStadiumDto userClickStadiumDto = new UserClickStadiumDto();
        userClickStadiumDto.setStadiumDto(stadiumDto);
        userClickStadiumDto.setActivityDtos(activityDtoListResult);
        return Result.success(userClickStadiumDto);
    }

    @GetMapping("/getHalls")
    @ApiOperation("获取展厅信息")
    public Result<List<Hall>> getHalls(@RequestHeader(name = "token") String token,
                                       @RequestParam("stadiumId") Integer stadiumId) {

        QueryWrapper<Hall> hallQueryWrapper = new QueryWrapper<>();
        hallQueryWrapper.eq("stadiumId", stadiumId);
        List<Hall> halls = hallService.list(hallQueryWrapper);

        return Result.success(halls);
    }

    @PostMapping("/updateStadium")
    @ApiOperation("系统管理员更新场馆")
    public Result<String> updateStadium(@RequestHeader(name = "token") String token,
                                        @RequestBody StadiumAlterSuperVo stadiumVo) {

        Manager manager = managerService.getById(stadiumVo.getSuperManagerId());
        if (!manager.getIsSuper().equals(Manager.IS_SUPER))
            return Result.fail("您不是系统管理员，没有权限更新场馆");

        Stadium stadium = stadiumService.getById(stadiumVo.getStadiumId());

        Integer stadiumId = stadium.getId();
        Integer managerId = stadium.getManagerId();

        Stadium stadiumAlter = new Stadium();
        BeanUtils.copyProperties(stadiumVo, stadiumAlter);
        stadiumAlter.setId(stadiumId);
        stadiumAlter.setManagerId(managerId);

        stadiumAlter.setImagePath(FileUtils.convertURLToPath(stadiumVo.getImageURL()));

        List<Hall> halls = stadiumVo.getHalls();
        for (Hall hall : halls) {
            hall.setStadiumId(stadiumId);
        }

        hallService.saveOrUpdateBatch(halls);

        stadiumService.saveOrUpdate(stadiumAlter);
        return Result.success("更新场馆成功");

    }

    @GetMapping("/sendMessage")
    @ApiOperation("系统管理员向全体成员发送通知")
    public Result<String> sendMessage(@RequestHeader(name = "token") String token,
                                      @RequestParam("managerId") Integer managerId,
                                      @RequestParam("content") String content) {

        Manager manager = managerService.getById(managerId);
        if (!manager.getIsSuper().equals(Manager.IS_SUPER))
            return Result.fail("您不是系统管理员，没有权限更新场馆");

        Message message = new Message();
        message.setAcceptId(Message.ALL_ACCEPTED_ID);
        message.setInvokeId(Message.SYSTEM_INVOKED_ID);
        message.setContent(content);
        message.setOperateTime(new Date());
        message.setIsRemove(Message.IS_NOT_REMOVE);
        messageService.save(message);
        return Result.success("发布成功");
    }


//    @GetMapping("/getHomeInf")
//    @ApiOperation("系统管理员获得主页信息")
//    public Result<String> getHomeInf(@RequestParam("managerId") Integer managerId){
//
//
//
//    }

    @GetMapping("/getCommentByActivity")
    @ApiOperation("从活动获得评价信息")
    public Result<CommentInfDto> getCommentByActivity(@RequestHeader(name = "token") String token,
                                                      @RequestParam("activityId") Integer activityId) {

        Activity activity = activityService.getById(activityId);
        if (activity.getIsCancel().equals(Activity.IS_CANCEL))
            return Result.fail("不存在该活动或活动已被撤销");

        CommentInfDto commentInfDto = new CommentInfDto();

        QueryWrapper<Comment> commentWrapper = new QueryWrapper<>();
        commentWrapper.eq("activityId", activityId);
        List<Comment> commentList = commentService.list(commentWrapper);

        double score = 0;

        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : commentList) {
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

    @GetMapping("/searchActivityVisitorInf")
    @ApiOperation("查找活动的具体成功验票的参与者信息，前端要进行脱敏处理")
    public Result<List<VisitorDto>> searchActivityVisitorInf(@RequestHeader(name = "token") String token,
                                                             @RequestParam("activityId") Integer activityId) throws Exception {

//        Activity activity = activityService.getById(activityId);
//        if(!activity.getStatus().equals("已结束"))
//            return Result.fail("活动还未结束");
        QueryWrapper<Reserve> reserveWrapper = new QueryWrapper<>();
        reserveWrapper.eq("activityId", activityId);
        List<Reserve> reserveList = reserveService.list(reserveWrapper);        // 获得该活动的所有预约信息

        List<VisitorDto> visitors = new ArrayList<>();

        for (Reserve reserve : reserveList) {
            if (reserve.getReserveStatus().equals(Reserve.STATUS_NOT_CHECK)
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
    public Result<String> generateVisitorFile(@RequestHeader(name = "token") String token,
                                              @RequestParam("activityId") Integer activityId) throws Exception {
        Result<List<VisitorDto>> result = searchActivityVisitorInf(token, activityId);
        List<VisitorDto> visitors = result.getData();
        List<VisitorExcel> visitorExcels = new ArrayList<>();
        for (VisitorDto visitorDto : visitors) {
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


    @GetMapping("/selectActivityByName")
    @ApiOperation("系统管理员用活动名模糊查询活动")
    public Result<List<ActivityDto>> selectActivityByName(@RequestHeader(name = "token") String token,
                                                          @RequestParam("stadiumId") Integer stadiumId, @RequestParam("activityName") String activityName) {


        List<ActivityDto> activityDtoList = new ArrayList<>();
        QueryWrapper<Activity> activityWrapper = new QueryWrapper<>();
        activityWrapper.eq("stadiumId", stadiumId)
                .eq("is_cancel", Activity.IS_NOT_CANCEL)
                .like("activity_name", activityName);
        List<Activity> activityList = activityService.list(activityWrapper);
        for (Activity activity : activityList) {
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
        //组装数据

        List<ActivityScores> score = activityService.getScore(stadiumId);
        // 将分数信息存放到 Map 中，以便后续按照 id 获取分数
        Map<Integer, Double> scoreMap = score.stream()
                .collect(Collectors.toMap(ActivityScores::getId, ActivityScores::getScores));
        for (ActivityDto activityDto : activityDtoList) {
            if (scoreMap.get(activityDto.getId()) == null)
                activityDto.setScore(0.0);
            else
                activityDto.setScore(scoreMap.get(activityDto.getId()));
        }
        activityDtoList.sort(Comparator.comparingDouble(activityDto ->
                scoreMap.getOrDefault(((ActivityDto) activityDto).getId(), 0.0)).reversed());

        return Result.success(activityDtoList);
    }


    @GetMapping("/selectStadiumByName")
    @ApiOperation("系统管理员用场馆名模糊查询场馆")
    public Result<List<StadiumDto>> selectStadiumByName(@RequestHeader(name = "token") String token, @RequestParam("stadiumName") String stadiumName) {

        List<StadiumDto> stadiumDtoList = new ArrayList<>();
        QueryWrapper<Stadium> stadiumWrapper = new QueryWrapper<>();
        stadiumWrapper
//                .eq("is_cancel", Activity.IS_NOT_CANCEL)
                .like("stadium_name", stadiumName);
        List<Stadium> stadiumList = stadiumService.list(stadiumWrapper);
        for (Stadium stadium : stadiumList) {
            StadiumDto dto = new StadiumDto();
            BeanUtils.copyProperties(stadium, dto);
            String stadiumImagePath = stadium.getImagePath();
            String stadiumImageURL = FileUtils.convertPathToURL(stadiumImagePath);
            dto.setImageURL(stadiumImageURL);
            stadiumDtoList.add(dto);
        }
        //组装数据
        List<StadiumScores> scoreStadium = stadiumService.getScore();
        // 将分数信息存放到 Map 中，以便后续按照 id 获取分数
        Map<Integer, Double> scoreMapStadium = scoreStadium.stream()
                .collect(Collectors.toMap(StadiumScores::getId, StadiumScores::getScores));
        for (StadiumDto stadiumDto : stadiumDtoList) {
            if (scoreMapStadium.get(stadiumDto.getId()) == null)
                stadiumDto.setScore(0.0);
            else
                stadiumDto.setScore(scoreMapStadium.get(stadiumDto.getId()));
        }
        return Result.success(stadiumDtoList);
    }

    @GetMapping("/getMessageSendByOwn")
    @ApiOperation("系统管理员查询自己发布的通知")
    public Result<List<MessageDto>> getMessageSendByOwn(@RequestHeader(name = "token") String token,
                                                        @RequestParam("superManagerId") Integer superManagerId) {
        Manager superManager = managerService.getById(superManagerId);
        if (superManager == null)
            return Result.fail("传入id有误");
        if (!superManager.getIsSuper().equals(Manager.IS_SUPER))
            return Result.fail("您不是系统管理员，您不具有该权限");

        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("invokeId", Message.SYSTEM_INVOKED_ID)
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

    @GetMapping("/getMessageSendByStadium")
    @ApiOperation("系统管理员查询系统内场馆发布的通知")
    public Result<List<MessageDto>> getMessageSendByStadium(@RequestHeader(name = "token") String token,
                                                        @RequestParam("superManagerId") Integer superManagerId,
                                                            @RequestParam("stadiumId")Integer stadiumId) {
        Manager superManager = managerService.getById(superManagerId);
        if (superManager == null)
            return Result.fail("传入id有误");
        if (!superManager.getIsSuper().equals(Manager.IS_SUPER))
            return Result.fail("您不是系统管理员，您不具有该权限");

        Integer managerId = stadiumService.getById(stadiumId).getManagerId();

        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("invokeId", managerId)
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

    @GetMapping("/getMessageSendByStadiumTime")
    @ApiOperation("系统管理员按时间段查询系统内场馆发布的通知")
    public Result<List<MessageDto>> getMessageSendByStadiumTime(@RequestHeader(name = "token") String token,
                                                            @RequestParam("superManagerId") Integer superManagerId,
                                                            @RequestParam("stadiumId")Integer stadiumId,
                                                                @RequestParam("beginTime") @DateTimeFormat(pattern = "yyyy-MM-dd") Date beginTime,
                                                                @RequestParam("endTime") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime ) {
        Manager superManager = managerService.getById(superManagerId);
        if (superManager == null)
            return Result.fail("传入id有误");
        if (!superManager.getIsSuper().equals(Manager.IS_SUPER))
            return Result.fail("您不是系统管理员，您不具有该权限");

        Integer managerId = stadiumService.getById(stadiumId).getManagerId();

        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("invokeId", managerId)
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
    @GetMapping("/getMessageSendByOwnTime")
    @ApiOperation("系统管理员按照时间段查询自己发布的通知")
    public Result<List<MessageDto>> getMessageSendByOwnTime(@RequestHeader(name = "token") String token,
                                                        @RequestParam("superManagerId") Integer superManagerId,
                                                            @RequestParam("beginTime") @DateTimeFormat(pattern = "yyyy-MM-dd") Date beginTime,
                                                            @RequestParam("endTime") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        Manager superManager = managerService.getById(superManagerId);
        if (superManager == null)
            return Result.fail("传入id有误");
        if (!superManager.getIsSuper().equals(Manager.IS_SUPER))
            return Result.fail("您不是系统管理员，您不具有该权限");

        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("invokeId", Message.SYSTEM_INVOKED_ID)
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
                                                 @RequestParam("superManagerId") Integer superManagerId,
                                                 @RequestParam("messageId") Integer messageId) {
        Manager superManager = managerService.getById(superManagerId);
        if (superManager == null)
            return Result.fail("传入id有误");
        if (!superManager.getIsSuper().equals(Manager.IS_SUPER))
            return Result.fail("您不是系统管理员，您不具有该权限");

        Message message = messageService.getById(messageId);

        if (message.getIsRemove().equals(Message.IS_REMOVE))
            return Result.fail("该通知已撤回");

        if(MatchTemplateUtils.matchesAny(message.getContent())){
            return Result.fail("不可删除模板信息");
        }
        message.setIsRemove(Message.IS_REMOVE);
        messageService.saveOrUpdate(message);
        return Result.success("撤回通知成功");
    }

}
