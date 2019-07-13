package com.demo.interview.model

class PreconditionFailureException : Exception()

data class Policy(
        var customerName: String,
        var initialDepositAmount: Double,
        var cancellationFee: Double,
        var interestRate: Double
) {
    var state = PolicyState.AWAITING_PREMIUM_DEPOSIT
    var balance: Double = 0.0

    fun premiumReceived() = ensurePolicyState(PolicyState.AWAITING_PREMIUM_DEPOSIT) {
        state = PolicyState.ACTIVE_CANCELLABLE
        balance = initialDepositAmount
    }

    fun cancelActivePolicy() = ensurePolicyState(PolicyState.ACTIVE_CANCELLABLE) {
        state = PolicyState.CLOSED_CANCELLED
    }

    fun initialMonthPassed() = ensurePolicyState(PolicyState.ACTIVE_CANCELLABLE) {
        state = PolicyState.ACTIVE
    }

    fun monthiversery() = ensurePolicyState(PolicyState.ACTIVE) {
        val rate = interestRate / 12.0
        balance += balance * rate
    }

    fun ownerPassedAway() = ensurePolicyState(PolicyState.ACTIVE) {
        state = PolicyState.CLOSED_OWNER_DIED
    }

    fun termCompleted() = ensurePolicyState(PolicyState.ACTIVE) {
        state = PolicyState.CLOSED_TERM_COMPLETED
    }

    /**
     * Ensures that a [Policy] is in the desired state before the lambda is called.
     * This method acts as a state transition guard for the Policy "state machine"
     *
     * @param s The desired state the Policy has to be in in order to act on it
     * @param block The callback that is called iff the Policy meets all the preconditions
     * @throws PreconditionFailureException if the Policy is not in the right state
     */
    private fun ensurePolicyState(s: PolicyState, block: () -> Unit) {
        if (this.state != s) throw PreconditionFailureException()
        block()
    }
}

enum class PolicyState {
    AWAITING_PREMIUM_DEPOSIT,
    ACTIVE_CANCELLABLE,
    ACTIVE,
    CLOSED_OWNER_DIED,
    CLOSED_TERM_COMPLETED,
    CLOSED_CANCELLED
}