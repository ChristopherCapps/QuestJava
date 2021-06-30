package com.quest.endpoint.cloud.ws.io.twilio;

import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Media;
import com.twilio.twiml.messaging.Message;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@RestController
public class TwilioController {

    private final TwilioIOService ioService;

    public TwilioController(final TwilioIOService ioService) {
        this.ioService = requireNonNull(ioService);
    }

    @PostMapping(value="/", produces = "application/xml")
    @ResponseBody
    public String handleSmsWebhook(
            @RequestParam("From") String from,
            @RequestParam("Body") String body) {

        return ioService.getEngine().onMessageReceived(from, body)
        .map(message -> {
            final Message.Builder messageBuilder = new Message.Builder(message.text());
            if (message.photoUrl() != null) {
                messageBuilder.media(new Media.Builder(message.photoUrl()).build());
            }
            return new MessagingResponse.Builder().message(messageBuilder.build()).build().toXml();
        })
                .orElse("");
    }
}
