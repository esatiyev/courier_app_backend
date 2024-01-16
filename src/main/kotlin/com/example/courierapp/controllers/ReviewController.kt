package com.example.courierapp.controllers

import com.example.courierapp.entities.Review
import com.example.courierapp.services.CourierService
import com.example.courierapp.services.ReviewService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/reviews")
class ReviewController(private val reviewService: ReviewService,
                       private val courierService: CourierService
) {
    @GetMapping
    fun getAllReviews(): List<Review> = reviewService.getAllReviews()

    @GetMapping("/{reviewId}")
    fun getReviewById(@PathVariable reviewId: Long): ResponseEntity<out Any> {
        val review = reviewService.getReviewById(reviewId)
        return if (review.isPresent) {
            ResponseEntity.ok(review.get())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review with id $reviewId not found!")
        }
    }

    @GetMapping("/couriers/{courierId}")
    fun getReviewsByCourierId(@PathVariable courierId: Long): ResponseEntity<out Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        try {
            val courier = courierService.getCourierById(courierId)
            if (courier.isPresent && (isAdmin || courier.get().email == authEmail)){
                // show reviews for the courier only if it is an admin or the courierId is the same as the authenticated user
                val reviews = reviewService.getReviewsByCourierId(courierId)
                return ResponseEntity.ok(reviews)
            }
        } catch (e: NoSuchElementException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Courier with id $courierId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this courier!")
    }

    @PostMapping("/couriers/{courierId}")
    fun addReview(
        @PathVariable courierId: Long,
        @RequestBody review: Review
    ): ResponseEntity<out Any> {

        return try {
            val addedReview = reviewService.addReview(review, courierId)
            ResponseEntity.created(URI.create("/api/reviews/${addedReview.id}"))
                .body(addedReview)
        } catch (e: NoSuchElementException) {
            // Handle the exception and return a custom response
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @DeleteMapping("/couriers/{courierId}")
    fun deleteReviewByCourierId(@PathVariable courierId: Long): ResponseEntity<out Any> {
        return try {
            reviewService.deleteReview(courierId)
            ResponseEntity.status(HttpStatus.OK).body("Courier's reviews removed successfully")
        } catch (e: NoSuchElementException) {
            // Handle the case where the review with the specified ID is not found
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }
}
