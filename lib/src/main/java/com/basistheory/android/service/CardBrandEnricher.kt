package com.basistheory.android.service

import com.basistheory.android.constants.CardBrands

class CardBrandEnricher {

    object CardMasks {
        const val MASK_4_8_12GAPS_19LENGTH = "#### #### #### #######"
        const val MASK_4_8_12GAPS_16LENGTH = "#### #### #### ####"
    }

    object CvcMasks {
        const val THREE_DIGIT = "###"
        const val FOUR_DIGIT = "####"
    }

    class CardDetails(
        var brand: String = "",
        var identifierRanges: List<Pair<String, String?>>,
        var validLengths: IntArray,
        var cvcMask: String = "",
        var cardMask: String = ""
    )

    class CardResult(
        var cardDetails: CardDetails?,
        var cardLength: Int = -1,
        var identifierLength: Int = -1,
    ) {
        val complete: Boolean
            get() = cardDetails?.validLengths?.contains(cardLength) ?: false
    }

    private val cardBrands = listOf(
        CardDetails(
            CardBrands.VISA.label,
            listOf("4" to null),
            intArrayOf(16, 18, 19),
            CvcMasks.THREE_DIGIT,
            CardMasks.MASK_4_8_12GAPS_19LENGTH
        ),

        CardDetails(
            CardBrands.MASTERCARD.label, listOf(
                "51" to "55",
                "2221" to "2229",
                "223" to "229",
                "23" to "26",
                "270" to "271",
                "2720" to null
            ), intArrayOf(16), CvcMasks.THREE_DIGIT, CardMasks.MASK_4_8_12GAPS_16LENGTH
        ),

        CardDetails(
            CardBrands.AMERICAN_EXPRESS.label, listOf(
                "34" to null,
                "37" to null
            ), intArrayOf(15), CvcMasks.FOUR_DIGIT, "#### ###### #####"
        ),

        CardDetails(
            CardBrands.DINERS_CLUB.label, listOf(
                "36" to null,
                "38" to "39",
                "300" to "305"
            ), intArrayOf(14, 16, 19), CvcMasks.THREE_DIGIT, "#### ###### #########"
        ),

        CardDetails(
            CardBrands.DISCOVER.label, listOf(
                "65" to null,
                "6011" to "39",
                "644" to "649"
            ), intArrayOf(16, 19), CvcMasks.THREE_DIGIT, CardMasks.MASK_4_8_12GAPS_19LENGTH
        ),

        CardDetails(
            CardBrands.JCB.label, listOf(
                "2131" to null,
                "1800" to "39",
                "3528" to "3589"
            ), intArrayOf(16, 17, 18, 19), CvcMasks.THREE_DIGIT, CardMasks.MASK_4_8_12GAPS_19LENGTH
        ),

        CardDetails(
            CardBrands.UNION_PAY.label, listOf(
                "620" to null,
                "6270" to null,
                "6272" to null,
                "6276" to null,
                "6291" to null,
                "6292" to null,
                "810" to null,
                "62100" to "62197",
                "62200" to "62205",
                "622010" to "622999",
                "62207" to "62209",
                "623" to "626",
                "627700" to "627779",
                "627781" to "627799",
                "6282" to "6289",
                "8110" to "8171",
            ), intArrayOf(14, 15, 16, 17, 18, 19), CvcMasks.THREE_DIGIT, CardMasks.MASK_4_8_12GAPS_19LENGTH
        ),

        CardDetails(
            CardBrands.MAESTRO.label, listOf(
                "493698" to null,
                "63" to null,
                "67" to null,
                "6" to null,
                "500000" to "504174",
                "504176" to "506698",
                "506779" to "508999",
                "56" to "59",
            ), intArrayOf(12, 13, 14, 15, 16, 17, 18, 19), CvcMasks.THREE_DIGIT, CardMasks.MASK_4_8_12GAPS_19LENGTH
        ),

        CardDetails(
            CardBrands.ELO.label, listOf(
                "401178" to null,
                "401179" to null,
                "438935" to null,
                "457631" to null,
                "457632" to null,
                "431274" to null,
                "451416" to null,
                "457393" to null,
                "504175" to null,
                "627780" to null,
                "636297" to null,
                "636368" to null,
                "506699" to "506778",
                "509000" to "509999",
                "650031" to "650033",
                "650035" to "650051",
                "650405" to "650439",
                "650485" to "650538",
                "650541" to "650598",
                "650700" to "650718",
                "650720" to "650727",
                "650901" to "650978",
                "651652" to "651679",
                "655000" to "655019",
                "655021" to "655058",
            ), intArrayOf(16), CvcMasks.THREE_DIGIT, CardMasks.MASK_4_8_12GAPS_16LENGTH
        ),

        CardDetails(
            CardBrands.MIR.label,
            listOf("2200" to "2204"),
            intArrayOf(16, 17, 18, 19),
            CvcMasks.THREE_DIGIT,
            CardMasks.MASK_4_8_12GAPS_19LENGTH
        ),

        CardDetails(
            CardBrands.HIPER.label, listOf(
                "637095" to null,
                "63737423" to null,
                "63743358" to null,
                "637568" to null,
                "637599" to null,
                "637609" to null,
                "637612" to null,
            ), intArrayOf(16), CvcMasks.THREE_DIGIT, CardMasks.MASK_4_8_12GAPS_16LENGTH
        ),

        CardDetails(
            CardBrands.HIPERCARD.label, listOf(
                "606282" to null,
            ), intArrayOf(16), CvcMasks.THREE_DIGIT, CardMasks.MASK_4_8_12GAPS_16LENGTH
        )
    )

    fun evaluateCard(number: String?): CardResult {
        if (number.isNullOrBlank()) return CardResult(null)

        var bestMatch = CardResult(null)

        cardBrands.forEach { cardDetails ->
            cardDetails.identifierRanges.forEach { range ->
                val possibleMatch = number.take(range.first.length)

                if (!range.second.isNullOrBlank() && range.first.toInt() <= possibleMatch.toInt() && possibleMatch.toInt() <= range.second!!.toInt()) {
                    bestMatch = chooseBestMatch(bestMatch, cardDetails, possibleMatch, number)
                } else if (range.first == possibleMatch) {
                    bestMatch = chooseBestMatch(bestMatch, cardDetails, possibleMatch, number)
                }
            }
        }

        return bestMatch
    }

    private fun chooseBestMatch(
        currentBestMatch: CardResult,
        cardDetails: CardDetails,
        identifierMatch: String,
        number: String
    ): CardResult =
        if (currentBestMatch.identifierLength < identifierMatch.length) CardResult(cardDetails, number.length, identifierMatch.length)
        else currentBestMatch
}