package redmopag.notifier.google_meet_notifier;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.apps.meet.v2.SpacesServiceSettings;
import com.google.auth.Credentials;
import com.google.auth.oauth2.TokenStore;
import redmopag.notifier.google_meet_notifier.googleapi.SpaceServiceApi;
import redmopag.notifier.google_meet_notifier.services.*;
import redmopag.notifier.google_meet_notifier.storage.TokenStoreImpl;
import redmopag.notifier.google_meet_notifier.utils.CredentialsSettings;
import redmopag.notifier.google_meet_notifier.storage.TargetResourceStore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GoogleMeetApplication {
    private static final String TOPIC_NAME = "projects/meet-notifier-443718/topics/meet-events-topic";
    private static final String SUBSCRIPTION_ID = "meet-events-topic-sub";
    private static final String PROJECT_ID = "meet-notifier-443718";
    private static final String TOKENS_DIRECTORY = "tokens";
    private static final List<String> SCOPES = List.of(
            "https://www.googleapis.com/auth/meetings.space.created",
            "https://www.googleapis.com/auth/meetings.space.readonly",
            "https://www.googleapis.com/auth/pubsub"
    );

    private static final String CHANGE_CONFERENCE = "c";
    private static final String EXIT = "q";

    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            Credentials credentials = getCredentials();

            // Получение конференции, события которой нужно слушать и обрабатывать
            TargetResourceStore targetResourceStore = new TargetResourceStore(getSpaceServiceApi(credentials));
            String targetResource = targetResourceStore.getTargetResource();

            // Подписка на события
            Subscriber eventSubscriber = new EventSubscriber(credentials);

            // Настройка обработчика событий
            SubscriptionListener subscriptionListener = getSubscriptionListener(credentials);

            // Если конференцию до этого не вводилась - вводим
            if (targetResource == null) {
                System.out.print("Введите ссылку на конференцию: ");
                targetResource = targetResourceStore.saveTargetResource(scanner.nextLine());
            }

            String choice = null;
            do {
                eventSubscriber.subscribe(targetResource, TOPIC_NAME);
                subscriptionListener.listenSubscriptionAsync(PROJECT_ID, SUBSCRIPTION_ID);

                choice = getUserChoice(scanner);
                if (choice.equals("c")) { // Возможность сменить конференцию
                    System.out.print("Введите ссылку на новую конференцию: ");
                    targetResource = targetResourceStore.saveTargetResource(scanner.nextLine());
                    subscriptionListener.stopListeningSubscription();
                }
            } while (!choice.equals("q"));
        }
    }

    private static Credentials getCredentials() {
        TokenStore tokenStore = new TokenStoreImpl(TOKENS_DIRECTORY);
        CredentialsSettings credentialsSettings = new CredentialsSettings(tokenStore, SCOPES);
        try {
            return credentialsSettings.getCredentials();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static SpaceServiceApi getSpaceServiceApi(Credentials credentials) {
        try {
            SpacesServiceSettings settings = SpacesServiceSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();
            return new SpaceServiceApi(settings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static SubscriptionListener getSubscriptionListener(Credentials credentials) {
        Map<String, EventHandler> handlers = new HashMap<>();
        handlers.put("google.workspace.meet.participant.v2.joined", new ParticipantJoinedHandler(credentials));
        return new SubscriptionListener(credentials, handlers);
    }

    private static String getUserChoice(Scanner scanner) {
        String choice;
        do {
            System.out.print("Нажмите " + CHANGE_CONFERENCE + " - изменить конференцию, "
                    + EXIT + " - выйти: ");
            choice = scanner.nextLine();
        } while (!choice.equals("c") && !choice.equals("q"));

        return choice;
    }
}
