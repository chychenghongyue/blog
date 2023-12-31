package com.blog.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

import static com.blog.enums.ZoneEnum.SHANGHAI;

@Configuration
public class GlobalZoneConfig {

    @PostConstruct
    public void setGlobalZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(SHANGHAI.getZone()));
    }

}
