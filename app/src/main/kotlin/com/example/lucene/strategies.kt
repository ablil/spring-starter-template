package com.example.lucene

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.custom.CustomAnalyzer
import org.apache.lucene.analysis.de.GermanAnalyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Field
import org.apache.lucene.document.TextField
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.FuzzyQuery
import org.apache.lucene.search.PhraseQuery
import org.apache.lucene.search.PrefixQuery
import org.apache.lucene.search.Query
import org.apache.lucene.util.ClasspathResourceLoader


class FuzzySearchStrategy(val maxEdit: Int = FuzzyQuery.defaultMaxEdits) : SearchStrategy {
    override fun createAnalyzer(): Analyzer = StandardAnalyzer()

    override fun createQuery(field: String, q: String): Query = FuzzyQuery(Term(field, q), maxEdit)

    override fun createField(field: String, value: String): Field =
        TextField(field, value, Field.Store.YES)
}

/**
 * FuzzyQuery works only with single terms, any multi-word query is considered one term, This custom
 * strategy split the query into multiple terms then combine them with boolean operator
 */
open class FuzzyMultiTermStrategy : SearchStrategy {
    override fun createAnalyzer(): Analyzer = StandardAnalyzer()

    override fun createQuery(field: String, q: String): Query {
        val tokens = q.lowercase().split("\\s+".toRegex())
        return with(BooleanQuery.Builder()) {
            tokens
                .map { FuzzyQuery(Term(field, it)) }
                .forEach { add(it, BooleanClause.Occur.SHOULD) }
            build()
        }
    }

    override fun createField(field: String, value: String): Field =
        TextField(field, value, Field.Store.YES)
}

class GermanFuzzyMultiTermStrategy : FuzzyMultiTermStrategy() {
    override fun createAnalyzer(): Analyzer = GermanAnalyzer()
}

/** custom analyzer with synonyms enhancement that you can provide */
class SynonymSearchStrategy(val synonymsFilename: String = "synonyms.txt") : SearchStrategy {

    val analyzer =
        CustomAnalyzer.builder(ClasspathResourceLoader(this::class.java.classLoader))
            .withTokenizer("standard")
            .addTokenFilter("lowercase")
            .addTokenFilter(
                "synonymgraph",
                mapOf("synonyms" to synonymsFilename, "ignoreCase" to "true"),
            )
            .addTokenFilter("stop")
            .addTokenFilter("porterstem")
            .build()

    override fun createAnalyzer(): Analyzer = analyzer

    override fun createQuery(field: String, q: String): Query =
        QueryParser(field, createAnalyzer()).parse(q)

    override fun createField(field: String, value: String): Field =
        TextField(field, value, Field.Store.YES)
}

/** Basic, general-purpose search using StandardAnalyzer and QueryParser. */
class StandardSearchStrategy : SearchStrategy {
    override fun createAnalyzer(): Analyzer = StandardAnalyzer()

    override fun createQuery(field: String, q: String): Query =
        QueryParser(field, createAnalyzer()).parse(q)

    override fun createField(field: String, value: String): Field =
        TextField(field, value, Field.Store.YES)
}

/**
 * Designed for searching exact phrases. It's useful when the order and proximity of words are
 * critical, like in names, product titles, or specific sentences.
 */
class PhraseSearchStrategy(private val slop: Int = 0) : SearchStrategy {
    override fun createAnalyzer(): Analyzer = StandardAnalyzer()

    override fun createQuery(field: String, q: String): Query {
        val terms = q.lowercase().split("\\s+".toRegex())
        val phraseBuilder = PhraseQuery.Builder()
        terms.forEach { term -> phraseBuilder.add(Term(field, term)) }
        phraseBuilder.setSlop(slop)
        return phraseBuilder.build()
    }

    override fun createField(field: String, value: String): Field =
        TextField(field, value, Field.Store.YES)
}

/**
 * Allows users to find documents where a field's terms start with a given prefix. Useful for
 * "type-ahead" suggestions or when users might not know the exact spelling of the end of a word.
 */
class PrefixSearchStrategy : SearchStrategy {
    override fun createAnalyzer(): Analyzer = StandardAnalyzer()

    override fun createQuery(field: String, q: String): Query =
        PrefixQuery(Term(field, q.lowercase()))

    override fun createField(field: String, value: String): Field =
        TextField(field, value, Field.Store.YES)
}
