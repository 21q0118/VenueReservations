package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.entity.Reserve;
import com.example.reservedassistance.entity.User;
import com.example.reservedassistance.entity.UserVisitor;
import com.example.reservedassistance.entity.Visitor;
import com.example.reservedassistance.mapper.ReserveMapper;
import com.example.reservedassistance.mapper.UserMapper;
import com.example.reservedassistance.mapper.UserVisitorMapper;
import com.example.reservedassistance.mapper.VisitorMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReserveServiceImplTest {

    @Mock
    private ReserveMapper mockReserveMapper;
    @Mock
    private UserMapper mockUserMapper;
    @Mock
    private UserVisitorMapper mockUserVisitorMapper;
    @Mock
    private VisitorMapper mockVisitorMapper;

    @InjectMocks
    private ReserveServiceImpl reserveServiceImplUnderTest;

    @Test
    void testHaveReservedActivity() {
        // Setup
        // Configure UserMapper.selectById(...).
        final User user = new User();
        user.setId(0);
        user.setRealName("realName");
        user.setIdentificationNum("identificationNum");
        user.setImagePath("imagePath");
        user.setTelephone("telephone");
        when(mockUserMapper.selectById(0)).thenReturn(user);

        // Configure VisitorMapper.selectOne(...).
        final Visitor visitor = new Visitor();
        visitor.setId(0);
        visitor.setRealName("realName");
        visitor.setIdentificationNum("identificationNum");
        visitor.setTelephone("telephone");
        visitor.setKeyValue("keyValue");
        when(mockVisitorMapper.selectOne(any(QueryWrapper.class))).thenReturn(visitor);

        // Configure UserVisitorMapper.selectList(...).
        final UserVisitor userVisitor = new UserVisitor();
        userVisitor.setId(0);
        userVisitor.setUserId(0);
        userVisitor.setVisitorId(0);
        userVisitor.setIsValid(0);
        final List<UserVisitor> userVisitorList = Arrays.asList(userVisitor);
        when(mockUserVisitorMapper.selectList(any(QueryWrapper.class))).thenReturn(userVisitorList);

        // Configure ReserveMapper.selectList(...).
        final Reserve reserve = new Reserve();
        reserve.setId(0);
        reserve.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        reserve.setUserVisitorId(0);
        reserve.setIsReserve(0);
        reserve.setReserveStatus("reserveStatus");
        final List<Reserve> reserveList = Arrays.asList(reserve);
        when(mockReserveMapper.selectList(any(QueryWrapper.class))).thenReturn(reserveList);

        // Run the test
        final Boolean result = reserveServiceImplUnderTest.haveReservedActivity(0, 0);

        // Verify the results
        assertThat(result).isFalse();
    }

    @Test
    void testHaveReservedActivity_UserVisitorMapperReturnsNoItems() {
        // Setup
        // Configure UserMapper.selectById(...).
        final User user = new User();
        user.setId(0);
        user.setRealName("realName");
        user.setIdentificationNum("identificationNum");
        user.setImagePath("imagePath");
        user.setTelephone("telephone");
        when(mockUserMapper.selectById(0)).thenReturn(user);

        // Configure VisitorMapper.selectOne(...).
        final Visitor visitor = new Visitor();
        visitor.setId(0);
        visitor.setRealName("realName");
        visitor.setIdentificationNum("identificationNum");
        visitor.setTelephone("telephone");
        visitor.setKeyValue("keyValue");
        when(mockVisitorMapper.selectOne(any(QueryWrapper.class))).thenReturn(visitor);

        when(mockUserVisitorMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        // Run the test
        final Boolean result = reserveServiceImplUnderTest.haveReservedActivity(0, 0);

        // Verify the results
        assertThat(result).isFalse();
    }

    @Test
    void testHaveReservedActivity_ReserveMapperReturnsNoItems() {
        // Setup
        // Configure UserMapper.selectById(...).
        final User user = new User();
        user.setId(0);
        user.setRealName("realName");
        user.setIdentificationNum("identificationNum");
        user.setImagePath("imagePath");
        user.setTelephone("telephone");
        when(mockUserMapper.selectById(0)).thenReturn(user);

        // Configure VisitorMapper.selectOne(...).
        final Visitor visitor = new Visitor();
        visitor.setId(0);
        visitor.setRealName("realName");
        visitor.setIdentificationNum("identificationNum");
        visitor.setTelephone("telephone");
        visitor.setKeyValue("keyValue");
        when(mockVisitorMapper.selectOne(any(QueryWrapper.class))).thenReturn(visitor);

        // Configure UserVisitorMapper.selectList(...).
        final UserVisitor userVisitor = new UserVisitor();
        userVisitor.setId(0);
        userVisitor.setUserId(0);
        userVisitor.setVisitorId(0);
        userVisitor.setIsValid(0);
        final List<UserVisitor> userVisitorList = Arrays.asList(userVisitor);
        when(mockUserVisitorMapper.selectList(any(QueryWrapper.class))).thenReturn(userVisitorList);

        when(mockReserveMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        // Run the test
        final Boolean result = reserveServiceImplUnderTest.haveReservedActivity(0, 0);

        // Verify the results
        assertThat(result).isFalse();
    }

    @Test
    void testGetUserIdsByActivity() {
        // Setup
        // Configure ReserveMapper.selectList(...).
        final Reserve reserve = new Reserve();
        reserve.setId(0);
        reserve.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        reserve.setUserVisitorId(0);
        reserve.setIsReserve(0);
        reserve.setReserveStatus("reserveStatus");
        final List<Reserve> reserveList = Arrays.asList(reserve);
        when(mockReserveMapper.selectList(any(QueryWrapper.class))).thenReturn(reserveList);

        // Configure UserVisitorMapper.selectById(...).
        final UserVisitor userVisitor = new UserVisitor();
        userVisitor.setId(0);
        userVisitor.setUserId(0);
        userVisitor.setVisitorId(0);
        userVisitor.setIsValid(0);
        when(mockUserVisitorMapper.selectById(0)).thenReturn(userVisitor);

        // Configure VisitorMapper.selectById(...).
        final Visitor visitor = new Visitor();
        visitor.setId(0);
        visitor.setRealName("realName");
        visitor.setIdentificationNum("identificationNum");
        visitor.setTelephone("telephone");
        visitor.setKeyValue("keyValue");
        when(mockVisitorMapper.selectById(0)).thenReturn(visitor);

        // Configure UserMapper.selectOne(...).
        final User user = new User();
        user.setId(0);
        user.setRealName("realName");
        user.setIdentificationNum("identificationNum");
        user.setImagePath("imagePath");
        user.setTelephone("telephone");
        when(mockUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(user);

        // Configure UserMapper.selectById(...).
        final User user1 = new User();
        user1.setId(0);
        user1.setRealName("realName");
        user1.setIdentificationNum("identificationNum");
        user1.setImagePath("imagePath");
        user1.setTelephone("telephone");
        when(mockUserMapper.selectById(0)).thenReturn(user1);

        // Configure VisitorMapper.selectOne(...).
        final Visitor visitor1 = new Visitor();
        visitor1.setId(0);
        visitor1.setRealName("realName");
        visitor1.setIdentificationNum("identificationNum");
        visitor1.setTelephone("telephone");
        visitor1.setKeyValue("keyValue");
        when(mockVisitorMapper.selectOne(any(QueryWrapper.class))).thenReturn(visitor1);

        // Configure UserVisitorMapper.selectList(...).
        final UserVisitor userVisitor1 = new UserVisitor();
        userVisitor1.setId(0);
        userVisitor1.setUserId(0);
        userVisitor1.setVisitorId(0);
        userVisitor1.setIsValid(0);
        final List<UserVisitor> userVisitorList = Arrays.asList(userVisitor1);
        when(mockUserVisitorMapper.selectList(any(QueryWrapper.class))).thenReturn(userVisitorList);

        // Run the test
        final List<Integer> result = reserveServiceImplUnderTest.getUserIdsByActivity(0);

        // Verify the results
        assertThat(result).isEqualTo(Arrays.asList(0));
    }

    @Test
    void testGetUserIdsByActivity_ReserveMapperReturnsNoItems() {
        // Setup
        when(mockReserveMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        // Run the test
        final List<Integer> result = reserveServiceImplUnderTest.getUserIdsByActivity(0);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetUserIdsByActivity_UserVisitorMapperSelectListReturnsNoItems() {
        // Setup
        // Configure ReserveMapper.selectList(...).
        final Reserve reserve = new Reserve();
        reserve.setId(0);
        reserve.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        reserve.setUserVisitorId(0);
        reserve.setIsReserve(0);
        reserve.setReserveStatus("reserveStatus");
        final List<Reserve> reserveList = Arrays.asList(reserve);
        when(mockReserveMapper.selectList(any(QueryWrapper.class))).thenReturn(reserveList);

        // Configure UserVisitorMapper.selectById(...).
        final UserVisitor userVisitor = new UserVisitor();
        userVisitor.setId(0);
        userVisitor.setUserId(0);
        userVisitor.setVisitorId(0);
        userVisitor.setIsValid(0);
        when(mockUserVisitorMapper.selectById(0)).thenReturn(userVisitor);

        // Configure VisitorMapper.selectById(...).
        final Visitor visitor = new Visitor();
        visitor.setId(0);
        visitor.setRealName("realName");
        visitor.setIdentificationNum("identificationNum");
        visitor.setTelephone("telephone");
        visitor.setKeyValue("keyValue");
        when(mockVisitorMapper.selectById(0)).thenReturn(visitor);

        // Configure UserMapper.selectOne(...).
        final User user = new User();
        user.setId(0);
        user.setRealName("realName");
        user.setIdentificationNum("identificationNum");
        user.setImagePath("imagePath");
        user.setTelephone("telephone");
        when(mockUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(user);

        // Configure UserMapper.selectById(...).
        final User user1 = new User();
        user1.setId(0);
        user1.setRealName("realName");
        user1.setIdentificationNum("identificationNum");
        user1.setImagePath("imagePath");
        user1.setTelephone("telephone");
        when(mockUserMapper.selectById(0)).thenReturn(user1);

        // Configure VisitorMapper.selectOne(...).
        final Visitor visitor1 = new Visitor();
        visitor1.setId(0);
        visitor1.setRealName("realName");
        visitor1.setIdentificationNum("identificationNum");
        visitor1.setTelephone("telephone");
        visitor1.setKeyValue("keyValue");
        when(mockVisitorMapper.selectOne(any(QueryWrapper.class))).thenReturn(visitor1);

        when(mockUserVisitorMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        // Run the test
        final List<Integer> result = reserveServiceImplUnderTest.getUserIdsByActivity(0);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetUserVisitorIdsByActivity() {
        // Setup
        // Configure ReserveMapper.selectList(...).
        final Reserve reserve = new Reserve();
        reserve.setId(0);
        reserve.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        reserve.setUserVisitorId(0);
        reserve.setIsReserve(0);
        reserve.setReserveStatus("reserveStatus");
        final List<Reserve> reserveList = Arrays.asList(reserve);
        when(mockReserveMapper.selectList(any(QueryWrapper.class))).thenReturn(reserveList);

        // Run the test
        final List<Integer> result = reserveServiceImplUnderTest.getUserVisitorIdsByActivity(0);

        // Verify the results
        assertThat(result).isEqualTo(Arrays.asList(0));
    }

    @Test
    void testGetUserVisitorIdsByActivity_ReserveMapperReturnsNoItems() {
        // Setup
        when(mockReserveMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        // Run the test
        final List<Integer> result = reserveServiceImplUnderTest.getUserVisitorIdsByActivity(0);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetReserveInf() {
        // Setup
        final Reserve expectedResult = new Reserve();
        expectedResult.setId(0);
        expectedResult.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        expectedResult.setUserVisitorId(0);
        expectedResult.setIsReserve(0);
        expectedResult.setReserveStatus("reserveStatus");

        // Configure ReserveMapper.getReserveInf(...).
        final Reserve reserve = new Reserve();
        reserve.setId(0);
        reserve.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        reserve.setUserVisitorId(0);
        reserve.setIsReserve(0);
        reserve.setReserveStatus("reserveStatus");
        when(mockReserveMapper.getReserveInf("telephone", 0)).thenReturn(reserve);

        // Run the test
        final Reserve result = reserveServiceImplUnderTest.getReserveInf("telephone", 0);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }
}
