package com.petspa.backend.controller;

import com.petspa.backend.dto.response.ServiceResponse;
import com.petspa.backend.service.ServiceCatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceController {

    private final ServiceCatalogService serviceCatalogService;

    public ServiceController(ServiceCatalogService serviceCatalogService) {
        this.serviceCatalogService = serviceCatalogService;
    }

    @GetMapping
    public List<ServiceResponse> getAllServices(@RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty()) {
            return serviceCatalogService.getServicesByCategory(category);
        }
        return serviceCatalogService.getAllServices();
    }

    @GetMapping("/{serviceId}")
    public ServiceResponse getServiceById(@PathVariable Long serviceId) {
        return serviceCatalogService.getServiceById(serviceId);
    }
}

