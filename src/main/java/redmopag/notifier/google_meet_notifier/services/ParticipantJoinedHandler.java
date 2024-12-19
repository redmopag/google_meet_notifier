package redmopag.notifier.google_meet_notifier.services;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.apps.meet.v2.*;
import com.google.auth.Credentials;
import com.google.gson.JsonParser;
import com.google.pubsub.v1.PubsubMessage;

import java.awt.*;
import java.io.IOException;

public class ParticipantJoinedHandler implements EventHandler {
    private final Credentials credentials;

    public ParticipantJoinedHandler(Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public void handle(PubsubMessage message) throws IOException {
        String participantSessionName = JsonParser.parseString(message.getData().toStringUtf8())
                .getAsJsonObject()
                .getAsJsonObject("participantSession")
                .get("name")
                .getAsString();

        Participant participant = getParticipant(participantSessionName);
        String displayName = getDisplayName(participant);

        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();

            Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img.png"));
            TrayIcon trayIcon = new TrayIcon(image);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }

            trayIcon.displayMessage("Уведомление Google Meet",
                    displayName + "присоединился к конеференции",
                    TrayIcon.MessageType.INFO);
        } else {
            System.out.println("SystemTray not supported");
        }
    }

    private Participant getParticipant(String participantSessionName) throws IOException {
        ConferenceRecordsServiceSettings settings = ConferenceRecordsServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        // ParticipantSession looks like: conferenceRecords/XXX/participants/YYY/participantSessions/ZZZ
        String[] participantSessionData = participantSessionName.split("/");
        String conferenceRecord = participantSessionData[1];
        String participant = participantSessionData[3];

        try (ConferenceRecordsServiceClient client = ConferenceRecordsServiceClient.create(settings)) {
            ParticipantName participantName = ParticipantName.newBuilder()
                    .setConferenceRecord(conferenceRecord)
                    .setParticipant(participant)
                    .build();

            return client.getParticipant(participantName);
        }
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
