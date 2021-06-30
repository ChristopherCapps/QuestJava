package com.quest.endpoint.cloud.ws.io.twilio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Bean
    public TwilioIOService twilioIOService() {
        return new TwilioIOService();
    }
}
