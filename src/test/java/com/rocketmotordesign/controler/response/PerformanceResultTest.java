package com.rocketmotordesign.controler.response;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PerformanceResultTest {

    @Test
    public void neDoitPasIndiquerUnProblemeDeKN() {
        int lowKNCorrection = 200;
        PerformanceResult result = createPerformanceResult(lowKNCorrection);

        assertThat(result.isLowKNCorrection()).isFalse();
    }

    @Test
    public void doitIndiquerUnProblemeDeKN() {
        int lowKNCorrection = 201;
        PerformanceResult result = createPerformanceResult(lowKNCorrection);

        assertThat(result.isLowKNCorrection()).isTrue();
    }

    private PerformanceResult createPerformanceResult(int lowKNCorrection) {
        return new PerformanceResult("description", 1, 2, 3, 4, 5, true, 6, 7, 8, 9.0, 10.0, 11, lowKNCorrection, 12);
    }

}