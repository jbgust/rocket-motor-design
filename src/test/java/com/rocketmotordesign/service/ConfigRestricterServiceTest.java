package com.rocketmotordesign.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rocketmotordesign.controler.request.BasicComputationRequest;
import com.rocketmotordesign.controler.request.FinocylComputationRequest;
import com.rocketmotordesign.controler.request.StarGrainComputationRequest;
import com.rocketmotordesign.utils.TestHelper;

class ConfigRestricterServiceTest {

    private ConfigRestricterService configRestricterService;

    @BeforeEach
    void setUp() {
        configRestricterService = new ConfigRestricterService(2, 3, 4);
    }

    @Test
    void doitLimiterLeNombreDeResultatPourFinocyl() throws UnauthorizedValueException {
        BasicComputationRequest request = TestHelper.getDefaultFinocylRequest();
        configRestricterService.applyRestriction(request);

        assertThat(request.getExtraConfig().getNumberOfCalculationLine()).isEqualTo(2);
    }

    @Test
    void doitLimiterLeNombreDeResultatPourStar() throws UnauthorizedValueException {
        StarGrainComputationRequest request = TestHelper.getDefaultStarGrainRequest();
        request.setPointCount(3);
        configRestricterService.applyRestriction(request);

        assertThat(request.getExtraConfig().getNumberOfCalculationLine()).isEqualTo(3);
    }

    @Test
    void doitLimiterLeNombreBranchePourStar() {
        StarGrainComputationRequest request = TestHelper.getDefaultStarGrainRequest();

        assertThatThrownBy(() -> configRestricterService.applyRestriction(request))
                .isInstanceOf(UnauthorizedValueException.class)
                .hasMessage("Due to performance issue on METEOR, you can't use more than 4 branches on star grain.");
    }
}