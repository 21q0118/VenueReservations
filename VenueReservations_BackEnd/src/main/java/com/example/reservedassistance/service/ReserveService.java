package com.example.reservedassistance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reservedassistance.entity.Reserve;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ReserveService extends IService<Reserve> {
//    Boolean isInvoked(Reserve reserve);

//    Boolean isInvoked(Reserve reserveSearch, List<Reserve> checkingReserves);

    Boolean haveReservedActivity(Integer userId, Integer activityId);

    List<Integer> getUserIdsByActivity(Integer activityId);

    List<Integer> getUserVisitorIdsByActivity(Integer activityId);

    Reserve getReserveInf(String telephone, Integer activityId);
}
