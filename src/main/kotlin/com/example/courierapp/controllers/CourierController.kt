package com.example.courierapp.controllers

import com.example.courierapp.entities.Courier
import com.example.courierapp.entities.Role
import com.example.courierapp.repositories.CustomerRepository
import com.example.courierapp.services.CourierService
import com.example.courierapp.services.CustomerService
import com.example.courierapp.services.PackageService
import com.example.courierapp.services.ReviewService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/couriers")
class CourierController(private val courierService: CourierService,
                        private val customerService: CustomerService,
                        private val customerRepository: CustomerRepository,
                        private val reviewService: ReviewService,
                        private val packageService: PackageService) {
    @GetMapping
    fun getCouriers(): List<Courier> = courierService.getCouriers()

    @GetMapping("/{courierId}")
    fun getCourierById(@PathVariable courierId: Long): ResponseEntity<out Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        val courier = courierService.getCourierById(courierId)
        val customer = customerService.getCustomerById(courierId)
        if (courier.isPresent) {
            if(isAdmin || courier.get().email == authEmail)
                return ResponseEntity.ok(courier.get())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this courier!")
        } else if (customer.isPresent) {
            if(isAdmin || customer.get().email == authEmail)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $courierId is not Courier yet!")
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this courier!")
        } else {
            return if (isAdmin)
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer and Courier with id $courierId not found!")
            else ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this customer!")
        }
    }

    @PostMapping
    fun createCourier(@RequestBody newCourier: Courier): ResponseEntity<out Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        val customer = customerRepository.findByEmail(authEmail)
        if (customer != null) {
            val courier = courierService.getCourierById(customer.id!!)
            if (courier.isPresent) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Courier with id ${courier.get().id} already exists!")
            } else {
                // create if courier doesn't exist and authorized to access it
                try {
                    newCourier.id = customer.id!!
                    newCourier.email = customer.email!!
                    newCourier.phone = customer.phone!!
                    newCourier.password = customer.password!!
                    newCourier.firstname = customer.firstname!!
                    newCourier.lastname = customer.lastname!!
                    newCourier.address = customer.address!!
                    newCourier.age = customer.age!!
                    newCourier.gender = customer.gender!!
                    newCourier.deliveriesPackages = mutableListOf()
                    newCourier.fin = customer.fin!!
                    newCourier.serialNo = customer.serialNo!!
                    newCourier.username = customer.username!!
                    newCourier.reviews = mutableListOf()

                    if (isAdmin) newCourier.role = Role.ADMIN
                    else newCourier.role = Role.COURIER
                    customer.role = newCourier.role
                    customer.isCourier = true

                    val createdCourier = courierService.createCourier(newCourier)
                    val savedCustomer = customerService.updateCustomerRole(customer.id!!, customer)

                    return ResponseEntity.created(URI.create("/api/couriers/${createdCourier.id}"))
                        .body(createdCourier)
                } catch (e: NoSuchElementException) {
                    // Handle the case where the courier already exists
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
                } catch (e: Exception) {
                    // Handle other exceptions
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
                }
            }
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("SOMETHING WENT WRONG!!!")
        }

    }

    @PutMapping("/{courierId}")
    fun updateCourier(@PathVariable courierId: Long,
                      @RequestBody updatedCourier: Courier?): ResponseEntity<out Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        if (updatedCourier == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Required Courier object is missing!")
        }
        try {
            val courier = courierService.getCourierById(courierId)
            val customer = customerService.getCustomerById(courierId)
            if (courier.isPresent && (isAdmin || courier.get().email == authEmail)) {
                // update if courier exists and authorized to access it
                val updated = courierService.updateCourier(courierId, updatedCourier)
                return ResponseEntity.ok(updated)
            }
            if (customer.isPresent && (isAdmin || customer.get().email == authEmail)) {
                // notify if customer exists and authorized to access it
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $courierId is not Courier yet!")
            }
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: NoSuchElementException) {
            // Handle the case where the courier with the specified ID is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }

        // if courier not found with specified ID
        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Courier with id $courierId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this courier!")
    }

    @DeleteMapping("/{courierId}")
    fun deleteCourier(@PathVariable courierId: Long): ResponseEntity<out Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        try {
            val courier = courierService.getCourierById(courierId)
            if (courier.isPresent && (isAdmin || courier.get().email == authEmail)) {
                // delete if courier exists and authorized to access it
                reviewService.deleteReview(courierId)
                packageService.removePackagesFromCourier(courierId)
                courierService.deleteCourier(courierId)

                return ResponseEntity.status(HttpStatus.OK).body("Courier's reviews removed successfully\n" +
                        "Package(s) removed from the courier!\n" +
                        "Courier deleted successfully!")
            }
        } catch (e: NoSuchElementException) {
            // Handle the exception and return a 404 response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Courier with id $courierId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this courier!")
    }
}