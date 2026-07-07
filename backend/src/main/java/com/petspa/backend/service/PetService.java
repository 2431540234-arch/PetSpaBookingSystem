package com.petspa.backend.service;

import com.petspa.backend.dto.request.PetRequest;
import com.petspa.backend.dto.response.PetResponse;
import com.petspa.backend.entity.Pet;
import com.petspa.backend.exception.ResourceNotFoundException;
import com.petspa.backend.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public PetResponse createPet(PetRequest request, Long customerId) {
        Pet pet = new Pet();
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setGender(request.getGender());
        pet.setAge(request.getAge());
        pet.setWeight(request.getWeight());
        pet.setAllergies(request.getAllergies());
        pet.setMedicalHistory(request.getMedicalHistory());
        pet.setNotes(request.getNotes());
        pet.setEmoji(request.getEmoji());
        pet.setImageUrl(request.getImageUrl());
        pet.setCustomerId(customerId);

        Pet savedPet = petRepository.save(pet);
        return convertToResponse(savedPet);
    }

    public PetResponse getPetById(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));
        return convertToResponse(pet);
    }

    public List<PetResponse> getPetsByCustomerId(Long customerId) {
        List<Pet> pets = petRepository.findByCustomerId(customerId);
        return pets.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public PetResponse updatePet(Long petId, PetRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (request.getName() != null) pet.setName(request.getName());
        if (request.getSpecies() != null) pet.setSpecies(request.getSpecies());
        if (request.getBreed() != null) pet.setBreed(request.getBreed());
        if (request.getGender() != null) pet.setGender(request.getGender());
        if (request.getAge() != null) pet.setAge(request.getAge());
        if (request.getWeight() != null) pet.setWeight(request.getWeight());
        if (request.getAllergies() != null) pet.setAllergies(request.getAllergies());
        if (request.getMedicalHistory() != null) pet.setMedicalHistory(request.getMedicalHistory());
        if (request.getNotes() != null) pet.setNotes(request.getNotes());
        if (request.getEmoji() != null) pet.setEmoji(request.getEmoji());
        if (request.getImageUrl() != null) pet.setImageUrl(request.getImageUrl());

        Pet updatedPet = petRepository.save(pet);
        return convertToResponse(updatedPet);
    }

    public void deletePet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));
        petRepository.delete(pet);
    }

    private PetResponse convertToResponse(Pet pet) {
        PetResponse response = new PetResponse();
        response.setId(pet.getId());
        response.setName(pet.getName());
        response.setSpecies(pet.getSpecies());
        response.setBreed(pet.getBreed());
        response.setGender(pet.getGender());
        response.setAge(pet.getAge());
        response.setWeight(pet.getWeight());
        response.setAllergies(pet.getAllergies());
        response.setMedicalHistory(pet.getMedicalHistory());
        response.setNotes(pet.getNotes());
        response.setEmoji(pet.getEmoji());
        response.setImageUrl(pet.getImageUrl());
        response.setCustomerId(pet.getCustomerId());
        return response;
    }
}

