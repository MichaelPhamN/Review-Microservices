package com.example.productservice.repository;

import com.example.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    /*********************************************************************
     * default                                                           *
     *        - findAll                                                  *
     *        - findById                                                 *
     *        - save                                                     *
     *        - delete                                                   *
     *        - existsById                                               *
     *        - count                                                    *
     *        - ...                                                      *
     *********************************************************************/


    /*********************************************************************
     *                      findProductByProductName                     *
     *********************************************************************/
    List<Product> findProductByProductNameContainsIgnoreCase(String name);

    /*********************************************************************
     *                      findProductByProductType                     *
     *********************************************************************/
    List<Product> findProductByProductType(String productType);

    /*********************************************************************
     *                      findProductByPriceBetween                    *
     *********************************************************************/
    List<Product> findProductByPriceBetween(double minPrice, double maxPrice);

    /*********************************************************************
     *                      findProductByPriceGreaterThanEqual           *
     *********************************************************************/
    List<Product> findProductByPriceGreaterThanEqual(double price);

    /*********************************************************************
     *                      findProductByPriceLessThan                   *
     *********************************************************************/
    List<Product> findProductByPriceLessThan(double price);

    /*********************************************************************
     *                      getTotalCost                                 *
     *********************************************************************/
    @Query("SELECT SUM(p.price * p.quantity) FROM Product AS p")
    double getTotal();

    /*********************************************************************
     *                      getRow(s)OfProduct(s)ByProductName           *
     *********************************************************************/
    long countProductByProductNameEqualsIgnoreCase(String productName);

}
