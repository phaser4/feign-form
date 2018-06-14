package net.phaser4.feign;

import feign.Logger;
import feign.codec.Encoder;
import org.springframework.context.annotation.Bean;

public class TestClientConfiguration {
    @Bean
    public Encoder encoder() {
        return new FormEncoder();
    }

    @Bean
    public Logger.Level feignLoggerLevel() { return Logger.Level.FULL; }
}
