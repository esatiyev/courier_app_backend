package com.example.courierapp.services

import com.example.courierapp.entities.Customer
import com.example.courierapp.repositories.CustomerRepository
import com.example.courierapp.repositories.PackageRepository
import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.NoSuchElementException

@Service
class CustomerService(
    private val customerRepository: CustomerRepository,
    private val packageRepository: PackageRepository,
    private val encoder: PasswordEncoder
//    private val passwordEncoder: BCryptPasswordEncoder
) {
    fun getCustomers(): List<Customer> = customerRepository.findAll()

    fun getCustomerById(customerId: Long): Optional<Customer> = customerRepository.findById(customerId)

    fun createCustomer(customer: Customer): Customer {
        if (customer.username == null)
            throw IllegalArgumentException("Username cannot be null")
        else if (customerRepository.existsByUsername(customer.username!!))
            throw IllegalArgumentException("Customer with this username {${customer.username}} already exists!")

        if (customer.email == null)
            throw IllegalArgumentException("Email cannot be null")
        else if (customerRepository.existsByEmail(customer.email!!))
            throw IllegalArgumentException("Customer with this email {${customer.email}} already exists!")

        if (customer.fin == null)
            throw IllegalArgumentException("FIN cannot be null")
        else if (customerRepository.existsByFin(customer.fin!!))
            throw IllegalArgumentException("Customer with this FIN {${customer.fin}} already exists!")

        if (customer.serialNo == null)
            throw IllegalArgumentException("Serial number cannot be null")
        else if (customerRepository.existsBySerialNo(customer.serialNo!!))
            throw IllegalArgumentException("Customer with this serial number {${customer.serialNo}} already exists!")

        if (customer.phone == null)
            throw IllegalArgumentException("Phone number cannot be null")
        else if (customerRepository.existsByPhone(customer.phone!!))
            throw IllegalArgumentException("Customer with this phone number {${customer.phone}} already exists!")

        if (customer.password == null)
            throw IllegalArgumentException("Password cannot be null")


        val updatedCustomer = customer.copy(password = encoder.encode(customer.password))
        return customerRepository.save(updatedCustomer)
    }

    fun updateCustomer(customerId: Long, updatedCustomer: Customer): Customer {
        val existingCustomer = customerRepository.findById(customerId)
        if (existingCustomer.isPresent) {
            val customer = existingCustomer.get()
            customer.apply {
                if (updatedCustomer.firstname != null)
                    firstname = updatedCustomer.firstname
                if (updatedCustomer.lastname != null)
                    lastname = updatedCustomer.lastname
                if (updatedCustomer.username != null && updatedCustomer.username != customer.username) {
                    if (customerRepository.existsByUsername(updatedCustomer.username!!)) {
                        throw IllegalArgumentException("Username already exists")
                    }
                    username = updatedCustomer.username
                }
                if (updatedCustomer.email != null && updatedCustomer.email != customer.email) {
                    if (customerRepository.existsByEmail(updatedCustomer.email!!)) {
                        throw IllegalArgumentException("Email already exists")
                    }
                    email = updatedCustomer.email
                }
                if (updatedCustomer.password != null && updatedCustomer.password != customer.password) {
                    password = updatedCustomer.password
                }

                if (updatedCustomer.age != null)
                    age = updatedCustomer.age

                if (updatedCustomer.phone != null && updatedCustomer.phone != customer.phone) {
                    if (customerRepository.existsByPhone(updatedCustomer.phone!!)) {
                        throw IllegalArgumentException("Phone number already exists")
                    }
                    phone = updatedCustomer.phone
                }
                if (updatedCustomer.address != null)
                    address = updatedCustomer.address

                if (updatedCustomer.isCourier != null)
                    isCourier = updatedCustomer.isCourier
            }
            return customerRepository.save(customer)
        } else {
            throw NoSuchElementException("Customer with id $customerId not found.")
        }
    }

    fun updateCustomerRole(customerId: Long, updatedCustomer: Customer): Customer {
        val existingCustomer = customerRepository.findById(customerId)
        if (existingCustomer.isPresent) {
            val customer = existingCustomer.get()
            customer.apply {
                if (updatedCustomer.isCourier != null)
                    isCourier = updatedCustomer.isCourier
                if (updatedCustomer.role != null)
                    role = updatedCustomer.role
            }
            return customerRepository.save(customer)
        } else {
            throw NoSuchElementException("Customer with id $customerId not found.")
        }
    }

    fun deleteCustomer(customerId: Long) {
        // Implement validation or additional logic if needed
//        customerRepository.deleteById(customerId)

        val customer = customerRepository.findById(customerId)
        if (customer.isPresent) {
            packageRepository.deleteByCustomerId(customerId)
            customerRepository.deleteById(customerId)

        } else {
            throw NoSuchElementException("Customer with id $customerId not found.")
        }
    }
}