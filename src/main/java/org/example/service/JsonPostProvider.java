package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.model.ScheduledPost;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class JsonPostProvider implements PostProvider {
    private final Path filePath;
    private final ObjectMapper mapper;

    public JsonPostProvider(String filePath) {
        this.filePath = Path.of(filePath);
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT); // pretty print
    }

    @Override
    public List<ScheduledPost> getScheduledPosts() {
        try {
            if (!Files.exists(filePath)) {
                return Collections.emptyList();
            }
            return mapper.readValue(
                    filePath.toFile(),
                    new TypeReference<List<ScheduledPost>>() {}
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public void saveScheduledPosts(List<ScheduledPost> posts) {
        try {
            mapper.writeValue(new File(filePath.toString()), posts);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save posts to " + filePath + ": " + e.getMessage(), e);
        }
    }
}
