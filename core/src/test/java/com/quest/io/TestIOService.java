package com.quest.io;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public class TestIOService implements IOService {

    static final Logger logger = LoggerFactory.getLogger(TestIOService.class);

    private Optional<Engine> engine;

    public TestIOService() {
    }

    @Override
    public void setEngine(Engine engine) {
        this.engine = Optional.ofNullable(engine);
    }

    @Override
    public void sendMessage(String channel, Message message) {
        logger.info("IOService: Sending message to {}: {}", channel, message.text());
    }

    @Override
    public void broadcastMessage(Message message, String... channels) {
        final StringBuffer channelsText = new StringBuffer();
        Arrays.stream(channels).sequential().forEach(channel -> sendMessage(channel, message));
    }

}
