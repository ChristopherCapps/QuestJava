package com.quest.endpoint.cloud.ws.io.twilio;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;

import java.util.Arrays;

public class TwilioIOService implements IOService {
    private final static String ACCOUNT_SID = "AC390db326d4b244be990728560c12e1fc";
    private final static String AUTH_TOKEN = "ef7b535c6370440686c56464dcc5c05c";

    private final static String QUEST_PHONE_NUMBER = "19193357806";

    private Engine engine;

    TwilioIOService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    @Override
    public void setEngine(final Engine engine) {
        this.engine = engine;
    }

    public Engine getEngine() {
        return engine;
    }

    @Override
    public void sendMessage(final String channel, final com.quest.io.Message message) {
        final MessageCreator creator = Message.creator(
                new PhoneNumber(channel),
                new PhoneNumber(QUEST_PHONE_NUMBER),
                message.text());
        if (message.photoUrl() != null) {
            creator.setMediaUrl(message.photoUrl());
        }
        creator.create();
    }

    @Override
    public void broadcastMessage(final com.quest.io.Message message, final String... channels) {
        Arrays.stream(channels).sequential().forEach(channel -> sendMessage(channel, message));
    }

    public static void main(String[] args) {
        (new TwilioIOService()).sendMessage("19198892572", com.quest.io.Message.newBuilder().append("Howdy!").build());
    }
}
