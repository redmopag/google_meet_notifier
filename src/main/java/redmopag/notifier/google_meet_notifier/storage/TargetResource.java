package redmopag.notifier.google_meet_notifier.storage;

import java.io.IOException;

public interface TargetResource {
    String getTargetResource() throws IOException;

    String saveTargetResource(String spaceUri) throws IOException;
}
