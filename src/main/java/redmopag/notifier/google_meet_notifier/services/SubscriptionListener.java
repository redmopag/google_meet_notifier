package redmopag.notifier.google_meet_notifier.services;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SubscriptionListener {
    private final static HashMap<String, EventHandler> HANDLERS = new HashMap<>();

    static {
        HANDLERS.put("google.workspace.meet.participant.v2.joined", new ParticipantJoinedHandler());
    }

    private final Credentials credentials;

    public SubscriptionListener(Credentials credentials) {
        this.credentials = credentials;
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

        try {
            subscriber.awaitTerminated(60, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            subscriber.stopAsync();
        }
    }

    private MessageReceiver getReceiver() {
        return (PubsubMessage message, AckReplyConsumer consumer) -> {
            // Обработка событий
            HANDLERS.get(message.getAttributesMap().get("ce-type")).handle(message);
            consumer.ack();
        };
    }
}
