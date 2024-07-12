package com.example.reservedassistance.service.impl;

import com.example.reservedassistance.db.StadiumScores;
import com.example.reservedassistance.mapper.StadiumMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StadiumServiceImplTest {

    @Mock
    private StadiumMapper mockStadiumMapper;

    @InjectMocks
    private StadiumServiceImpl stadiumServiceImplUnderTest;

    @Test
    void testGetScore() {
        // Setup
        final StadiumScores stadiumScores = new StadiumScores();
        stadiumScores.setId(0);
        stadiumScores.setScores(0.0);
        final List<StadiumScores> expectedResult = Arrays.asList(stadiumScores);

        // Configure StadiumMapper.getScore(...).
        final StadiumScores stadiumScores2 = new StadiumScores();
        stadiumScores2.setId(0);
        stadiumScores2.setScores(0.0);
        final List<StadiumScores> stadiumScores1 = Arrays.asList(stadiumScores2);
        when(mockStadiumMapper.getScore()).thenReturn(stadiumScores1);

        // Run the test
        final List<StadiumScores> result = stadiumServiceImplUnderTest.getScore();

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetScore_StadiumMapperReturnsNoItems() {
        // Setup
        when(mockStadiumMapper.getScore()).thenReturn(Collections.emptyList());

        // Run the test
        final List<StadiumScores> result = stadiumServiceImplUnderTest.getScore();

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }
}
