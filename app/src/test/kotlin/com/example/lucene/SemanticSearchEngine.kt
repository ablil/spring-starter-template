package com.example.lucene

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SemanticSearchEngine {

    @Test
    fun searchTest() {
        val embedder = { s: String -> floatArrayOf(s.length.toFloat()) }
        val searchEngine = SearchEngine(SemanticStrategy(embedder))

        val results = searchEngine.search("affordable coffee", coupons)
        val couponCodes = results.map { it.token }

        assertThat(couponCodes).contains("token1")
    }

    companion object {
        val coupons: Collection<Coupon> = listOf(
            Coupon("token1", "cheap coffee"),
            Coupon("token2", "fashion clothes")
        )
    }
}