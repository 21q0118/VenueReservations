package com.example.reservedassistance.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MatchTemplateUtilsTest {

    @Test
    void testMatchesAny() {
        assertThat(MatchTemplateUtils.matchesAny("input")).isFalse();
    }
}
