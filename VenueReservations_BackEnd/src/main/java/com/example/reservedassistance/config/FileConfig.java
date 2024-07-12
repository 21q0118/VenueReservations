package com.example.reservedassistance.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(value = "file")
@Data
public class FileConfig {

    private String basePath;

    private String prefix;
}
