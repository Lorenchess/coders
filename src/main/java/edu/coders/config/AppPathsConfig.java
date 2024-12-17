package edu.coders.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.paths")
@Getter
@Setter
public class AppPathsConfig {

    private String lessonsDir;
    private String quizzesDir;
}
