package com.example.courierapp.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "couriers")
data class Courier(
    @Id
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

//    @OneToOne
//    @JoinColumn(name = "customer_id")
//    var customer: Customer? = null,

    @OneToMany(mappedBy = "courier", cascade = [CascadeType.ALL])
    @JsonIgnore
    var deliveriesPackages: MutableList<Package> = mutableListOf(),


    @OneToMany(mappedBy = "courier", cascade = [CascadeType.ALL])
    @JsonIgnore
    var reviews: MutableList<Review> = mutableListOf()

// Add any additional methods or properties specific to the Courier entity
) {
    // Add any additional methods or properties specific to the Courier entity
}
