package com.basistheory.android.util

import com.basistheory.android.model.ElementValueReference
import com.basistheory.android.service.getElementValueReference
import com.basistheory.android.service.getValue
import net.datafaker.Faker
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import java.time.LocalDateTime

class GenericResponseAccessorFunctionsTests {
    private val faker = Faker()

    @Test
    fun `can get values of a generic response`() {
        val phoneNumber = faker.phoneNumber().phoneNumber()

        val tokenData = mapOf(
            "type" to "token",
            "data" to mapOf(
                "raw" to faker.name(),
                "name" to faker.name(),
                "card" to mapOf(
                    "number" to faker.finance().creditCard(),
                    "expMonth" to LocalDateTime.now().month,
                    "expYear" to LocalDateTime.now().year + 1,
                    "cvc" to 324
                ),
                "nested" to mapOf(
                    "raw" to faker.name(),
                    "phoneNumber" to phoneNumber
                )
            )
        ) as Any?

        expectThat(tokenData.getValue<String>("type")).isEqualTo("token")
        expectThat(tokenData.getValue<String>("data.nested.phoneNumber")).isEqualTo(phoneNumber)
        expectThat(tokenData.getValue<Int>("data.card.cvc")).isEqualTo(324)
    }

    @Test
    fun `can get value references of a generic response`() {
        val tokenData = mapOf(
            "type" to ElementValueReference { "token" },
            "data" to mapOf(
                "raw" to ElementValueReference { "name" },
                "name" to ElementValueReference { "name" },
                "card" to mapOf(
                    "number" to ElementValueReference { "name" },
                    "expMonth" to ElementValueReference { "name" },
                    "expYear" to ElementValueReference { "name" },
                    "cvc" to ElementValueReference { "name" }
                ),
                "nested" to mapOf(
                    "raw" to ElementValueReference { "name" },
                    "phoneNumber" to ElementValueReference { "name" }
                )
            )
        ) as Any?

        expectThat(tokenData.getElementValueReference("type")).isA<ElementValueReference>()
        expectThat(tokenData.getElementValueReference("data.card.cvc")).isA<ElementValueReference>()
    }
}