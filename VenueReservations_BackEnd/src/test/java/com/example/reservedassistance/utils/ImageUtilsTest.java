package com.example.reservedassistance.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageUtilsTest {

    @Test
    void testIsImage() {
        assertThat(ImageUtils.isImage("filename")).isFalse();
    }
}
