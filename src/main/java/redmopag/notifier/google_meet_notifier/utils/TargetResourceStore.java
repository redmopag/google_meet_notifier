package redmopag.notifier.google_meet_notifier.utils;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.apps.meet.v2.Space;
import com.google.apps.meet.v2.SpacesServiceClient;
import com.google.apps.meet.v2.SpacesServiceSettings;
import com.google.auth.Credentials;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class TargetResourceStore {
    private final static Path TARGET_RESOURCE_PATH = Paths.get(".", "target-resource");

    private final Credentials credentials;

    public TargetResourceStore(Credentials credentials) {
        this.credentials = credentials;
    }

    public String getTargetResource() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String resourceName = null;

        if (!Files.exists(TARGET_RESOURCE_PATH)) {
            return null;
        } else {
            resourceName = Files.readString(TARGET_RESOURCE_PATH);
        }

        return resourceName;
    }

    private String getResourceName(String meetingCode) throws IOException {
        SpacesServiceSettings settings = SpacesServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        try (SpacesServiceClient client = SpacesServiceClient.create(settings)) {
            Space space = client.getSpace("spaces/" + meetingCode);
            return space.getName();
        }
    }

    private String getMeetingCode (String spaceUri) {
        return spaceUri.substring(spaceUri.lastIndexOf('/') + 1);
    }

    public String saveTargetResource(String spaceUri) throws IOException {
        String resourceName = "//meet.googleapis.com/" + getResourceName(getMeetingCode(spaceUri));
        Files.writeString(TARGET_RESOURCE_PATH, resourceName);

        return resourceName;
    }
}
