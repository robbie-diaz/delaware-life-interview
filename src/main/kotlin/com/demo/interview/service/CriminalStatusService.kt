package com.demo.interview.service

import org.springframework.stereotype.Service

interface CriminalStatusService {
    fun isPersonMoneyLaunderer(name: String): Boolean
}

@Service
class DummyCriminalStatusService : CriminalStatusService {
    override fun isPersonMoneyLaunderer(name: String): Boolean {
        TODO("not implemented")
    }
}