package com.example.reservedassistance.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.entity.Message;
import com.example.reservedassistance.entity.MessageIsDelete;
import com.example.reservedassistance.entity.MessageIsRead;
import com.example.reservedassistance.service.MessageIsDeleteService;
import com.example.reservedassistance.service.MessageIsReadService;
import com.example.reservedassistance.service.MessageService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@ApiModel("信息板块")
@RequestMapping(value = "/messgae")
public class MessageController {


    @Resource
    private MessageIsReadService messageIsReadService;

    @Resource
    private MessageService messageService;

    @Resource
    private MessageIsDeleteService messageIsDeleteService;

    @GetMapping("/readMessage")
    @ApiOperation("用户阅读消息")
    public Result<String> readMessage(@RequestParam("messageId")Integer messageId,
                                      @RequestParam("userId")Integer userId,
                                      @RequestHeader(name = "token")String token){

        QueryWrapper<MessageIsRead> wrapper = new QueryWrapper<>();
        wrapper.eq("messageId", messageId)
                .eq("userId", userId);
        MessageIsRead detail = messageIsReadService.getOne(wrapper, false);
        if(detail != null)
            return Result.success("");

        Integer acceptId = messageService.getById(messageId).getAcceptId();

        if(acceptId.equals(userId) || acceptId.equals(Message.ALL_ACCEPTED_ID)){

            MessageIsRead messageIsRead = new MessageIsRead();
            messageIsRead.setMessageId(messageId);
            messageIsRead.setUserId(userId);
            messageIsReadService.save(messageIsRead);
        }
        return Result.success("已阅读");
    }

    @GetMapping("/deleteMessage")
    @ApiOperation("用户逻辑删除消息")
    public Result<String> deleteMessage(@RequestHeader(name = "token") String token,
                                        @RequestParam("messageId") Integer messageId, @RequestParam("userId")Integer userId){
        QueryWrapper<MessageIsDelete> wrapper = new QueryWrapper<>();
        wrapper.eq("messageId", messageId)
                .eq("userId", userId);
        MessageIsDelete detail = messageIsDeleteService.getOne(wrapper, false);
        if(detail != null)
            return Result.fail("请勿重复删除");

        Integer acceptId = messageService.getById(messageId).getAcceptId();

        if(acceptId.equals(userId) || acceptId.equals(Message.ALL_ACCEPTED_ID)){

            MessageIsDelete messageIsDelete = new MessageIsDelete();
            messageIsDelete.setMessageId(messageId);
            messageIsDelete.setUserId(userId);
            messageIsDeleteService.save(messageIsDelete);

        }
        return Result.success("已删除");

    }

}
