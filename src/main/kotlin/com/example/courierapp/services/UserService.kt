//package com.example.courierapp.services
//
//import com.example.courierapp.entities.Customer
//import com.example.courierapp.repositories.CustomerRepository
//import org.springframework.stereotype.Service
//
//@Service
//class UserService(private val customerRepository: CustomerRepository) {
//    fun getAllUsers(): List<Customer> = customerRepository.findAll()
//
//    fun getUserById(id: Long): Customer? = customerRepository.findById(id).orElse(null)
//
//    fun createUser(customer: Customer): Customer = customerRepository.save(customer)
//
//    fun updateUser(id: Long, updatedCustomer: Customer): Customer {
//        if (customerRepository.existsById(id)) {
//            updatedCustomer.id = id
//            return customerRepository.save(updatedCustomer)
//        }
//        throw NoSuchElementException("User with ID $id not found.")
//    }
//
//    fun deleteUser(id: Long) {
//        if (customerRepository.existsById(id)) {
//            customerRepository.deleteById(id)
//        } else {
//            throw NoSuchElementException("User with ID $id not found.")
//        }
//    }
//
////    fun addReview(id: Long, review: String): Customer? {
////        return customerRepository.findById(id.toString()).map {
////            it.addReview(review)
////            customerRepository.save(it)
////        }.orElse(null)
////    }
//}