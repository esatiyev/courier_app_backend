package com.example.courierapp.controllers

import com.example.courierapp.entities.Review
import com.example.courierapp.services.CourierService
import com.example.courierapp.services.ReviewService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    fun getAllReviews(): List<Review> {
        logger.info("Received get all reviews request by {}", getAuthenticatedId())
        return reviewService.getAllReviews()
    }

    @GetMapping("/{reviewId}")
    fun getReviewById(@PathVariable reviewId: Long): ResponseEntity<out Any> {
        val review = reviewService.getReviewById(reviewId)
        return if (review.isPresent) {
            logger.info("Received get review by ID request for ID: {} by {}", reviewId, getAuthenticatedId())
            ResponseEntity.ok(review.get())
        } else {
            logger.warn("Review with id {} not found for getReviewById", reviewId)
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review with id $reviewId not found!")
        }
    }

    @GetMapping("/couriers/{courierId}")
    fun getReviewsByCourierId(@PathVariable courierId: Long): ResponseEntity<out Any> {
        val (authId, isAdmin) = getIdAndRolePair()

        try {
            val courier = courierService.getCourierById(courierId)
            if (courier.isPresent && (isAdmin || courier.get().id.toString() == authId)){
                // show reviews for the courier only if it is an admin or the courierId is the same as the authenticated user
                val reviews = reviewService.getReviewsByCourierId(courierId)

                logger.info("Received get reviews by courier ID request for courier ID: {} by {}", courierId, authId)

                return ResponseEntity.ok(reviews)
            }
        } catch (e: NoSuchElementException) {
            logger.warn("Courier with id {} not found for getReviewsByCourierId", courierId)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            logger.error("Error getting reviews by courier ID: {}", e.message)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }


        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Courier with id $courierId not found!")

        logger.warn("Unauthorized attempt to access reviews for courier with ID: {} by {}", courierId, authId)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this courier!")
    }

    @PostMapping("/couriers/{courierEmail}")
    fun addReview(
        @PathVariable courierEmail: String,
        @RequestBody review: Review
    ): ResponseEntity<out Any> {

        return try {
            val addedReview = reviewService.addReview(review, courierEmail)

            logger.info("Review added successfully with ID: {} by {}", addedReview.id, getAuthenticatedId())

            ResponseEntity.created(URI.create("/api/reviews/${addedReview.id}"))
                .body(addedReview)
        } catch (e: NoSuchElementException) {
            // Handle the exception and return a custom response
            logger.warn("Courier with id {} not found for addReview", courierEmail)
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @DeleteMapping("/couriers/{courierId}")
    fun deleteReviewByCourierId(@PathVariable courierId: Long): ResponseEntity<out Any> {
        return try {
            reviewService.deleteReview(courierId)

            logger.info("Reviews removed successfully for courier with ID: {} by {}", courierId, getAuthenticatedId())

            ResponseEntity.status(HttpStatus.OK).body("Courier's reviews removed successfully")
        } catch (e: NoSuchElementException) {
            // Handle the case where the review with the specified ID is not found
            logger.warn("Courier with id {} not found for deleteReviewByCourierId", courierId)
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }


    private fun getIdAndRolePair(): Pair<String, Boolean> {
        val authentication = SecurityContextHolder.getContext().authentication
        val authId = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")
        return Pair(authId, isAdmin)
    }

    private fun getAuthenticatedId(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        return (authentication.principal as UserDetails).username
    }
}
