package com.example.demo.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Loads cses-tasks.json (id, name, topic, acceptance per CSES task) once
 * at class-init time and exposes task id -> name lookups. nameFor falls
 * back to "CSES Task {id}" for any id not in the file, so a missing or
 * incomplete file never blocks markProblemAsSolved.
 */
public final class CsesTaskNames {

    private CsesTaskNames() {}

    private record CsesTask(String name, String topic, int id, double acceptance) {}

    private static final Map<String, String> NAMES = load();

    public static String nameFor(String taskId) {
        return NAMES.getOrDefault(taskId, "CSES Task " + taskId);
    }

    private static Map<String, String> load() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = CsesTaskNames.class
                .getClassLoader()
                .getResourceAsStream("cses-tasks.json")) {

            if (in == null) {
                return Map.of(); // file not added yet
            }

            List<CsesTask> tasks =
                    mapper.readValue(in, new TypeReference<List<CsesTask>>() {});

            return tasks.stream().collect(Collectors.toMap(
                    t -> String.valueOf(t.id()),
                    CsesTask::name,
                    (a, b) -> a // keep the first if a duplicate id ever shows up
            ));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load cses-tasks.json", e);
        }
    }
}