package redmopag.notifier.google_meet_notifier;

import com.google.apps.events.subscriptions.v1.Subscription;
import com.google.auth.Credentials;
import redmopag.notifier.google_meet_notifier.services.EventSubscriber;
import redmopag.notifier.google_meet_notifier.services.SubscriptionListener;
import redmopag.notifier.google_meet_notifier.utils.CredentialsSettings;

import java.util.concurrent.ExecutionException;

public class GoogleMeetApplication {
    private static final CredentialsSettings CREDENTIALS_SETTINGS = new CredentialsSettings();

    private static final String TARGET_RESOURCE = "//meet.googleapis.com/spaces/DT49t30ilt4B";
    private static final String TOPIC_NAME = "projects/meet-notifier-443718/topics/meet-events-topic";

    private static final String SUBSCRIPTION_ID = "meet-events-topic-sub";
    private static final String PROJECT_ID = "meet-notifier-443718";

    public static void main(String[] args) throws Exception {
        // Настройка доступа
        Credentials credentials = CREDENTIALS_SETTINGS.getCredentials();
        EventSubscriber joinedEventSubscriber = new EventSubscriber(credentials);

        // Подписка на событие - присоединение участника на конференцию
        try {
            Subscription response = joinedEventSubscriber.subscribe(TARGET_RESOURCE, TOPIC_NAME);
            System.out.println("Subscription was created");
        } catch (ExecutionException exception) {
            System.out.println("Subscription already exists");
        }

        // Прослушивание и обработка событий
        SubscriptionListener subscriptionListener = new SubscriptionListener(credentials);
        subscriptionListener.listenSubscriptionAsync(PROJECT_ID, SUBSCRIPTION_ID);
    }
}
