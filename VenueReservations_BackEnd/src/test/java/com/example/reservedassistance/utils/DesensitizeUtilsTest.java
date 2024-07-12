package com.example.reservedassistance.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DesensitizeUtilsTest {

    @Test
    void testDesensitizeName() {
        assertThat(DesensitizeUtils.desensitizeName("name")).isEqualTo("n***");
    }

    @Test
    void testDesensitizeIDCard() {
        assertThat(DesensitizeUtils.desensitizeIDCard("idCard")).isEqualTo("");
    }

    @Test
    void testDesensitizePhoneNumber() {
        assertThat(DesensitizeUtils.desensitizePhoneNumber("phoneNumber")).isEqualTo("pho****mber");
    }
}
