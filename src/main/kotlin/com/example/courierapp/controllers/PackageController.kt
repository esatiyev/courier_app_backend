package com.example.courierapp.controllers

import com.example.courierapp.entities.DeliveryStatus
import com.example.courierapp.services.PackageService
import com.example.courierapp.entities.Package
import com.example.courierapp.services.CourierService
import com.example.courierapp.services.CustomerService
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
    @GetMapping
    fun getAllPackages(): List<Package> = packageService.getAllPackages()

    @GetMapping("/{packageId}")
    fun getPackageById(@PathVariable packageId: Long): ResponseEntity<out Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        try {
            val packet = packageService.getPackageById(packageId)
            if (packet.isPresent && (isAdmin || packet.get().customer?.email == authEmail || packet.get().courier?.email == authEmail)){
                // show customer or courier delivery package only if it is an admin or the customer or courier email is the same as the authenticated user
                return ResponseEntity.ok(packet.get())
            }
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Package with id $packageId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this package!")
    }

    @GetMapping("/customers/{customerId}")
    fun getPackagesByCustomerId(@PathVariable customerId: Long): ResponseEntity<out Any> {
//        packageService.getPackagesByCustomerId(customerId)
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        try {
            val customer = customerService.getCustomerById(customerId)
            if (customer.isPresent && (isAdmin || customer.get().email == authEmail)){
                // show packages
                val packages = packageService.getPackagesByCustomerId(customerId)
                return ResponseEntity.ok(packages)
            }
        } catch (e: NoSuchElementException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $customerId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this customer's packages!")
    }

    @GetMapping("/couriers/{courierId}")
    fun getPackagesByCourierId(@PathVariable courierId: Long): ResponseEntity<out Any> {
//        packageService.getPackagesByCourierId(courierId)
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        try {
            val courier = courierService.getCourierById(courierId)
            val customer = customerService.getCustomerById(courierId)
            if(courier.isPresent && (isAdmin || courier.get().email == authEmail)) {
                // show packages
                val packages = packageService.getPackagesByCourierId(courierId)
                return ResponseEntity.ok(packages)
            } else if(customer.isPresent && (isAdmin || customer.get().email == authEmail)){
                // notify if customer exists and authorized to access it
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $courierId is not Courier yet!")
            }
        } catch (e: NoSuchElementException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
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
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        try {
            val customer = customerService.getCustomerById(customerId)
            if (customer.isPresent && (isAdmin || customer.get().email == authEmail)){
                // create package
                val createdPackage = packageService.createPackage(packet, customerId)
                return ResponseEntity.created(URI.create("/api/packages/${createdPackage.id}")).body(createdPackage)
            }
        } catch (e: NoSuchElementException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $customerId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this customer!")
    }

    @PutMapping("{packageId}/courier/{courierId}")
    fun addPackageToCourier(@PathVariable packageId: Long, @PathVariable courierId: Long): ResponseEntity<out Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        try {
            val courier = courierService.getCourierById(courierId)
            val customer = customerService.getCustomerById(courierId)
            if (courier.isPresent && (isAdmin || courier.get().email == authEmail)){
                // add package
                packageService.addPackageToCourier(courierId, packageId)
                return ResponseEntity.noContent().build()
            } else if (customer.isPresent && (isAdmin || customer.get().email == authEmail)) {
                // notify if customer exists and authorized to access it
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with id $courierId is not Courier yet!")
            }
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: NoSuchElementException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            // Handle other exceptions if needed
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
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        try {
            val packet = packageService.getPackageById(packageId)
            if (packet.isPresent && (isAdmin || packet.get().courier?.email == authEmail)){
                // update if package exists and authorized to access it
                val updated = packageService.updatePackage(packageId, updatedPackage)
                return ResponseEntity.ok(updated)
            }
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: NoSuchElementException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            // Handle other exceptions if needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.")
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Package with id $packageId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this package!")
    }

    // ancaq admin remove ede bilir
    @PutMapping("/couriers/{courierId}")
    fun removePackagesFromCourier(@PathVariable courierId: Long): ResponseEntity<out Any> {
        try {
//            val courier = courierService.getCourierById(courierId)
            packageService.removePackagesFromCourier(courierId)
            return ResponseEntity.status(HttpStatus.OK).body("Package(s) removed from the courier!")
        } catch (e: NoSuchElementException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @DeleteMapping("/{packageId}")
    fun deletePackage(@PathVariable packageId: Long): ResponseEntity<out Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val authEmail = (authentication.principal as UserDetails).username
        val roles = authentication.authorities.map { it.authority }
        val isAdmin = roles.contains("ROLE_ADMIN")

        try {
            val packet = packageService.getPackageById(packageId)
            if (packet.isPresent){
                // delete if package exists and authorized to access it
                if (isAdmin) {
                    packageService.deletePackage(packageId)
                    return ResponseEntity.status(HttpStatus.OK).body("Package deleted successfully!")
                }
                if (packet.get().customer?.email == authEmail) {
                    if (packet.get().deliveryStatus == DeliveryStatus.PACKAGE_CREATED) {
                        packageService.deletePackage(packageId)
                        return ResponseEntity.status(HttpStatus.OK).body("Package deleted successfully!")
                    }
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Package cannot be deleted!")
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this package!")
            }
        } catch (e: NoSuchElementException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            // Handle other exceptions if needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }

        if (isAdmin)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Package with id $packageId not found!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this package!")
    }
}