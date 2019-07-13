package com.demo.interview.controller

import com.demo.interview.service.AccountVerificationService
import com.demo.interview.service.CriminalStatusService
import com.demo.interview.service.PolicyApplicationVerificationService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

class ServiceConfig(accountVerificationService: AccountVerificationService,
                    criminalStatusService: CriminalStatusService) {
    @get:Bean
    var verificationService = PolicyApplicationVerificationService(accountVerificationService, criminalStatusService)
}

@ExtendWith(SpringExtension::class)
@WebFluxTest
@Import(ServiceConfig::class)
internal class VerificationControllerTest(@Autowired val client: WebTestClient) {
    @MockkBean
    private lateinit var accountVerificationService: AccountVerificationService

    @MockkBean
    private lateinit var criminalStatusService: CriminalStatusService

    data class Data(
            val customerName: String,
            val bankAccountNumber: String,
            val validAccountNumber: Boolean,
            val enoughFundsInAccount: Boolean,
            val knownMoneyLaunderer: Boolean,
            val overallSuccess: HttpStatus,
            val error: String
    )

    @ParameterizedTest
    @MethodSource("annunitySource")
    fun `apply for an annunity`(data: Data) {
        every { accountVerificationService.accountNumberExists(data.bankAccountNumber) } returns Mono.just(data.validAccountNumber)
        every { accountVerificationService.accountContainsFunds(data.bankAccountNumber, 500.00) } returns Mono.just(data.enoughFundsInAccount)
        every { criminalStatusService.isPersonMoneyLaunderer(data.customerName) } returns data.knownMoneyLaunderer

        val result = client.post()
                .uri("/verify")
                .syncBody(AnnuityApplication(data.customerName, data.bankAccountNumber))
                .exchange()
                .expectStatus().isEqualTo(data.overallSuccess)
                .expectBody(String::class.java)
                .returnResult()

        if (data.overallSuccess != HttpStatus.OK) {
            assertThat(result.responseBody).isEqualTo(data.error)
        }
    }

    companion object {
        @JvmStatic
        fun annunitySource(): List<Data> = listOf(
                Data("Robbie", "12345", true, true, false, HttpStatus.OK, ""),
                Data("Robbie", "12345", false, true, false, HttpStatus.BAD_REQUEST, "Invalid account number"),
                Data("Robbie", "12345", true, false, false, HttpStatus.BAD_REQUEST, "Not enough funds in account"),
                Data("Robbie", "12345", true, true, true, HttpStatus.BAD_REQUEST, "Known money launderer")
        )
    }
}