package com.example.courierapp.controllers

import com.example.courierapp.services.PackageService
import com.example.courierapp.entities.Package
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/packages")
class PackageController(private val packageService: PackageService) {
    @GetMapping
    fun getAllPackages(): List<Package> = packageService.getAllPackages()

    @GetMapping("/{packageId}")
    fun getPackageById(@PathVariable packageId: Long): ResponseEntity<out Any> {
        val packet = packageService.getPackageById(packageId)
        return if (packet.isPresent) {
            ResponseEntity.ok(packet.get())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Package with id $packageId not found!")
        }
    }

    @GetMapping("/customers/{customerId}")
    fun getPackagesByCustomerId(@PathVariable customerId: Long): ResponseEntity<out Any> {
//        packageService.getPackagesByCustomerId(customerId)
        return try {
            val packages = packageService.getPackagesByCustomerId(customerId)
            ResponseEntity.ok(packages)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @GetMapping("/couriers/{courierId}")
    fun getPackagesByCourierId(@PathVariable courierId: Long): ResponseEntity<out Any> {
//        packageService.getPackagesByCourierId(courierId)
        return try {
            val packages = packageService.getPackagesByCourierId(courierId)
            ResponseEntity.ok(packages)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @PostMapping("/customers/{customerId}")
    fun createPackage(
        @RequestBody packet: Package,
        @PathVariable customerId: Long

    ): ResponseEntity<out Any> {
//        val createdPackage = packageService.createPackage(packet, customerId)
//        return ResponseEntity.created(URI.create("/api/packages/${createdPackage.id}")).body(createdPackage)

        return try {
            val createdPackage = packageService.createPackage(packet, customerId)
            ResponseEntity.created(URI.create("/api/packages/${createdPackage.id}")).body(createdPackage)
        } catch (e: Exception) {
            if (e == IllegalArgumentException())
                ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)

            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @PutMapping("{packageId}/courier/{courierId}")
    fun addPackageToCourier(@PathVariable packageId: Long, @PathVariable courierId: Long): ResponseEntity<out Any> {
        return try {
            packageService.addPackageToCourier(courierId, packageId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            // Handle other exceptions if needed
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.")
        }

    }

    @PutMapping("/{packageId}")
    fun updatePackage(
        @PathVariable packageId: Long,
        @RequestBody updatedPackage: Package
    ): ResponseEntity<out Any> {

        return try {
            val updated = packageService.updatePackage(packageId, updatedPackage)
            ResponseEntity.ok(updated)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            // Handle other exceptions if needed
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.")
        }

    }

    @PutMapping("/couriers/{courierId}")
    fun removePackageFromCourier(@PathVariable courierId: Long): ResponseEntity<out Any> {
        return try {
            packageService.removePackageFromCourier(courierId)
            ResponseEntity.status(HttpStatus.OK).body("Package(s) removed from the courier!")
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @DeleteMapping("/{packageId}")
    fun deletePackage(@PathVariable packageId: Long): ResponseEntity<out Any> {
        return try {
            packageService.deletePackage(packageId)
            ResponseEntity.noContent().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }
}