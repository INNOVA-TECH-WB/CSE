
package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.model.ScheduledPost;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.List;

public class JsonPostProvider implements PostProvider {

    private final String resourcePath;

    public JsonPostProvider(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public List<ScheduledPost> getScheduledPosts() {
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(resourcePath))) {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                    .create();

            Type listType = new TypeToken<List<ScheduledPost>>() {}.getType();
            return gson.fromJson(reader, listType);

        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // empty list
        }
    }
}
