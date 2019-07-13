package com.demo.interview.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

interface AccountVerificationService {
    fun accountNumberExists(accountNumber: String): Mono<Boolean>
    fun accountContainsFunds(accountNumber: String, premium: Number): Mono<Boolean>
}

@Service
class DummyAccountVerificationService : AccountVerificationService {
    override fun accountNumberExists(accountNumber: String): Mono<Boolean> {
        TODO("not implemented")
    }

    override fun accountContainsFunds(accountNumber: String, premium: Number): Mono<Boolean> {
        TODO("not implemented")
    }
}