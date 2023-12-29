package com.example.courierapp.services

import com.example.courierapp.entities.Courier
import com.example.courierapp.repositories.CourierRepository
import com.example.courierapp.repositories.PackageRepository
import com.example.courierapp.repositories.ReviewRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.NoSuchElementException

@Service
class CourierService(
    private val courierRepository: CourierRepository,
    private val packageRepository: PackageRepository,
    private val reviewRepository: ReviewRepository,
    private val encoder: PasswordEncoder
) {
    fun getCouriers(): List<Courier> = courierRepository.findAll()

    fun getCourierById(courierId: Long): Optional<Courier> = courierRepository.findById(courierId)

    fun createCourier(courier: Courier): Courier {
        if (courier.username != null) {
            if (courierRepository.existsByUsername(courier.username!!)) {
                throw NoSuchElementException("Username already exists")
            }
        }
        if (courier.email != null) {
            if (courierRepository.existsByEmail(courier.email!!)) {
                throw NoSuchElementException("Email already exists")
            }
        }
        if (courier.fin != null) {
            if (courierRepository.existsByFin(courier.fin!!)) {
                throw NoSuchElementException("FIN already exists")
            }
        }
        if (courier.serialNo != null) {
            if (courierRepository.existsBySerialNo(courier.serialNo!!)) {
                throw NoSuchElementException("Serial number already exists")
            }
        }
        if (courier.phone != null) {
            if (courierRepository.existsByPhone(courier.phone!!)) {
                throw NoSuchElementException("Phone number already exists")
            }
        }


        val updatedCourier = courier.copy(password = encoder.encode(courier.password))
        return courierRepository.save(updatedCourier)
    }

    fun updateCourier(courierId: Long, updatedCourier: Courier): Courier {
        val existingCourier = courierRepository.findById(courierId)
        if (existingCourier.isPresent) {
            val courier = existingCourier.get()
            courier.apply {
                if (updatedCourier.firstname != null && updatedCourier.firstname != courier.firstname)
                    firstname = updatedCourier.firstname
                if (updatedCourier.lastname != null && updatedCourier.lastname != courier.lastname)
                    lastname = updatedCourier.lastname

                if (updatedCourier.address != null && updatedCourier.address != courier.address)
                    address = updatedCourier.address

                if (updatedCourier.password != null && updatedCourier.password != courier.password) {
                    password = updatedCourier.password
                }

                if (updatedCourier.age != null && updatedCourier.age != courier.age)
                    age = updatedCourier.age

                if (updatedCourier.username != null && updatedCourier.username != courier.username) {
                    if (courierRepository.existsByUsername(updatedCourier.username!!)) {
                        throw IllegalArgumentException("Username already exists")
                    }
                    username = updatedCourier.username
                }

                if (updatedCourier.email != null && updatedCourier.email != courier.email) {
                    if (courierRepository.existsByEmail(updatedCourier.email!!)) {
                        throw IllegalArgumentException("Email already exists")
                    }
                    email = updatedCourier.email
                }


                if (updatedCourier.phone != null && updatedCourier.phone != courier.phone) {
                    if (courierRepository.existsByPhone(updatedCourier.phone!!)) {
                        throw IllegalArgumentException("Phone number already exists")
                    }
                    phone = updatedCourier.phone
                }

            }
            return courierRepository.save(courier)
        } else {
            throw NoSuchElementException("Courier with id $courierId not found.")
        }
    }

    fun deleteCourier(courierId: Long) {
        // Implement validation or additional logic if needed
//        courierRepository.deleteById(courierId)

        val courier = courierRepository.findById(courierId)
        if (courier.isPresent) {
            courierRepository.deleteById(courierId)
        } else {
            throw NoSuchElementException("Courier with id $courierId not found.")
        }
    }
}