package redmopag.notifier.google_meet_notifier.storage;

import redmopag.notifier.google_meet_notifier.googleapi.SpaceServiceApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TargetResourceStore implements TargetResource {
    private final static Path RESOURCE_FILE_PATH = Paths.get(".", "target-resource");

    private final SpaceServiceApi spaceServiceApi;

    public TargetResourceStore(SpaceServiceApi spaceServiceApi) {
        this.spaceServiceApi = spaceServiceApi;
    }

    @Override
    public String getTargetResource() {
        if (!Files.exists(RESOURCE_FILE_PATH)) {
            return null;
        } else {
            try {
                return Files.readString(RESOURCE_FILE_PATH);
            } catch (IOException e) {
                System.out.println("File nod found: " + RESOURCE_FILE_PATH);
                return null;
            }
        }
    }

    @Override
    public String saveTargetResource(String spaceUri) throws IOException {
        String resourceName = spaceServiceApi.getSpaceName(spaceUri);
        Files.writeString(RESOURCE_FILE_PATH, resourceName);

        return resourceName;
    }
}
