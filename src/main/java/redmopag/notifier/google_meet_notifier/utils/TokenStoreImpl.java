package redmopag.notifier.google_meet_notifier.utils;

import com.google.auth.oauth2.TokenStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/* Класс для хранения токенов авторизации в файле */
public class TokenStoreImpl implements TokenStore {
    private final String tokens_directory;

    public TokenStoreImpl(String tokens_directory) {
        this.tokens_directory = tokens_directory;
    }

    private Path pathFor(String id){
        return Paths.get(".", tokens_directory, id + ".json");
    }

    // Загрузка файла с токенами
    @Override
    public String load(String id) throws IOException {
        if(!Files.exists(pathFor(id))){
            return null;
        }
        return Files.readString(pathFor(id));
    }

    // Запись токена в файл
    @Override
    public void store(String id, String token) throws IOException {
        Files.createDirectory(Paths.get(".", tokens_directory)); // Сохраняется в корне
        Files.writeString(pathFor(id), token);
    }

    // Удаление токена
    @Override
    public void delete(String id) throws IOException {
        if(!Files.exists(pathFor(id))){
            return;
        }
        Files.delete(pathFor(id));
    }
}
