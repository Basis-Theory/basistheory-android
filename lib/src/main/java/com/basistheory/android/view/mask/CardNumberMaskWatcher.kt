package com.basistheory.android.view.mask

import com.basistheory.android.service.CardBrandEnricher

internal class CardNumberMaskWatcher(mask: List<Any>) : MaskWatcher(mask) {

    private var cardBrandEnricher: CardBrandEnricher = CardBrandEnricher()

    override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
        val cardResult = cardBrandEnricher.evaluateCard(charSequence?.toString())
        if (cardResult?.cardDetails?.cardMask != null)
            mask = Mask(cardResult.cardDetails!!.cardMask.toList())

        if (isApplyingMask || charSequence.isNullOrEmpty()) return

        val action = if (before > 0 && count == 0) Action.DELETE else Action.INSERT

        result = mask.apply(charSequence, action)
    }
}