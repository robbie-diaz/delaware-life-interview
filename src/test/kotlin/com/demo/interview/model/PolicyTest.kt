package com.demo.interview.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.reflect.KFunction1

class PolicyTest {
    data class State(
            val transitionMethods: List<KFunction1<Policy, Unit>>,
            val expectedState: PolicyState
    )

    companion object {
        @JvmStatic
        fun stateSource(): List<State> = listOf(
                State(emptyList(), PolicyState.AWAITING_PREMIUM_DEPOSIT),
                State(listOf(Policy::premiumReceived), PolicyState.ACTIVE_CANCELLABLE),
                State(listOf(Policy::premiumReceived, Policy::cancelActivePolicy), PolicyState.CLOSED_CANCELLED),
                State(listOf(Policy::premiumReceived, Policy::initialMonthPassed), PolicyState.ACTIVE),
                State(listOf(Policy::premiumReceived, Policy::initialMonthPassed, Policy::monthiversery), PolicyState.ACTIVE),
                State(listOf(Policy::premiumReceived, Policy::initialMonthPassed, Policy::ownerPassedAway), PolicyState.CLOSED_OWNER_DIED),
                State(listOf(Policy::premiumReceived, Policy::initialMonthPassed, Policy::termCompleted), PolicyState.CLOSED_TERM_COMPLETED)
        )
    }

    @ParameterizedTest
    @MethodSource("stateSource")
    fun `should create policy`(state: State) {
        val p = Policy("Robbie", 5000.00, 500.00, 5.0)
        state.transitionMethods.forEach { it.invoke(p) }
        assertThat(p.state).isEqualTo(state.expectedState)
    }

    @Test
    fun `policy cannot change to incorrect state`() {
        Assertions.assertThrows(PreconditionFailureException::class.java) {
            val p = Policy("Robbie", 5000.00, 500.00, 5.0)
            p.premiumReceived()
            p.termCompleted()
        }
    }

    @Test
    fun `monthiversery calculates interest`() {
        val deposit = 5000.00
        val interestRate = 5.0
        val newBalance = (1 + (interestRate / 12)) * deposit

        val p = Policy("Robbie", deposit, 500.00, interestRate)
        p.premiumReceived()
        assertThat(p.balance).isEqualTo(p.initialDepositAmount)

        p.initialMonthPassed()
        p.monthiversery()

        assertThat(p.balance).isCloseTo(newBalance, within(0.001))
    }
}