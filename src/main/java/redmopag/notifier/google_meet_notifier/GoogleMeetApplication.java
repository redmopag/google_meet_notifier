package redmopag.notifier.google_meet_notifier;

import com.google.apps.events.subscriptions.v1.*;
import com.google.auth.Credentials;
import redmopag.notifier.google_meet_notifier.services.EventSubscriber;
import redmopag.notifier.google_meet_notifier.utils.CredentialsSettings;

public class GoogleMeetApplication {
    private static final CredentialsSettings CREDENTIALS_SETTINGS = new CredentialsSettings();

    private static String targetResource = "//meet.google.com/ajz-ajjo-uxw";
    private static final String TOPIC_NAME = "projects/meet-notifier-443718/topics/meet-events-topic";

    public static void main(String[] args) throws Exception {
        // Настройка доступа
        Credentials credentials = CREDENTIALS_SETTINGS.getCredentials();
        EventSubscriber joinedEventSubscriber = new EventSubscriber(credentials);

        // Подписка на событие - присоединение участника на конференцию
        Subscription response = joinedEventSubscriber.subscribe(targetResource, TOPIC_NAME);
    }
}
