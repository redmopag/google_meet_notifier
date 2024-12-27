package redmopag.notifier.google_meet_notifier.googleapi;

import com.google.apps.meet.v2.ConferenceRecordsServiceClient;
import com.google.apps.meet.v2.ConferenceRecordsServiceSettings;
import com.google.apps.meet.v2.Participant;
import com.google.apps.meet.v2.ParticipantName;
import com.google.gson.JsonParser;
import com.google.pubsub.v1.PubsubMessage;

import java.io.IOException;

public class ConferenceServiceApi {
    private final ConferenceRecordsServiceSettings settings;

    public ConferenceServiceApi(ConferenceRecordsServiceSettings settings) {
        this.settings = settings;
    }

    public Participant getParticipant(PubsubMessage message) {
        try (ConferenceRecordsServiceClient client = ConferenceRecordsServiceClient.create(settings)) {
            String participantSessionName = getParticipantSessionName(message);
            ParticipantName participantName = getParticipantName(participantSessionName);
            return client.getParticipant(participantName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ParticipantName getParticipantName(String participantSessionName) {
        // ParticipantSession looks like: conferenceRecords/XXX/participants/YYY/participantSessions/ZZZ
        String[] participantSessionData = participantSessionName.split("/");
        String conferenceRecord = participantSessionData[1];
        String participant = participantSessionData[3];

        return ParticipantName.newBuilder()
                .setConferenceRecord(conferenceRecord)
                .setParticipant(participant)
                .build();
    }

    private String getParticipantSessionName(PubsubMessage message) {
        return JsonParser.parseString(message.getData().toStringUtf8())
                .getAsJsonObject()
                .getAsJsonObject("participantSession")
                .get("name")
                .getAsString();
    }
}
