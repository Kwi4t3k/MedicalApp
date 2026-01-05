package com.example.MedicalApplication.controller;

import com.example.MedicalApplication.model.Medication;
import com.example.MedicalApplication.repository.UserRepository;
import com.example.MedicalApplication.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MedicationPageController {

    private final UserRepository userRepository;
    private final MedicationService medicationService;

    @GetMapping("/medications")
    public String medicationsPage(
            Model model,
            Authentication authentication,
            @RequestParam(required = false) Long id
    ) {
        var user = userRepository.findByEmail(authentication.getName()).orElseThrow();

        var meds = medicationService.getMedicationsForUser(user);

        Medication selected = null;
        if (id != null) {
            selected = meds.stream()
                    .filter(m -> m.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        boolean canEditSelected = selected != null && !selected.isCreatedByDoctor();

        model.addAttribute("welcomeName", user.getFirstName());
        model.addAttribute("userFullName", user.getFullName());

        // TO JEST KLUCZ — bez tego lista jest pusta
        model.addAttribute("medications", meds);

        model.addAttribute("selected", selected);
        model.addAttribute("canEditSelected", canEditSelected);

        return "medications";
    }

    // Dodaj nowy lek (pacjent) — przycisk z lewej
    @PostMapping("/medications/new")
    public String addNewMedication(Authentication authentication) {
        var user = userRepository.findByEmail(authentication.getName()).orElseThrow();

        Medication m = Medication.builder()
                .name("Nowy lek")
                .dose("1 tabletka")
                .status("SCHEDULED")
                .createdByDoctor(false)
                .patient(user)
                .build();

        medicationService.addMedicationByPatient(user, m);

        return "redirect:/medications";
    }

    // Zapis edycji (pacjent) — tylko gdy createdByDoctor=false
    @PostMapping("/medications/save")
    public String saveMedication(
            Authentication authentication,
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String dose,
            @RequestParam(required = false) String intakeTime
    ) {
        var user = userRepository.findByEmail(authentication.getName()).orElseThrow();

        Medication update = new Medication();
        update.setName(name);
        update.setDose(dose);

        // intakeTime jako String "HH:mm" -> parsowanie w serwisie albo tutaj
        update.setIntakeTime(intakeTime == null || intakeTime.isBlank() ? null : java.time.LocalTime.parse(intakeTime));

        medicationService.updateMedicationByPatient(user, id, update);

        return "redirect:/medications?id=" + id;
    }
}