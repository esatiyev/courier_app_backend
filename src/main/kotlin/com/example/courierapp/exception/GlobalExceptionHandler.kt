package com.example.courierapp.exception

//import io.jsonwebtoken.ExpiredJwtException
import com.example.courierapp.exception.ExpiredJwtException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJwtException(exception: ExpiredJwtException): ResponseEntity<ErrorMessageModel> {
//        return ResponseEntity
//            .status(HttpStatus.BAD_REQUEST)
//            .body(exception.message)
        val errorMessage = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            exception.message
        )
        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }
}
