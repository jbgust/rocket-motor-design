package com.rocketmotordesign.controler;

import com.rocketmotordesign.propellant.entity.MeteorPropellant;
import com.rocketmotordesign.propellant.repository.MeteorPropellantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static com.rocketmotordesign.utils.TestHelper.getKNDXFromSRM2014;
import static com.rocketmotordesign.utils.TestHelper.getKNSUFromSRM2014;
import static org.mockito.BDDMockito.given;

public abstract class LegacySRMPropellant {

    public static final UUID KNDX_SRM_2014_UUID = UUID.fromString("0c748178-d36d-4a31-959c-a584934dbcde");
    public static final UUID KNSU_SRM_2014_UUID = UUID.fromString("c984f82a-e519-4492-98d4-35866fdf3c69");
    @MockBean
    protected MeteorPropellantRepository propellantRepository;
    protected MeteorPropellant kndxSRM2014;
    protected MeteorPropellant knsuSRM2014;

    @BeforeEach
    void initLegacyPropellant() {
        kndxSRM2014 = getKNDXFromSRM2014();
        kndxSRM2014.setId(KNDX_SRM_2014_UUID);
        given(propellantRepository.findById(kndxSRM2014.getId()))
                .willReturn(Optional.of(kndxSRM2014));

        knsuSRM2014 = getKNSUFromSRM2014();
        knsuSRM2014.setId(KNSU_SRM_2014_UUID);
        given(propellantRepository.findById(knsuSRM2014.getId()))
                .willReturn(Optional.of(knsuSRM2014));
    }
}
