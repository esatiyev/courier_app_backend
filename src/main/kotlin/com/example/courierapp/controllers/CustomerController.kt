package com.example.courierapp.controllers

import com.example.courierapp.entities.Customer
import com.example.courierapp.services.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/customers")
class CustomerController(private val customerService: CustomerService) {
    @GetMapping
    fun getCustomers(): List<Customer> = customerService.getCustomers()

    @GetMapping("/{customerId}")
    fun getCustomerById(@PathVariable customerId: Long): ResponseEntity<out Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        val customer = customerService.getCustomerById(customerId)
        if (customer.isPresent) {
            if(isAdmin || customer.get().email == authEmail) return ResponseEntity.ok(customer.get())
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
            ResponseEntity.created(URI.create("/api/customers/${createdCustomer.id}"))
                .body(createdCustomer)
        } catch (e: HttpMessageNotReadableException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        } catch (e: IllegalArgumentException) {
            // Handle the case where the customer already exists
            ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }
    }

    @PutMapping("/{customerId}")
    fun updateCustomer(
        @PathVariable customerId: Long,
        @RequestBody updatedCustomer: Customer
    ): ResponseEntity<out Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        try {
            val customer = customerService.getCustomerById(customerId)
            if (customer.isPresent && (isAdmin || customer.get().email == authEmail)) {
                // update if customer exists and authorized to access it
                val updated = customerService.updateCustomer(customerId, updatedCustomer)
                return ResponseEntity.ok(updated)
            }
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: NoSuchElementException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }

        // if customer not found with specified ID
        return if (isAdmin)
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $customerId not found!")
        else ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this customer!")
    }

    @DeleteMapping("/{customerId}")
    fun deleteCustomer(@PathVariable customerId: Long): ResponseEntity<out Any> {
//        customerService.deleteCustomer(customerId)
//        return ResponseEntity.noContent().build()
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        try {
            val customer = customerService.getCustomerById(customerId)
            if (customer.isPresent && (isAdmin || customer.get().email == authEmail)) {
                // delete if customer exists and authorized to access it
                customerService.deleteCustomer(customerId)
                return ResponseEntity.status(HttpStatus.OK).body("Customer with id $customerId deleted successfully!")
            }
        } catch (e: NoSuchElementException) {
            // Handle the exception and return a 404 response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $customerId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this customer!")
    }
}