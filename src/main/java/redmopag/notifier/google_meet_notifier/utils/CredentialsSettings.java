package redmopag.notifier.google_meet_notifier.utils;

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ClientId;
import com.google.auth.oauth2.DefaultPKCEProvider;
import com.google.auth.oauth2.TokenStore;
import com.google.auth.oauth2.UserAuthorizer;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class CredentialsSettings {
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String USER = "me";

    private final List<String> scopes;
    private final TokenStore tokenStore;

    public CredentialsSettings(TokenStore tokenStore, List<String> scopes) {
        this.tokenStore = tokenStore;
        this.scopes = scopes;
    }

    private UserAuthorizer getUserAuthorizer(URI callbackUri) throws IOException {
        // Считывание полномочий (авторизованных или нет)
        try (InputStream in = CredentialsSettings.class.getResourceAsStream(CREDENTIALS_FILE_PATH)) {
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
            }

            ClientId clientId = ClientId.fromStream(in);

            // Авторизация пользователя
            return UserAuthorizer.newBuilder()
                    .setClientId(clientId)
                    .setCallbackUri(callbackUri)
                    .setScopes(scopes)
                    .setPKCEProvider(new DefaultPKCEProvider() {
                        @Override
                        public String getCodeChallenge() {
                            return super.getCodeChallenge().split("=")[0];
                        }
                    })
                    .setTokenStore(tokenStore)
                    .build();
        }
    }

    public Credentials getCredentials() throws Exception {
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().build();

        try {
            // Путь, куда должен прийти ответ от Google с токенами
            URI callbackUri = URI.create(receiver.getRedirectUri());
            UserAuthorizer userAuthorizer = getUserAuthorizer(callbackUri);

            // Получаем полномочия
            Credentials credentials = userAuthorizer.getCredentials(USER);
            if (credentials != null) {
                return credentials;
            }

            // Если полномочия не авторизованы, то их нужно авторизовать. Вернуться токены на указанный путь
            URL authorizationUrl = userAuthorizer.getAuthorizationUrl(USER, "", null);
            if (Desktop.isDesktopSupported() &&
                    Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(authorizationUrl.toURI());
            } else {
                System.out.printf("Open the following URL to authorize access: %s\n",
                        authorizationUrl.toExternalForm());
            }

            String code = receiver.waitForCode();
            credentials = userAuthorizer.getAndStoreCredentialsFromCode(USER, code, callbackUri);

            return credentials;
        } finally {
            receiver.stop();
        }
    }
}
