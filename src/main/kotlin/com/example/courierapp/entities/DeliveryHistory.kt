package com.example.courierapp.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDate

@Entity
@Table(name = "delivery_history")
data class DeliveryHistory(


    @Enumerated(EnumType.STRING)
    var status: DeliveryStatus? = null,
    var timestamp: String? = null,

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "package_id")
    var packet: Package? = null,

    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}