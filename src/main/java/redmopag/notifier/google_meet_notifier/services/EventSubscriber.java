package redmopag.notifier.google_meet_notifier.services;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.apps.events.subscriptions.v1.*;
import com.google.auth.Credentials;
import com.google.protobuf.Duration;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class EventSubscriber implements Subscriber {
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

    @Override
    public void subscribe(String targetResource, String topicName) throws IOException {
        try (SubscriptionsServiceClient subscriptionsServiceClient = SubscriptionsServiceClient.create(settings)) {
            Subscription subscription = createSubscription(targetResource, topicName);
            CreateSubscriptionRequest request = CreateSubscriptionRequest.newBuilder()
                    .setSubscription(subscription)
                    .build();

            subscriptionsServiceClient.createSubscriptionAsync(request).get();
            System.out.println("Subscription was created");
        } catch (ExecutionException e) {
            System.out.println("Subscription already exists");
        } catch (InterruptedException e) {
            System.out.println("Subscribing failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
