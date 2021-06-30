package com.quest.engine;

import com.quest.io.Message;

public interface IOService {
    void setEngine(Engine engine);
    void sendMessage(String channel, Message message);
    void broadcastMessage(Message message, String... channels);
}
