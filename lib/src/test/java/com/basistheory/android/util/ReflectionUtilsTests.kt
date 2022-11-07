package com.basistheory.android.util

import com.basistheory.android.view.TextElement
import com.github.javafaker.Faker
import org.junit.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*
import java.time.Instant

class ReflectionUtilsTests {
    private val faker = Faker()

    @Test
    fun `toMap throws IllegalArgumentException when given a primitive value`() {
        val stringValue = faker.lorem().word()
        expectCatching { stringValue.toMap() }
            .isFailure()
            .isA<IllegalArgumentException>()
    }

    @Test
    fun `toMap can convert object containing primitives`() {
        val obj = object {
            val string = faker.lorem().word()
            val int = faker.random().nextInt(10, 20)
            val double = faker.random().nextDouble()
            val bool = faker.random().nextBoolean()
            val instant = Instant.now()
        }

        expectThat(obj.toMap()) {
            get("string").isEqualTo(obj.string)
            get("int").isEqualTo(obj.int)
            get("double").isEqualTo(obj.double)
            get("bool").isEqualTo(obj.bool)
            get("instant").isEqualTo(obj.instant)
        }
    }

    @Test
    fun `toMap can convert object without modifying nested objects`() {
        val user = object {
            val name = object {
                val first = faker.name().firstName()
                val last = faker.name().lastName()
            }
            val address = object {
                val streetAddress = faker.address().streetAddress()
                val city = faker.address().city()
                val state = faker.address().stateAbbr()
                val zip = faker.address().zipCode()
            }
            val birthday = faker.date().birthday().toInstant()
        }

        expectThat(user.toMap()) {
            get("name").isEqualTo(user.name)
            get("address").isEqualTo(user.address)
            get("birthday").isEqualTo(user.birthday)
        }
    }

    @Test
    fun `isPrimitiveType returns true for kotlin primitives`() {
        val primitives = listOf(
            String::class.java,
            Int::class.java,
            Double::class.java,
            Boolean::class.java
        )

        primitives.forEach {
            expectThat(it.isPrimitiveType()).isTrue()
        }
    }

    @Test
    fun `isPrimitiveType returns true for java primitives`() {
        val primitives = listOf(
            java.lang.String::class.java,
            Integer::class.java,
            java.lang.Double::class.java,
            java.lang.Boolean::class.java
        )

        primitives.forEach {
            expectThat(it.isPrimitiveType()).isTrue()
        }
    }

    @Test
    fun `isPrimitiveType returns false for non primitives`() {
        val primitiveTypes = listOf(
            Any::class.java,
            java.lang.Object::class.java,
            TextElement::class.java,
            listOf<String>()::class.java,
            mapOf<String, String>()::class.java
        )

        primitiveTypes.forEach {
            expectThat(it.isPrimitiveType()).isFalse()
        }
    }
}