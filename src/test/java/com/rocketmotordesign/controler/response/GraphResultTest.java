package com.rocketmotordesign.controler.response;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GraphResultTest {

    @Test
    void doitConvertirAuFormatDuFrontend() {
        assertThat(GraphResult.toFrontendPrecision(0.12345)).isEqualTo(0.1235);
    }

}
