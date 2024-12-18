package redmopag.notifier.google_meet_notifier.services;

import com.google.pubsub.v1.PubsubMessage;

public class ParticipantJoinedHandler implements EventHandler {
    @Override
    public void handle(PubsubMessage message) {

        System.out.println("Data: " + message.getData().toStringUtf8());
    }
}
