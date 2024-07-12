package com.example.reservedassistance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reservedassistance.db.StadiumScores;
import com.example.reservedassistance.entity.Stadium;
import com.example.reservedassistance.entity.User;

import java.util.List;

public interface StadiumService extends IService<Stadium> {

    List<StadiumScores> getScore();
}
