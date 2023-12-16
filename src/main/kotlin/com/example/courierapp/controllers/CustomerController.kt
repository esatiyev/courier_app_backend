package com.example.courierapp.controllers

import com.example.courierapp.entities.Customer
import com.example.courierapp.services.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/customers")
class CustomerController(private val customerService: CustomerService) {
    @GetMapping
    fun getCustomers(): List<Customer> = customerService.getCustomers()

    @GetMapping("/{customerId}")
    fun getCustomerById(@PathVariable customerId: Long): ResponseEntity<out Any> {
        val customer = customerService.getCustomerById(customerId)
        return if (customer.isPresent) {
            ResponseEntity.ok(customer.get())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $customerId not found!")
        }
    }

    @PostMapping
    fun createCustomer(@RequestBody customer: Customer): ResponseEntity<out Any> {
        return try {
            val createdCustomer = customerService.createCustomer(customer)
            ResponseEntity.created(URI.create("/api/customers/${createdCustomer.id}"))
                .body(createdCustomer)
        } catch (e: IllegalArgumentException) {
            // Handle the case where the customer already exists
            ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        }
    }

    @PutMapping("/{customerId}")
    fun updateCustomer(
        @PathVariable customerId: Long,
        @RequestBody updatedCustomer: Customer
    ): ResponseEntity<Customer> {
        return try {
            val updated = customerService.updateCustomer(customerId, updatedCustomer)
            ResponseEntity.ok(updated)
        } catch (e: NoSuchElementException) {
            // Handle the case where the courier with the specified ID is not found
            if (e == IllegalArgumentException())
                ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @DeleteMapping("/{customerId}")
    fun deleteCustomer(@PathVariable customerId: Long): ResponseEntity<out Any> {
//        customerService.deleteCustomer(customerId)
//        return ResponseEntity.noContent().build()

        return try {
            customerService.deleteCustomer(customerId)
            ResponseEntity.noContent().build()
        } catch (e: NoSuchElementException) {
            // Handle the exception and return a 404 response
//            ResponseEntity.notFound().build()
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }
}