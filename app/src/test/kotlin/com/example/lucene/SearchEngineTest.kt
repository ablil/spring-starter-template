package com.example.lucene

import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

data class SearchTestCase(
    val q: String,
    val coupons: Collection<Coupon>,
    val expectedCoupons: Collection<String>,
)

class SearchEngineTest {

    @ParameterizedTest
    @MethodSource("com.example.lucene.FuzzySearchTestData#testCases")
    fun searchTest(testCase: SearchTestCase) {
        val searchEngine = SearchEngine(FuzzySearchStrategy())
        val results = searchEngine.search(testCase.q, testCase.coupons)
        val couponsCodes = results.map { it.token }
        assertThat(couponsCodes).containsAll(testCase.expectedCoupons)
    }

    companion object {
        @JvmStatic
        fun provideTestCases(): Stream<SearchTestCase> =
            Stream.of(
                SearchTestCase(
                    q = "female",
                    FuzzySearchTestData.couponsForFuzzySearch,
                    listOf("token1"),
                ),
                SearchTestCase(
                    q = "male",
                    FuzzySearchTestData.couponsForFuzzySearch,
                    listOf("token2"),
                ),
            )
    }
}
