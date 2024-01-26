package com.example.courierapp.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "customers")
data class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    var username: String? = null,
    var email: String? = null,
    var password: String? = null,
    var fin: String? = null,
    var serialNo: String? = null,
    var age: Int? = null,
    var gender: String? = null,
    var phone: String? = null,
    var address: String? = null,

//    @OneToOne(mappedBy = "customer", cascade = [CascadeType.ALL])
//    @JoinColumn(name = "courier_id")
//    var courier: Courier? = null,

    @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL])
    @JsonIgnore
    var packages: MutableList<Package>? = mutableListOf(),

    var ordersNumber: Int = 0,
    var expenses: Float = 0f,

    var role: Role = Role.CUSTOMER,
    var isCourier: Boolean? = false

    ) {
    // Add any additional methods or properties specific to the User entity
}

