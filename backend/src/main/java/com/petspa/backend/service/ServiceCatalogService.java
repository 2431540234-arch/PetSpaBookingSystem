package com.petspa.backend.service;

import com.petspa.backend.dto.response.ServiceResponse;
import com.petspa.backend.entity.ServiceCatalog;
import com.petspa.backend.exception.ResourceNotFoundException;
import com.petspa.backend.repository.ServiceCatalogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceCatalogService {

    private final ServiceCatalogRepository serviceCatalogRepository;

    public ServiceCatalogService(ServiceCatalogRepository serviceCatalogRepository) {
        this.serviceCatalogRepository = serviceCatalogRepository;
    }

    public ServiceResponse getServiceById(Long serviceId) {
        ServiceCatalog service = serviceCatalogRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));
        return convertToResponse(service);
    }

    public List<ServiceResponse> getAllServices() {
        List<ServiceCatalog> services = serviceCatalogRepository.findByStatus("active");
        return services.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<ServiceResponse> getServicesByCategory(String category) {
        List<ServiceCatalog> services = serviceCatalogRepository.findByCategory(category);
        return services.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private ServiceResponse convertToResponse(ServiceCatalog service) {
        ServiceResponse response = new ServiceResponse();
        response.setId(service.getId());
        response.setName(service.getName());
        response.setCategory(service.getCategory());
        response.setPrice(service.getPrice());
        response.setDuration(service.getDuration());
        response.setEmoji(service.getEmoji());
        response.setDescription(service.getDescription());
        response.setStatus(service.getStatus());
        response.setImageUrl(service.getImageUrl());
        return response;
    }
}

