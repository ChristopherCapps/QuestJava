package com.quest.endpoint.cloud.ws.io;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.io.Message;

import java.util.Arrays;
import java.util.Optional;

public class TestIOService implements IOService {

    private Optional<Engine> engine;

    public TestIOService() {
    }

    @Override
    public void setEngine(Engine engine) {
        this.engine = Optional.ofNullable(engine);
    }

    @Override
    public void sendMessage(String channel, Message message) {
        System.out.println("[" + channel + "]: " + message.text());
    }

    @Override
    public void broadcastMessage(Message message, String... channels) {
        final StringBuffer channelsText = new StringBuffer();
        Arrays.stream(channels).forEach(channel -> channelsText.append(channel + ","));
        System.out.println("[" + channelsText.toString() + "]: " + message.text());
    }

}
