package com.example.reservedassistance.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JudgeUtilsTest {

    @Test
    void testIsValidPhoneNumber() {
        assertThat(JudgeUtils.isValidPhoneNumber("phoneNumber")).isFalse();
    }

    @Test
    void testIsValidIdCardNumber() {
        assertThat(JudgeUtils.isValidIdCardNumber("idCardNumber")).isFalse();
    }
}
