package com.example.courierapp.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "packages")
data class Package(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var packageName: String? = null,
    var weight: Float? = null,
    var price: Float? = null,
    var deliveryMethod: String? = null,
    var deliverLatitude: Double? = null,
    var deliverLongitude: Double? = null,
    var deliverAddress: String? = null,
    var pickUpLatitude: Double? = null,
    var pickUpLongitude: Double? = null,
    var pickUpAddress: String? = null,
    var receiverFullName: String? = null,
    var receiverPhone: String? = null,
    var receiverEmail: String? = null,
    var senderFullName: String? = null,
    var senderPhone: String? = null,
    var senderEmail: String? = null,
    var deliveryNote: String? = null,

    var courierFullName: String? = null,
    var courierPhone: String? = null,
    var courierEmail: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    var deliveryStatus: DeliveryStatus = DeliveryStatus.PACKAGE_CREATED,

    //burda
    @OneToMany(mappedBy = "packet", cascade = [CascadeType.ALL])
    val deliveryHistory: MutableList<DeliveryHistory> = mutableListOf(),

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id")
    var customer: Customer? = null,

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "courier_id")
    var courier: Courier? = null
) {
    // Add any additional methods or properties specific to the Package entity

    var createdDate: String? = null

    init {
        createdDate = formatDateTime(LocalDateTime.now())
    }

    fun updatePackageStatus(newStatus: DeliveryStatus) {
        deliveryStatus = newStatus
        deliveryHistory.add(DeliveryHistory(newStatus, formatDateTime(LocalDateTime.now()), this))
    }

    fun formatDateTime(dateTime: LocalDateTime): String {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.YYYY")

        val timeString = dateTime.format(timeFormatter)
        val dateString = dateTime.format(dateFormatter)

        return "$timeString\n$dateString"
    }
}
