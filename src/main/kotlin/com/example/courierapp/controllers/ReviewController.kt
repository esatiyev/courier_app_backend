package com.example.courierapp.controllers

import com.example.courierapp.entities.Review
import com.example.courierapp.services.ReviewService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/reviews")
class ReviewController(private val reviewService: ReviewService) {
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
        return try {
            val reviews = reviewService.getReviewsByCourierId(courierId)
            ResponseEntity.ok(reviews)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @PostMapping("/couriers/{courierId}")
    fun addReview(
        @RequestBody review: Review,
        @PathVariable courierId: Long
    ): ResponseEntity<out Any> {
//        val addedReview = reviewService.addReview(review, courierId)
//        return ResponseEntity.created(URI.create("/api/reviews/${addedReview.id}"))
//            .body(addedReview)

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
