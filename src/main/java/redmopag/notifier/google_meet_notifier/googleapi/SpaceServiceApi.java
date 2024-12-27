package redmopag.notifier.google_meet_notifier.googleapi;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.apps.meet.v2.Space;
import com.google.apps.meet.v2.SpacesServiceClient;
import com.google.apps.meet.v2.SpacesServiceSettings;
import com.google.auth.Credentials;

import java.io.IOException;

public class SpaceServiceApi {
    private final SpacesServiceSettings settings;

    public SpaceServiceApi(SpacesServiceSettings settings) throws IOException {
        this.settings = settings;
    }

    public String getSpaceName(String meetingUri) {
        try (SpacesServiceClient client = SpacesServiceClient.create(settings)) {
            Space space = client.getSpace("spaces/" + getMeetingCode(meetingUri));
            return "//meet.googleapis.com/" + space.getName();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getMeetingCode(String spaceUri) {
        return spaceUri.substring(spaceUri.lastIndexOf('/') + 1);
    }
}
