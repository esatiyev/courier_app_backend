package com.example.courierapp.services

import com.example.courierapp.entities.Courier
import com.example.courierapp.entities.Review
import com.example.courierapp.repositories.CourierRepository
import com.example.courierapp.repositories.ReviewRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReviewService(private val reviewRepository: ReviewRepository,
    private val courierRepository: CourierRepository
) {
    fun getAllReviews(): List<Review> = reviewRepository.findAll()

    fun getReviewById(reviewId: Long): Optional<Review> = reviewRepository.findById(reviewId)

    fun getReviewsByCourierId(courierId: Long): List<Review> {
//        val courier = Courier(id = courierId) // Create a dummy courier with the given ID
//        return reviewRepository.findByCourier(courier)

        val courier = courierRepository.findById(courierId)
        if (courier.isPresent){
            return reviewRepository.findByCourier(courier.get()) // Create a dummy courier with the given ID
        } else {
            throw NoSuchElementException("Courier with id $courierId not found.")
        }
    }

    fun addReview(review: Review, courierId: Long): Review {
        // Implement validation or additional logic if needed
        val courier = courierRepository.findById(courierId)
        if (courier.isPresent){
            review.courier = courier.get()
            return reviewRepository.save(review)
        } else {
            throw NoSuchElementException("Courier with id $courierId not found.")
        }
    }

    fun deleteReview(courierId: Long) {
        // Implement validation or additional logic if needed
        val courier = courierRepository.findById(courierId)
        if (courier.isPresent){
            for (review in reviewRepository.findByCourier(courier.get())) {
                reviewRepository.delete(review)
            }

        } else {
            throw NoSuchElementException("Courier with id $courierId not found.")
        }
    }

}