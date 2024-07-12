package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.entity.User;
import com.example.reservedassistance.entity.Visitor;
import com.example.reservedassistance.mapper.UserMapper;
import com.example.reservedassistance.mapper.VisitorMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitorServiceImplTest {

    @Mock
    private VisitorMapper mockVisitorMapper;
    @Mock
    private UserMapper mockUserMapper;

    @InjectMocks
    private VisitorServiceImpl visitorServiceImplUnderTest;

    @Test
    void testGetUserId() {
        // Setup
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
        user.setEmail("email");
        when(mockUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(user);

        // Run the test
        final Integer result = visitorServiceImplUnderTest.getUserId(0);

        // Verify the results
        assertThat(result).isEqualTo(0);
    }

    @Test
    void testGetUserId_UserMapperReturnsNull() {
        // Setup
        // Configure VisitorMapper.selectById(...).
        final Visitor visitor = new Visitor();
        visitor.setId(0);
        visitor.setRealName("realName");
        visitor.setIdentificationNum("identificationNum");
        visitor.setTelephone("telephone");
        visitor.setKeyValue("keyValue");
        when(mockVisitorMapper.selectById(0)).thenReturn(visitor);

        when(mockUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // Run the test
        final Integer result = visitorServiceImplUnderTest.getUserId(0);

        // Verify the results
        assertThat(result).isNull();
    }
}
