package redmopag.notifier.google_meet_notifier.services;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import java.io.IOException;
import java.util.Map;

public class SubscriptionListener {
    private final Map<String, EventHandler> handlers;
    private final Credentials credentials;
    private Subscriber subscriber;

    public SubscriptionListener(Credentials credentials, Map<String, EventHandler> handlers) {
        this.credentials = credentials;
        this.handlers = handlers;
    }

    public void listenSubscriptionAsync(String projectId, String subscriptionId) {
        subscriber = getSubscriber(projectId, subscriptionId);
        // Start thr subscriber
        subscriber.startAsync().awaitRunning();
        System.out.println("Listening for messages");
    }

    private Subscriber getSubscriber(String projectId, String subscriptionId) {
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(projectId, subscriptionId);
        return Subscriber.newBuilder(subscriptionName, getReceiver())
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();
    }

    private MessageReceiver getReceiver() {
        return (PubsubMessage message, AckReplyConsumer consumer) -> {
            try {
                if (handlers.containsKey(message.getAttributesMap().get("ce-type"))) {
                    handlers.get(message.getAttributesMap().get("ce-type")).handle(message);
                }
            } catch (IOException e) {
                System.out.println("Не удалось обработать событие: " + e.getMessage());
            }

            consumer.ack(); // Сообщаем, что сообщение получено
        };
    }

    public void stopListeningSubscription() {
        if (subscriber != null) {
            subscriber.stopAsync();
        }
    }
}
