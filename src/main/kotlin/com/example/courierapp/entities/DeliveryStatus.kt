package com.example.courierapp.entities

enum class DeliveryStatus(val value: Int) {
    PACKAGE_CREATED(1),
    COURIER_ON_THE_WAY(2),
    COURIER_PICK_UP_PACKAGE( 3),
    PACKAGE_DELIVERED(4);

    companion object {
        fun fromString(value: String): DeliveryStatus? =
            enumValues<DeliveryStatus>().find { it.name == value }
    }

    // Check if the update status value is greater than the past status value
    fun isUpdateValid(updateStatus: DeliveryStatus): Boolean {
        return updateStatus.value > this.value
    }
}