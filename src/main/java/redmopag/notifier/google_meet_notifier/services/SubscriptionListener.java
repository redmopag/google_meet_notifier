package redmopag.notifier.google_meet_notifier.services;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import java.io.IOException;
import java.util.HashMap;

public class SubscriptionListener {
    private final static HashMap<String, EventHandler> HANDLERS = new HashMap<>();

    private final Credentials credentials;

    public SubscriptionListener(Credentials credentials) {
        this.credentials = credentials;
        HANDLERS.put("google.workspace.meet.participant.v2.joined",
                new ParticipantJoinedHandler(credentials));
    }

    public void listenSubscriptionAsync(String projectId, String subscriptionId) {
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(projectId, subscriptionId);
        Subscriber subscriber = Subscriber.newBuilder(subscriptionName, getReceiver())
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        // Start thr subscriber
        subscriber.startAsync().awaitRunning();
        System.out.println("Listening for messages on: " + subscriptionName);

        subscriber.awaitTerminated();
    }

    private MessageReceiver getReceiver() {
        return (PubsubMessage message, AckReplyConsumer consumer) -> {
            try {
                if (HANDLERS.containsKey(message.getAttributesMap().get("ce-type"))) {
                    HANDLERS.get(message.getAttributesMap().get("ce-type")).handle(message);
                }
            } catch (IOException e) {
                System.out.println("Не удалось обработать событие: " + e.getMessage());
            }

            consumer.ack();
        };
    }
}
