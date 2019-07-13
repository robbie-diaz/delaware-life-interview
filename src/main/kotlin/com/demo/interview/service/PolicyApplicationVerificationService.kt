package com.demo.interview.service

import org.springframework.stereotype.Service

class VerificationFailureException(s: String) : Exception(s)

@Service
class PolicyApplicationVerificationService(val accountVerificationService: AccountVerificationService,
                                           val criminalStatusService: CriminalStatusService) {
    fun verifyApplication(customerName: String, bankAccountNumber: String) {
        val accountNumberExists = accountVerificationService.accountNumberExists(bankAccountNumber).block()
        if (accountNumberExists == false)
            throw VerificationFailureException("Invalid account number")

        val hasEnoughFunds = accountVerificationService.accountContainsFunds(bankAccountNumber, 500.00).block()
        if (hasEnoughFunds == false)
            throw VerificationFailureException("Not enough funds in account")

        val isMoneyLaunderer = criminalStatusService.isPersonMoneyLaunderer(customerName)
        if (isMoneyLaunderer)
            throw VerificationFailureException("Known money launderer")
    }
}