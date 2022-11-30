package com.example.productservice.repository;

import com.example.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCustomRepository extends JpaRepository<Product, Long> {
    /*********************************************************************
    *                      findProductByName                             *
    **********************************************************************/
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE %:name%")
    List<Product> findByNameIgnoreCase(@Param("name") String name);
}
