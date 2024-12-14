package redmopag.notifier.google_meet_notifier.services;

import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.AckReplyConsumer;

public class SubscriptionListener {
    public void listenSubscriptionAsync(String projectId, String subscriptionId) {
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(projectId, subscriptionId);

        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
            String eventType = message.getAttributesMap().get("ce-type");
            // Handle incoming message, then ack the received message.
            System.out.println("Id: " + message.getMessageId());
            System.out.println("Data: " + message.getData().toStringUtf8());
            consumer.ack();
        };
    }

    private void onParticipantJoined (PubsubMessage message) {

    }
}
