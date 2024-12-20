package redmopag.notifier.google_meet_notifier.services;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.apps.events.subscriptions.v1.*;
import com.google.auth.Credentials;
import com.google.protobuf.Duration;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class EventSubscriber {
    private final SubscriptionsServiceSettings settings;

    public EventSubscriber(Credentials credentials) throws IOException {
        settings = SubscriptionsServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();
    }

    private Subscription createSubscription(String targetResource, String topicName) {
        return Subscription.newBuilder()
                .setTargetResource(targetResource) // настройка отслеживаемого ресурса
                .addEventTypes("google.workspace.meet.participant.v2.joined") // тип события
                // будут приходить в topic Pub/Sub
                .setNotificationEndpoint(NotificationEndpoint.newBuilder()
                        .setPubsubTopic(topicName)
                        .build())
                .setPayloadOptions(PayloadOptions.newBuilder()
                        .setIncludeResource(false)
                        .build())
                .setTtl(Duration.newBuilder()
                        .setSeconds(86400)
                        .build())
                .build();
    }

    public Subscription subscribe(String targetResource, String topicName) throws IOException, ExecutionException, InterruptedException {
        try (SubscriptionsServiceClient subscriptionsServiceClient = SubscriptionsServiceClient.create(settings)) {
            Subscription subscription = createSubscription(targetResource, topicName);
            CreateSubscriptionRequest request = CreateSubscriptionRequest.newBuilder()
                    .setSubscription(subscription)
                    .build();

            return subscriptionsServiceClient.createSubscriptionAsync(request).get();
        }
    }
}
