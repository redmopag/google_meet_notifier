package redmopag.notifier.google_meet_notifier.services;

import com.google.pubsub.v1.PubsubMessage;

public interface EventHandler {
    void handle(PubsubMessage message);
}
