package com.rocketmotordesign.controler.response;


import com.github.jbgust.jsrm.application.result.MotorClassification;
import com.rocketmotordesign.service.MeasureUnit;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.application.result.PortToThroatAreaWarning.NORMAL;
import static com.rocketmotordesign.controler.response.PerformanceResult.format;
import static org.assertj.core.api.Assertions.assertThat;

public class PerformanceResultTest {

    @Test
    void doitIndiquerLePourcentageDeLaClasse() {
        PerformanceResult result = createPerformanceResult(0);

        assertThat(result.getMotorDescription()).isEqualTo("L1672");
        assertThat(result.getClassPercentage()).isEqualTo(41);
    }

    @Test
    void neDoitPasIndiquerLutilisationDeSafeKN() {
        int lowKNCorrection = 0;
        PerformanceResult result = createPerformanceResult(lowKNCorrection);

        assertThat(result.isLowKNCorrection()).isFalse();
        assertThat(result.isSafeKN()).isFalse();
    }

    @Test
    void neDoitPasIndiquerUnProblemeDeKN() {
        int lowKNCorrection = 100;
        PerformanceResult result = createPerformanceResult(lowKNCorrection);

        assertThat(result.isLowKNCorrection()).isFalse();
        assertThat(result.isSafeKN()).isTrue();
    }

    @Test
    void doitIndiquerUnProblemeDeKN() {
        int lowKNCorrection = 201;
        PerformanceResult result = createPerformanceResult(lowKNCorrection);

        assertThat(result.isLowKNCorrection()).isTrue();
        assertThat(result.isSafeKN()).isTrue();
    }

    @Test
    void shouldFormat() {
        assertThat(format(52.329)).isEqualTo("52.33");
    }

    @Test
    void shouldFormatWithPrecision() {
        assertThat(format(52.329, "%.1f")).isEqualTo("52.3");
    }

    private PerformanceResult createPerformanceResult(int lowKNCorrection) {
        return new PerformanceResult("L1672", 1, 3603.07, 3, 4, 5, true, 6, 7, 8, 9.0, 10.0, 11, lowKNCorrection, 12, MotorClassification.L, MeasureUnit.SI, 3.5D, NORMAL);
    }

}
