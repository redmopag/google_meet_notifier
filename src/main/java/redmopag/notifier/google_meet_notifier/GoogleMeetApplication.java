package redmopag.notifier.google_meet_notifier;

import com.google.auth.Credentials;
import redmopag.notifier.google_meet_notifier.services.EventSubscriber;
import redmopag.notifier.google_meet_notifier.services.SubscriptionListener;
import redmopag.notifier.google_meet_notifier.utils.CredentialsSettings;
import redmopag.notifier.google_meet_notifier.utils.TargetResourceStore;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class GoogleMeetApplication {
    private static final CredentialsSettings CREDENTIALS_SETTINGS = new CredentialsSettings();

    private static final String TOPIC_NAME = "projects/meet-notifier-443718/topics/meet-events-topic";
    private static final String SUBSCRIPTION_ID = "meet-events-topic-sub";
    private static final String PROJECT_ID = "meet-notifier-443718";

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Настройка доступа
        Credentials credentials = CREDENTIALS_SETTINGS.getCredentials();
        TargetResourceStore targetResourceStore = new TargetResourceStore(credentials);
        EventSubscriber eventSubscriber = new EventSubscriber(credentials);
        SubscriptionListener subscriptionListener = new SubscriptionListener(credentials);

        String targetResource = targetResourceStore.getTargetResource();
        if (targetResource == null) {
            System.out.print("Введите ссылку на конференцию: ");
            targetResource = targetResourceStore.saveTargetResource(scanner.nextLine());
        }

        String choice = null;
        do {
            createSubscription(targetResource, eventSubscriber);
            subscriptionListener.listenSubscriptionAsync(PROJECT_ID, SUBSCRIPTION_ID);

            System.out.print("Нажмите c - изменить конференцию, q - выйти: ");
            choice = scanner.next();
            if (choice.equals("c")) {
                System.out.println("Введите ссылку на новую конференцию: ");
                targetResource = targetResourceStore.saveTargetResource(scanner.nextLine());
                subscriptionListener.stopListeningSubscription();
            }
        } while (!choice.equals("q"));
    }

    private static void createSubscription(String targetResource, EventSubscriber eventSubscriber) throws IOException {
        try {
            eventSubscriber.subscribe(targetResource, TOPIC_NAME);
            System.out.println("Subscription was created");
        } catch (ExecutionException exception) {
            System.out.println("Subscription already exists");
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }
}
