package com.example.courierapp.repositories

import com.example.courierapp.entities.Courier
import com.example.courierapp.entities.Customer
import com.example.courierapp.entities.Package
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface PackageRepository : JpaRepository<Package, Long> {
    fun findByCustomer(customer: Customer): List<Package>
    fun findByCourier(courier: Courier?): List<Package>
    fun deleteByCourierId(courierId: Long)
    fun deleteByCustomerId(customerId: Long)
}

