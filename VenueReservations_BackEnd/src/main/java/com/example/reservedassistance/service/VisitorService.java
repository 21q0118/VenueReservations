package com.example.reservedassistance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reservedassistance.entity.Visitor;

public interface VisitorService extends IService<Visitor> {
//    Boolean isExistUser(Integer visitorId);

    Integer getUserId(Integer visitorId);


}
