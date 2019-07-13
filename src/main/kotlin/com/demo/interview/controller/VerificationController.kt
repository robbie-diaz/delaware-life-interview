package com.demo.interview.controller

import com.demo.interview.service.PolicyApplicationVerificationService
import com.demo.interview.service.VerificationFailureException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/verify")
class VerificationController(val verificationService: PolicyApplicationVerificationService) {
    @PostMapping
    fun verifyApplication(@RequestBody application: AnnuityApplication): ResponseEntity<Any> {
        return try {
            verificationService.verifyApplication(application.customerName, application.bankAccountNumber)
            ResponseEntity.ok().build()
        } catch (e: VerificationFailureException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}

data class AnnuityApplication(val customerName: String, val bankAccountNumber: String)
