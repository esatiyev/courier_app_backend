package com.example.courierapp.repositories


import com.example.courierapp.entities.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : JpaRepository<Customer, Long> {
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun existsByFin(fin: String): Boolean
    fun existsBySerialNo(serialNo: String): Boolean
    fun existsByPhone(phone: String): Boolean
    fun findCustomerByEmailAndPassword(email: String, password: String): Customer
    fun existsCustomerByEmailAndPassword(email: String, password: String): Boolean
    fun findByEmailAndPassword(email: String, password: String): Customer
    fun findByEmail(id: String): Customer?
}
