package com.example.application.data.service;

import com.example.application.data.entity.DataMobil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataMobilRepository extends JpaRepository<DataMobil, Long>, JpaSpecificationExecutor<DataMobil> {

}
