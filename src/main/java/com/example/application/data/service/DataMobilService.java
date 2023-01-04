package com.example.application.data.service;

import com.example.application.data.entity.DataMobil;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class DataMobilService {

    private final DataMobilRepository repository;

    public DataMobilService(DataMobilRepository repository) {
        this.repository = repository;
    }

    public Optional<DataMobil> get(Long id) {
        return repository.findById(id);
    }

    public DataMobil update(DataMobil entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<DataMobil> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<DataMobil> list(Pageable pageable, Specification<DataMobil> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
