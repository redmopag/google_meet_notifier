package redmopag.notifier.google_meet_notifier.services;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.apps.meet.v2.*;
import com.google.auth.Credentials;
import com.google.pubsub.v1.PubsubMessage;
import redmopag.notifier.google_meet_notifier.googleapi.ConferenceServiceApi;
import redmopag.notifier.google_meet_notifier.utils.NotificationService;
import redmopag.notifier.google_meet_notifier.utils.TrayNotificationService;

import java.io.IOException;

public class ParticipantJoinedHandler implements EventHandler {
    private final ConferenceServiceApi conferenceServiceApi;
    private final NotificationService notificationService;

    public ParticipantJoinedHandler(Credentials credentials) {
        ConferenceRecordsServiceSettings settings = null;
        try {
            settings = ConferenceRecordsServiceSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        conferenceServiceApi = new ConferenceServiceApi(settings);
        notificationService = new TrayNotificationService();
    }

    @Override
    public void handle(PubsubMessage message) {
        Participant participant = conferenceServiceApi.getParticipant(message);
        String displayName = getDisplayName(participant);

        String title = "Google Meet Notifier";
        String text = displayName + " присоединился к конференции";

        notificationService.notify(title, text);
    }

    private String getDisplayName(Participant participant) {
        if (participant.hasAnonymousUser()) {
            return participant.getAnonymousUser().getDisplayName() + " (Anonymous)";
        } else if (participant.hasSignedinUser()) {
            return participant.getSignedinUser().getDisplayName();
        } else if (participant.hasPhoneUser()) {
            return participant.getPhoneUser().getDisplayName() + " (Phone)";
        } else {
            return "Unknown user";
        }
    }
}
