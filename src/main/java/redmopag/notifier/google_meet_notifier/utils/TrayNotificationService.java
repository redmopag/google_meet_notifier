package redmopag.notifier.google_meet_notifier.utils;

import java.awt.*;

public class TrayNotificationService implements NotificationService {
    private final SystemTray systemTray;
    private final TrayIcon trayIcon;

    public TrayNotificationService() {
        systemTray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img.png"));
        if (image == null) {
            throw new RuntimeException("Image not found");
        }
        trayIcon = new TrayIcon(image);
    }

    @Override
    public void notify(String title, String message) {
        if (SystemTray.isSupported()) {

            try {
                systemTray.add(trayIcon);
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }

            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
            systemTray.remove(trayIcon);
        } else {
            System.out.println("SystemTray not supported");
        }
    }
}
