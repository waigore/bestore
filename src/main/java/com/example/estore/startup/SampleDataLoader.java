package com.example.estore.startup;

import com.example.estore.entity.ProductType;
import com.example.estore.repository.ProductTypeRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SampleDataLoader {
    private static final Logger LOG = LoggerFactory.getLogger(SampleDataLoader.class);

    @Autowired
    ProductTypeRepository productTypeRepository;

    @PostConstruct
    public void init() {
        LOG.info("Loading sample data into database");

        List<ProductType> productTypes = List.of(
                ProductType.builder()
                        .type("SMTV").typeDisplayName("Smart TVs")
                        .build(),
                ProductType.builder()
                        .type("APPL").typeDisplayName("Kitchen Appliances")
                        .build(),
                ProductType.builder()
                        .type("PHON").typeDisplayName("Smartphones")
                        .build(),
                ProductType.builder()
                        .type("AUDI").typeDisplayName("Audio Visual Devices")
                        .build(),
                ProductType.builder()
                        .type("HOME").typeDisplayName("Home Appliances")
                        .build()
        );
        productTypeRepository.saveAll(productTypes);
    }
}
