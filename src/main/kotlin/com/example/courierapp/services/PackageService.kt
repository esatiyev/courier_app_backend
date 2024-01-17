package com.example.courierapp.services

import com.example.courierapp.entities.Package
import com.example.courierapp.repositories.CourierRepository
import com.example.courierapp.repositories.CustomerRepository
import com.example.courierapp.repositories.PackageRepository
import com.example.courierapp.repositories.ReviewRepository
import org.springframework.stereotype.Service
import java.util.*
import kotlin.NoSuchElementException

@Service
class PackageService(
    private val packageRepository: PackageRepository,
    private val customerRepository: CustomerRepository,
    private val courierRepository: CourierRepository,
    private val reviewRepository: ReviewRepository
) {
    fun getAllPackages(): List<Package> = packageRepository.findAll()

    fun getPackageById(packageId: Long): Optional<Package> =
        packageRepository.findById(packageId)

    fun getPackagesByCustomerId(customerId: Long): List<Package> {
        val customer = customerRepository.findById(customerId)
        return customer.map { packageRepository.findByCustomer(it) }.orElseThrow {
            NoSuchElementException("Customer with id $customerId not found.")
        }
    }

    fun getPackagesByCourierId(courierId: Long): List<Package> {
        val courier = courierRepository.findById(courierId)
        return courier.map { packageRepository.findByCourier(it) }.orElseThrow {
            NoSuchElementException("Courier with id $courierId not found.")
        }
    }

    fun createPackage(packet: Package, customerId: Long): Package {
        // Implement validation or additional logic if needed
        val customer = customerRepository.findById(customerId)
        return if (customer.isPresent) {
            if (packet.deliverAddress == null)
                throw IllegalArgumentException("Deliver address cannot be null.")
            if (packet.pickUpAddress == null)
                throw IllegalArgumentException("Pickup address cannot be null.")
            if (packet.deliveryMethod == null)
                throw IllegalArgumentException("Delivery method cannot be null.")
            if (packet.packageName == null)
                throw IllegalArgumentException("Package name cannot be null.")
            if (packet.price == null)
                throw IllegalArgumentException("Price cannot be null.")
            if (packet.receiverEmail == null)
                throw IllegalArgumentException("Receiver email cannot be null.")
            if (packet.receiverFullName == null)
                throw IllegalArgumentException("Receiver name cannot be null.")
            if (packet.receiverPhone == null)
                throw IllegalArgumentException("Receiver phone cannot be null.")
            if (packet.weight == null)
                throw IllegalArgumentException("Weight cannot be null.")

//            packet.createdDate = packet.formatDateTime(LocalDateTime.now())

            packet.customer = customer.get()
            packageRepository.save(packet)
        } else {
            throw NoSuchElementException("Customer with id $customerId not found.")
        }
    }

    fun addPackageToCourier(courierId: Long, packageId: Long) {
        // Implement validation or additional logic if needed
        val courier = courierRepository.findById(courierId)
        if (courier.isPresent) {
            val packet = packageRepository.findById(packageId)
            if (packet.isPresent) {
                if (packet.get().courier != null)
                    throw IllegalArgumentException("Package already assigned to a courier.")

                packet.get().courier = courier.get()
                packageRepository.save(packet.get())
            } else {
                throw NoSuchElementException("Package with id $packageId not found.")
            }
        } else {
            throw NoSuchElementException("Courier with id $courierId not found.")
        }
    }

    fun updatePackage(packageId: Long, updatedPackage: Package): Package {
        // Implement validation or additional logic if needed
        val existingPackage = packageRepository.findById(packageId)
        if (existingPackage.isPresent) {
            val packet = existingPackage.get()
            packet.apply {
                if (deliveryStatus.isUpdateValid(updatedPackage.deliveryStatus)){
                    updatePackageStatus(updatedPackage.deliveryStatus)
//                    deliveryStatus = updatedPackage.deliveryStatus
//                    deliveryHistory.add(DeliveryHistory(updatedPackage.deliveryStatus, formatDateTime(LocalDate.now()), this))
                }
                else throw IllegalArgumentException("Invalid delivery status")
            }
            return packageRepository.save(packet)
        } else {
            throw NoSuchElementException("Package with id $packageId not found.")
        }
    }

    fun removePackagesFromCourier(courierId: Long) {
        val courier = courierRepository.findById(courierId)
        if (courier.isPresent) {
            for (packet in packageRepository.findByCourier(courier.get())) {
                packet.courier = null
                packageRepository.save(packet)
            }
        } else {
            throw NoSuchElementException("Courier with id $courierId not found.")
        }
    }

    fun deletePackage(packageId: Long) {
        val packet = packageRepository.findById(packageId)
        if (packet.isPresent) {
            packageRepository.deleteById(packageId)
        } else {
            throw NoSuchElementException("Package with id $packageId not found.")
        }
    }
}