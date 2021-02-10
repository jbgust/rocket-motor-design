package com.rocketmotordesign;

import com.rocketmotordesign.config.MeteorRequestLoggingFilter;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableEncryptableProperties
public class RocketMotorDesignApplication {

	public static void main(String[] args) {
		SpringApplication.run(RocketMotorDesignApplication.class, args);
	}

	@Bean
	public CommonsRequestLoggingFilter buildReauqetLogger() {
		MeteorRequestLoggingFilter loggingFilter = new MeteorRequestLoggingFilter();
		loggingFilter.setIncludeQueryString(true);
		loggingFilter.setIncludePayload(true);
		loggingFilter.setMaxPayloadLength(1200);
		loggingFilter.setIncludeClientInfo(false);
		return loggingFilter;
	}

}

