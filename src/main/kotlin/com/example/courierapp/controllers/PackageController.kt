package com.example.courierapp.controllers

import com.example.courierapp.entities.DeliveryStatus
import com.example.courierapp.services.PackageService
import com.example.courierapp.entities.Package
import com.example.courierapp.services.CourierService
import com.example.courierapp.services.CustomerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/packages")
class PackageController(private val packageService: PackageService,
                        private val customerService: CustomerService,
                        private val courierService: CourierService
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    fun getAllPackages(): List<Package> {
        logger.info("Received get all packages request by {}", getAuthenticatedId())
        return packageService.getAllPackages()
    }

    @GetMapping("/awaitingDelivery")
    fun getAwaitingDeliveryPackages(): List<Package> {
        val (authId, isAdmin) = getIdAndRolePair()
        logger.info("Received get awaiting delivery packages request by {}", getAuthenticatedId())
        return packageService.getAwaitingDeliveryPackages(authId)
    }

    @GetMapping("/{packageId}")
    fun getPackageById(@PathVariable packageId: Long): ResponseEntity<out Any> {
        val (authId, isAdmin) = getIdAndRolePair()

        try {
            val packet = packageService.getPackageById(packageId)
            if (packet.isPresent && (isAdmin || packet.get().customer?.id.toString() == authId || packet.get().courier?.id.toString() == authId)){
                // show customer or courier delivery package only if it is an admin or the customer or courier email is the same as the authenticated user
                logger.info("Received get package by ID request for ID: {} by {}", packageId, authId)

                return ResponseEntity.ok(packet.get())
            }
        } catch (e: Exception) {
            logger.error("Error getting package by ID: {}", e.message)

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Package with id $packageId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this package!")
    }

    @GetMapping("/customers/{customerId}")
    fun getPackagesByCustomerId(@PathVariable customerId: Long): ResponseEntity<out Any> {
        val (authId, isAdmin) = getIdAndRolePair()


        try {
            val customer = customerService.getCustomerById(customerId)
            if (customer.isPresent && (isAdmin || customer.get().id.toString() == authId)){
                // show packages
                logger.info("Received get packages by customer ID request for customer ID: {} by {}", customerId, authId)

                val packages = packageService.getPackagesByCustomerId(customerId)
                return ResponseEntity.ok(packages)
            }
        } catch (e: NoSuchElementException) {
            logger.error("Error getting packages by customer ID: {}", e.message)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            logger.error("Error getting packages by customer ID: {}", e.message)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $customerId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this customer's packages!")
    }

    @GetMapping("/couriers/{courierId}")
    fun getPackagesByCourierId(@PathVariable courierId: Long): ResponseEntity<out Any> {
        val (authId, isAdmin) = getIdAndRolePair()

        try {
            val courier = courierService.getCourierById(courierId)
            val customer = customerService.getCustomerById(courierId)
            if(courier.isPresent && (isAdmin || courier.get().id.toString() == authId)) {
                // show packages
                val packages = packageService.getPackagesByCourierId(courierId)

                logger.info("Received get packages by courier ID request for courier ID: {} by {}", courierId, authId)

                return ResponseEntity.ok(packages)
            } else if(customer.isPresent && (isAdmin || customer.get().id.toString() == authId)){
                // notify if customer exists and authorized to access it
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $courierId is not Courier yet!")
            }
        } catch (e: NoSuchElementException) {
            logger.error("Error getting packages by courier ID: {}", e.message)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            logger.error("Error getting packages by courier ID: {}", e.message)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Courier with id $courierId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this courier's packages!")
    }

    @PostMapping("/customers/{customerId}")
    fun createPackage(
        @RequestBody packet: Package,
        @PathVariable customerId: Long
    ): ResponseEntity<out Any> {
        val (authId, isAdmin) = getIdAndRolePair()

        try {
            val customer = customerService.getCustomerById(customerId)
            if (customer.isPresent && (isAdmin || customer.get().id.toString() == authId)){
                // create package
                val createdPackage = packageService.createPackage(packet, customerId)

                logger.info("Received create package request for customer ID: {} by {}", customerId, authId)

                return ResponseEntity.created(URI.create("/api/packages/${createdPackage.id}")).body(createdPackage)
            }
        } catch (e: NoSuchElementException) {
            logger.error("Error creating package: {}", e.message)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: IllegalArgumentException) {
            logger.error("Error creating package: {}", e.message)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: Exception) {
            logger.error("Error creating package: {}", e.message)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $customerId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this customer!")
    }

    @PutMapping("/{packageId}/courier/{courierId}")
    fun addPackageToCourier(@PathVariable packageId: Long, @PathVariable courierId: Long): ResponseEntity<out Any> {
        val (authId, isAdmin) = getIdAndRolePair()

        try {
            val courier = courierService.getCourierById(courierId)
            val customer = customerService.getCustomerById(courierId)
            if (courier.isPresent && (isAdmin || courier.get().id.toString() == authId)){
                // add package
                packageService.addPackageToCourier(courierId, packageId)

                logger.info("Received add package to courier request for package ID: {} and courier ID: {} by {}", packageId, courierId, authId)

                return ResponseEntity.noContent().build()
            } else if (customer.isPresent && (isAdmin || customer.get().id.toString() == authId)) {
                // notify if customer exists and authorized to access it
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $courierId is not Courier yet!")
            }
        } catch (e: IllegalArgumentException) {
            logger.error("Error adding package to courier: {}", e.message)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: NoSuchElementException) {
            logger.error("Error adding package to courier: {}", e.message)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            // Handle other exceptions if needed
            logger.error("An unexpected error occurred: {}", e.message)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.")
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Courier with id $courierId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this courier!")
    }

    @PutMapping("/{packageId}")
    fun updatePackage(
        @PathVariable packageId: Long,
        @RequestBody updatedPackage: Package
    ): ResponseEntity<out Any> {
        val (authId, isAdmin) = getIdAndRolePair()

        try {
            val packet = packageService.getPackageById(packageId)
            if (packet.isPresent && (isAdmin || packet.get().courier?.id.toString() == authId)){
                // update if package exists and authorized to access it
                val updated = packageService.updatePackage(packageId, updatedPackage)

                logger.info("Received update package request for package ID: {} by {}", packageId, authId)

                return ResponseEntity.ok(updated)
            }
        } catch (e: IllegalArgumentException) {
            logger.error("Error updating package: {}", e.message)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: NoSuchElementException) {
            logger.error("Error updating package: {}", e.message)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            // Handle other exceptions if needed
            logger.error("An unexpected error occurred: {}", e.message)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.")
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Package with id $packageId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this package!")
    }

    // only admin can remove packages from a courier
    @PutMapping("/couriers/{courierId}")
    fun removePackagesFromCourier(@PathVariable courierId: Long): ResponseEntity<out Any> {
        try {
//            val courier = courierService.getCourierById(courierId)
            packageService.removePackagesFromCourier(courierId)

            logger.info("Packages removed from courier with ID: {} by {}", courierId, getAuthenticatedId())

            return ResponseEntity.status(HttpStatus.OK).body("Package(s) removed from the courier!")
        } catch (e: NoSuchElementException) {
            logger.error("Error removing packages from courier: {}", e.message)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @DeleteMapping("/{packageId}")
    fun deletePackage(@PathVariable packageId: Long): ResponseEntity<out Any> {
        val (authId, isAdmin) = getIdAndRolePair()

        logger.info("Received delete package request for ID: {} by {}", packageId, authId)

        try {
            val packet = packageService.getPackageById(packageId)
            if (packet.isPresent){
                // delete if package exists and authorized to access it
                if (isAdmin) {
                    packageService.deletePackage(packageId)
                    logger.info("Package deleted successfully with ID: {} by {}", packageId, authId)
                    return ResponseEntity.status(HttpStatus.OK).body("Package deleted successfully!")
                }
                if (packet.get().customer?.id.toString() == authId) {
                    if (packet.get().deliveryStatus == DeliveryStatus.PACKAGE_CREATED) {
                        packageService.deletePackage(packageId)

                        logger.info("Package deleted successfully with ID: {} by {}", packageId, authId)

                        return ResponseEntity.status(HttpStatus.OK).body("Package deleted successfully!")
                    }
                    logger.warn("Attempt to delete package with ID: {} that cannot be deleted by {}", packageId, authId)
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Package cannot be deleted!")
                }

                logger.warn("Unauthorized attempt to delete package with ID: {} by {}", packageId, authId)

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this package!")
            }
        } catch (e: NoSuchElementException) {
            logger.error("Error deleting package - Not Found: {}", e.message)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            // Handle other exceptions if needed
            logger.error("Error deleting package - Internal Server Error: {}", e.message)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Package with id $packageId not found!")
        logger.warn("Unauthorized attempt to delete non-existent package with ID: {} by {}", packageId, authId)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this package!")
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