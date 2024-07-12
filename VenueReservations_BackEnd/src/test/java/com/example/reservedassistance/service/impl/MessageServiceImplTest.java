package com.example.reservedassistance.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageServiceImplTest {

    private MessageServiceImpl messageServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        messageServiceImplUnderTest = new MessageServiceImpl();
    }

    @Test
    void testManagerReverseActivityMethod() {
        assertThat(messageServiceImplUnderTest.managerReverseActivityMethod("realName", "activityName", "stadiumName",
                "reason"));
    }

    @Test
    void testAutoRemindMessagingUser() {
        assertThat(messageServiceImplUnderTest.autoRemindMessagingUser("realName", 0L, "activityName",
                "stadiumName"));
    }

    @Test
    void testNotCheckMessagingUser() {
        assertThat(messageServiceImplUnderTest.notCheckMessagingUser("realName", "activityName", "stadiumName",
                0));
    }

    @Test
    void testCheckMessagingUser() {
        assertThat(messageServiceImplUnderTest.checkMessagingUser("realName", "activityName", "stadiumName"));
    }
}
