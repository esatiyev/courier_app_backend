package com.example.courierapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

//@ConfigurationPropertiesScan
@SpringBootApplication
class CourierappApplication

fun main(args: Array<String>) {
	runApplication<CourierappApplication>(*args)
}