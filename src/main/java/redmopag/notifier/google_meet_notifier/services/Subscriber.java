package redmopag.notifier.google_meet_notifier.services;

import java.io.IOException;

public interface Subscriber {
    void subscribe(String targetResource, String topicName) throws IOException;
}
