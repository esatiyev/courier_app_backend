package com.example.courierapp.controllers

import com.example.courierapp.entities.Courier
import com.example.courierapp.entities.Customer
import com.example.courierapp.services.CourierService
import com.example.courierapp.services.CustomerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.net.URI
@RestController
@RequestMapping("/api/customers")
class CustomerController(private val customerService: CustomerService,
                         private val courierService: CourierService
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    fun getCustomers(): List<Customer> {
        logger.info("Received get customers request by ID {}", getAuthenticatedId())
        return  customerService.getCustomers()
    }

    @GetMapping("/{customerId}")
    fun getCustomerById(@PathVariable customerId: Long): ResponseEntity<out Any> {
        val (authId, isAdmin) = getIdAndRolePair()

        logger.info("Received get customer by ID request for ID: {} by ID {}", customerId, authId)

        val customer = customerService.getCustomerById(customerId)
        if (customer.isPresent) {
            if(isAdmin || customer.get().id.toString() == authId)
                return ResponseEntity.ok(customer.get())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this customer!")
        } else {
            return if (isAdmin)
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $customerId not found!")
            else ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this customer!")
        }
    }

    @PostMapping
    fun createCustomer(@RequestBody customer: Customer): ResponseEntity<out Any> {
        return try {
            val createdCustomer = customerService.createCustomer(customer)

            logger.info("Customer created successfully with ID: {} ", createdCustomer.id)

            ResponseEntity.created(URI.create("/api/customers/${createdCustomer.id}"))
                .body(createdCustomer)
        } catch (e: HttpMessageNotReadableException) {
            logger.error("Error creating customer - Bad Request: {}", e.message)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        } catch (e: IllegalArgumentException) {
            // Handle the case where the customer already exists
            logger.error("Error creating customer - Conflict: {}", e.message)
            ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: Exception) {
            logger.error("Error creating customer - Internal Server Error: {}", e.message)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }
    }

    @PatchMapping("/{customerId}")
    fun updateCustomer(
        @PathVariable customerId: Long,
        @RequestBody updatedCustomer: Customer
    ): ResponseEntity<out Any> {
        val (authId, isAdmin) = getIdAndRolePair()

        logger.info("Received update customer request for ID: {} by {}", customerId, authId)

        try {
            val customer = customerService.getCustomerById(customerId)
            if (customer.isPresent && (isAdmin || customer.get().id.toString() == authId)) {
                // update if customer exists and authorized to access it
                val updated = customerService.updateCustomer(customerId, updatedCustomer)
                val courier = customerService.getCustomerById(customerId)
                if (courier.isPresent){
                    // update courier
                    val updatedCourier = Courier(
                        firstname = updatedCustomer.firstname,
                        lastname = updatedCustomer.lastname,
                        username = updatedCustomer.username,
                        email = updatedCustomer.email,
                        password = updatedCustomer.password,
                        age = updatedCustomer.age,
                        phone = updatedCustomer.phone,
                    )
                    courierService.updateCourier(customerId, updatedCourier)
                    logger.info("Courier updated successfully with ID: {} by {}", customerId, authId)
                }
                logger.info("Customer updated successfully with ID: {} by {}", customerId, authId)

                return ResponseEntity.ok(updated)
            }
        } catch (e: IllegalArgumentException) {
            logger.error("Error updating customer - Conflict: {}", e.message)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: NoSuchElementException) {
            logger.error("Error updating customer - Not Found: {}", e.message)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            // Handle other exceptions
            logger.error("Error updating customer - Internal Server Error: {}", e.message)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }

        // if customer not found with specified ID
        logger.info("Customer with ID {} not found for update", customerId)

        return if (isAdmin)
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $customerId not found!")
        else ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this customer!")
    }

    @DeleteMapping("/{customerId}")
    fun deleteCustomer(@PathVariable customerId: Long): ResponseEntity<out Any> {
        val (authId, isAdmin) = getIdAndRolePair()

        logger.info("Received delete customer request for ID: {} by {}", customerId, authId)

        try {
            val customer = customerService.getCustomerById(customerId)
            if (customer.isPresent && (isAdmin || customer.get().id.toString() == authId)) {
                // delete if customer exists and authorized to access it
                customerService.deleteCustomer(customerId)

                logger.info("Customer deleted successfully with ID: {} by {}", customerId, authId)

                return ResponseEntity.status(HttpStatus.OK).body("Customer with id $customerId deleted successfully!")
            }
        } catch (e: NoSuchElementException) {
            // Handle the exception and return a 404 response
            logger.error("Error deleting customer - Not Found: {}", e.message)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            // Handle other exceptions
            logger.error("Error deleting customer - Internal Server Error: {}", e.message)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }

        // if customer not found with specified ID
        logger.info("Customer with ID {} not found for delete", customerId)

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $customerId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this customer!")
    }

    private fun getIdAndRolePair(): Pair<String, Boolean> {
        val authentication = SecurityContextHolder.getContext().authentication
        val authId = (authentication.principal as UserDetails).username.toString()
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")
        return Pair(authId, isAdmin)
    }


    // Helper methods for logging
    private fun getAuthenticatedId(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        return (authentication.principal as UserDetails).username
    }
}

