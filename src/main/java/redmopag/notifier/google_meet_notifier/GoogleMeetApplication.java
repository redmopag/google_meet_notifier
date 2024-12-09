package redmopag.notifier.google_meet_notifier;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.apps.events.subscriptions.v1.*;
import com.google.auth.Credentials;
import com.google.protobuf.Duration;
import redmopag.notifier.google_meet_notifier.utils.CredentialsSettings;
import com.google.apps.meet.v2.CreateSpaceRequest;
import com.google.apps.meet.v2.Space;
import com.google.apps.meet.v2.SpacesServiceClient;
import com.google.apps.meet.v2.SpacesServiceSettings;

import java.io.IOException;

public class GoogleMeetApplication {
    private static final CredentialsSettings CREDENTIALS_SETTINGS = new CredentialsSettings();

    private static String targetResource = "//meet.google.com/ajz-ajjo-uxw";
    private static String topicName = "projects/meet-notifier-443718/topics/meet-events-topic";

    public static void main(String[] args) throws Exception {
        // Настройка доступа
        Credentials credentials = CREDENTIALS_SETTINGS.getCredentials();
        SubscriptionsServiceSettings settings = SubscriptionsServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        Subscription subscription = Subscription.newBuilder()
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

        try (SubscriptionsServiceClient subscriptionsServiceClient = SubscriptionsServiceClient.create(settings)) {
            CreateSubscriptionRequest request = CreateSubscriptionRequest.newBuilder()
                    .setSubscription(subscription)
                    .build();

            Subscription response = subscriptionsServiceClient.createSubscriptionAsync(request).get();
        }
    }
}
