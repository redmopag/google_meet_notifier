package redmopag.notifier.google_meet_notifier.services;

import com.google.pubsub.v1.PubsubMessage;

import java.io.IOException;

public interface EventHandler {
    void handle(PubsubMessage message) throws IOException;
}
