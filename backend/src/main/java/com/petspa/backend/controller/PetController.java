package com.petspa.backend.controller;

import com.petspa.backend.dto.request.PetRequest;
import com.petspa.backend.dto.response.PetResponse;
import com.petspa.backend.service.PetService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public PetResponse createPet(@Valid @RequestBody PetRequest request, Authentication authentication) {
        Long customerId = Long.valueOf(authentication.getName());
        return petService.createPet(request, customerId);
    }

    @GetMapping("/{petId}")
    public PetResponse getPetById(@PathVariable Long petId) {
        return petService.getPetById(petId);
    }

    @GetMapping
    public List<PetResponse> getMyPets(Authentication authentication) {
        Long customerId = Long.valueOf(authentication.getName());
        return petService.getPetsByCustomerId(customerId);
    }

    @PutMapping("/{petId}")
    public PetResponse updatePet(@PathVariable Long petId, @Valid @RequestBody PetRequest request) {
        return petService.updatePet(petId, request);
    }

    @DeleteMapping("/{petId}")
    public void deletePet(@PathVariable Long petId) {
        petService.deletePet(petId);
    }
}

