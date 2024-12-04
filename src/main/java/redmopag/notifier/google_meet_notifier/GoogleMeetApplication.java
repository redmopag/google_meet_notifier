package redmopag.notifier.google_meet_notifier;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import redmopag.notifier.google_meet_notifier.utils.CredentialsSettings;
import com.google.apps.meet.v2.CreateSpaceRequest;
import com.google.apps.meet.v2.Space;
import com.google.apps.meet.v2.SpacesServiceClient;
import com.google.apps.meet.v2.SpacesServiceSettings;

import java.io.IOException;

public class GoogleMeetApplication {
    private static final CredentialsSettings CREDENTIALS_SETTINGS = new CredentialsSettings();

    public static void main(String[] args) throws Exception {
        Credentials credentials = CREDENTIALS_SETTINGS.getCredentials();
        SpacesServiceSettings settings = SpacesServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        try (SpacesServiceClient spacesServiceClient = SpacesServiceClient.create(settings)) {
            CreateSpaceRequest request = CreateSpaceRequest.newBuilder()
                    .setSpace(Space.newBuilder().build())
                    .build();
            Space response = spacesServiceClient.createSpace(request);
            System.out.println("Space created: " + response.getMeetingUri());
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
