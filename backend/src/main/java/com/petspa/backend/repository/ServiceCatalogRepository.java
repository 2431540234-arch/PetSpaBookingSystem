package com.petspa.backend.repository;

import com.petspa.backend.entity.ServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Long> {
    List<ServiceCatalog> findByCategory(String category);

    List<ServiceCatalog> findByStatus(String status);
}

