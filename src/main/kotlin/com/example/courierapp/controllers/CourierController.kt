package com.example.courierapp.controllers

import com.example.courierapp.entities.Courier
import com.example.courierapp.services.CourierService
import com.example.courierapp.services.PackageService
import com.example.courierapp.services.ReviewService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/couriers")
class CourierController(private val courierService: CourierService,
                        private val reviewService: ReviewService,
                        private val packageService: PackageService) {
    @GetMapping
    fun getCouriers(): List<Courier> = courierService.getCouriers()

    @GetMapping("/{courierId}")
    fun getCourierById(@PathVariable courierId: Long): ResponseEntity<out Any> {
        val courier = courierService.getCourierById(courierId)
        return if (courier.isPresent) {
            ResponseEntity.ok(courier.get())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Courier with id $courierId not found")
        }
    }

    @PostMapping
    fun createCourier(@RequestBody courier: Courier): ResponseEntity<out Any> {
        return try {
            val createdCourier = courierService.createCourier(courier)
            ResponseEntity.created(URI.create("/api/couriers/${createdCourier.id}"))
                .body(createdCourier)
        } catch (e: NoSuchElementException) {
            // Handle the case where the courier already exists
            ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        }
    }

    @PutMapping("/{courierId}")
    fun updateCourier(@PathVariable courierId: Long,
                      @RequestBody updatedCourier: Courier): ResponseEntity<out Any> {
        return try {
            val updated = courierService.updateCourier(courierId, updatedCourier)
            ResponseEntity.ok(updated)
        } catch (e: NoSuchElementException) {
            // Handle the case where the courier with the specified ID is not found
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @DeleteMapping("/{courierId}")
    fun deleteCourier(@PathVariable courierId: Long): ResponseEntity<out Any> {
        return try {
            reviewService.deleteReview(courierId)
            packageService.removePackageFromCourier(courierId)
            courierService.deleteCourier(courierId)

            ResponseEntity.status(HttpStatus.OK).body("Courier's reviews removed successfully\n" +
                    "Package(s) removed from the courier!\n" +
                    "Courier deleted successfully!")
        } catch (e: NoSuchElementException) {
            // Handle the exception and return a 404 response
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }
}