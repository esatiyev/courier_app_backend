package com.example.courierapp.repositories

import com.example.courierapp.entities.Courier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CourierRepository : JpaRepository<Courier, Long> {
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun existsByFin(fin: String): Boolean
    fun existsBySerialNo(serialNo: String): Boolean
    fun existsByPhone(phone: String): Boolean
}
