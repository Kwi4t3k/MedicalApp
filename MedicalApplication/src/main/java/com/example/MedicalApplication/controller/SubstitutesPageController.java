package com.example.MedicalApplication.controller;

import com.example.MedicalApplication.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SubstitutesPageController {

    private static final String RECENT_SEARCHES_SESSION_KEY = "recentSubstituteSearches";
    private static final int MAX_RECENT_SEARCHES = 10;

    private final UserRepository userRepository;

    private volatile List<String> medicinesCache = null;
    private volatile Map<String, List<String>> substitutesCache = null;

    @GetMapping("/substitutes")
    public String substitutesPage(
            @RequestParam(value = "name", required = false) String name,
            Authentication authentication,
            Model model,
            HttpSession session
    ) {
        var user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        model.addAttribute("welcomeName", user.getFirstName());

        Map<String, List<String>> substitutesMap = getSubstitutesMap();

        String selectedName = normalize(name);
        List<String> selectedSubstitutes = List.of();

        if (selectedName != null && !selectedName.isBlank()) {
            final String lookupName = selectedName;

            selectedSubstitutes = substitutesMap.entrySet().stream()
                    .filter(e -> e.getKey().equalsIgnoreCase(lookupName))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(List.of());

            selectedName = substitutesMap.keySet().stream()
                    .filter(k -> k.equalsIgnoreCase(lookupName))
                    .findFirst()
                    .orElse(lookupName);

            addRecentSearch(session, selectedName);
        }

        model.addAttribute("selectedName", selectedName);
        model.addAttribute("selectedSubstitutes", selectedSubstitutes);
        model.addAttribute("recentSearches", getRecentSearches(session));

        return "substitutes";
    }

    @ResponseBody
    @GetMapping(value = "/api/substitutes/suggest", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> suggest(
            @RequestParam("q") String q,
            @RequestParam(value = "limit", defaultValue = "20") int limit
    ) {
        String query = q == null ? "" : q.trim().toLowerCase();
        if (query.isEmpty()) {
            return List.of();
        }

        List<String> names = getMedicines();

        return names.stream()
                .filter(n -> n.toLowerCase().startsWith(query))
                .limit(Math.max(1, Math.min(limit, 50)))
                .collect(Collectors.toList());
    }

    private void addRecentSearch(HttpSession session, String medicineName) {
        if (medicineName == null || medicineName.isBlank()) {
            return;
        }

        List<String> recent = getRecentSearches(session);

        recent.removeIf(item -> item.equalsIgnoreCase(medicineName));
        recent.add(0, medicineName);

        if (recent.size() > MAX_RECENT_SEARCHES) {
            recent = new ArrayList<>(recent.subList(0, MAX_RECENT_SEARCHES));
        }

        session.setAttribute(RECENT_SEARCHES_SESSION_KEY, recent);
    }

    @SuppressWarnings("unchecked")
    private List<String> getRecentSearches(HttpSession session) {
        Object value = session.getAttribute(RECENT_SEARCHES_SESSION_KEY);
        if (value instanceof List<?>) {
            return new ArrayList<>((List<String>) value);
        }
        return new ArrayList<>();
    }

    private List<String> getMedicines() {
        ensureLoaded();
        return medicinesCache;
    }

    private Map<String, List<String>> getSubstitutesMap() {
        ensureLoaded();
        return substitutesCache;
    }

    private void ensureLoaded() {
        if (medicinesCache != null && substitutesCache != null) {
            return;
        }

        synchronized (this) {
            if (medicinesCache != null && substitutesCache != null) {
                return;
            }

            List<String> medicines = new ArrayList<>();
            Map<String, List<String>> substitutesMap = new LinkedHashMap<>();

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            Objects.requireNonNull(
                                    getClass().getClassLoader().getResourceAsStream("data/medicine_dataset.csv"),
                                    "Nie znaleziono pliku data/medicine_dataset.csv"
                            ),
                            StandardCharsets.UTF_8
                    )
            )) {
                CSVParser parser = CSVFormat.DEFAULT
                        .builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(br);

                Set<String> uniqueMedicines = new LinkedHashSet<>();

                for (CSVRecord record : parser) {
                    String medicineName = normalize(record.get("name"));
                    if (medicineName == null) {
                        continue;
                    }

                    uniqueMedicines.add(medicineName);

                    List<String> substitutes = new ArrayList<>();
                    for (int i = 0; i <= 4; i++) {
                        String value = normalize(record.get("substitute" + i));
                        if (value != null && !substitutes.contains(value)) {
                            substitutes.add(value);
                        }
                    }

                    substitutesMap.putIfAbsent(medicineName, substitutes);
                }

                medicines.addAll(uniqueMedicines);
                medicines.sort(String.CASE_INSENSITIVE_ORDER);

                this.medicinesCache = medicines;
                this.substitutesCache = substitutesMap;

            } catch (Exception e) {
                e.printStackTrace();
                this.medicinesCache = List.of();
                this.substitutesCache = Map.of();
            }
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}