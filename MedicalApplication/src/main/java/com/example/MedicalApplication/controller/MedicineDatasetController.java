package com.example.MedicalApplication.controller;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medicines")
public class MedicineDatasetController {

    // Trzymamy nazwy w pamięci (szybko). Ładujemy raz.
    private volatile List<String> namesCache = null;

    @GetMapping(value = "/suggest", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> suggest(
            @RequestParam("q") String q,
            @RequestParam(value = "limit", defaultValue = "50") int limit
    ) {
        String query = (q == null) ? "" : q.trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) return List.of();

        List<String> names = getNames();

        int safeLimit = Math.max(1, Math.min(limit, 200));

        // Filtr: startsWith (jak pisałaś: "au" => "au...")
        // Jeśli chcesz "contains", zamień startsWith na contains
        return names.stream()
                .filter(n -> n.toLowerCase(Locale.ROOT).startsWith(query))
                .limit(safeLimit)
                .collect(Collectors.toList());
    }

    private List<String> getNames() {
        if (namesCache != null) return namesCache;

        synchronized (this) {
            if (namesCache != null) return namesCache;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(
                            getClass().getClassLoader().getResourceAsStream("data/medicine_dataset.csv"),
                            "Nie znaleziono pliku: resources/data/medicine_dataset.csv"
                    ),
                    StandardCharsets.UTF_8
            ))) {
                CSVParser parser = CSVFormat.DEFAULT
                        .builder()
                        .setHeader()              // weź nagłówek z pierwszego wiersza
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(br);

                Set<String> set = new LinkedHashSet<>();
                for (CSVRecord r : parser) {
                    String name = r.get("name"); // kolumna "name" z CSV
                    if (name != null) {
                        String trimmed = name.trim();
                        if (!trimmed.isBlank()) set.add(trimmed);
                    }
                }

                List<String> list = new ArrayList<>(set);
                list.sort(String.CASE_INSENSITIVE_ORDER);

                namesCache = list;
                return namesCache;

            } catch (Exception e) {
                // możesz sobie dodać logowanie e, jeśli chcesz
                namesCache = List.of();
                return namesCache;
            }
        }
    }
}
