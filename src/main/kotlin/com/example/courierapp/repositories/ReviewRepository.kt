package com.example.courierapp.repositories

import com.example.courierapp.entities.Courier
import com.example.courierapp.entities.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByCourier(courier: Courier): List<Review>
    fun deleteByCourier(courier: Courier)
}
