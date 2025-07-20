package com.example.lucene

import java.util.function.Function
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.KnnFloatVectorField
import org.apache.lucene.document.StringField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.VectorSimilarityFunction
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.KnnFloatVectorQuery
import org.apache.lucene.search.MatchAllDocsQuery
import org.apache.lucene.search.Query
import org.apache.lucene.store.ByteBuffersDirectory

interface SearchStrategy {
    fun createAnalyzer(): Analyzer

    fun createQuery(field: String, q: String): Query

    fun createField(field: String, value: String): Field

    fun createVectorQuery(field: String, q: String): Query? = null

    fun createVectorField(field: String, value: String): Field? = null
}

class SearchEngine(val strategy: SearchStrategy) {

    fun search(q: String, data: Collection<Coupon>): Collection<Coupon> {
        val documents = data.map { buildDocument(it) }
        val index: ByteBuffersDirectory = buildIndex(documents)

        val reader = DirectoryReader.open(index)
        val searcher = IndexSearcher(reader)
        val query = strategy.createQuery("description", q)

        return doSearch(searcher, query, reader)
    }

    fun semanticSearch(q: String, data: Collection<Coupon>): Collection<Coupon> {
        val documents = data.map { buildDocument(it) }
        val index: ByteBuffersDirectory = buildIndex(documents)

        val reader = DirectoryReader.open(index)
        val searcher = IndexSearcher(reader)
        val query = strategy.createVectorQuery("description_vector", q)
            ?: error("vector query was expected ")

        return doSearch(searcher, query, reader)
    }

    private fun doSearch(
        searcher: IndexSearcher,
        query: Query,
        reader: DirectoryReader
    ): Collection<Coupon> {
        val results = searcher.search(query, 20)
        return if (results.totalHits.value.toInt() == 0) {
            emptyList()
        } else {
            val storedFields = reader.storedFields()
            results.scoreDocs.map { storedFields.document(it.doc) }.mapNotNull { buildCoupon(it) }
        }
    }

    private fun buildCoupon(document: Document): Coupon {
        return Coupon(token = document.get("token"), description = document.get("description"))
    }

    private fun buildIndex(documents: List<Document>): ByteBuffersDirectory {
        val index = ByteBuffersDirectory()
        IndexWriter(index, IndexWriterConfig(strategy.createAnalyzer())).use { writer ->
            documents.forEach { doc -> writer.addDocument(doc) }
            writer.commit()
        }
        return index
    }

    private fun buildDocument(coupon: Coupon): Document {
        val document = Document()
        document.add(strategy.createField("token", coupon.token))
        document.add(strategy.createField("description", coupon.description))

        strategy.createVectorField("description_vector", coupon.description)
            ?.let { document.add(it) }

        return document
    }
}


class SemanticStrategy(val embedder: Function<String, FloatArray>, val topK: Int = 2) : SearchStrategy {
    private val analyzer = StandardAnalyzer()

    override fun createAnalyzer() = analyzer

    override fun createQuery(field: String, q: String) = MatchAllDocsQuery()

    override fun createField(field: String, value: String) =
        StringField(field, value, Field.Store.YES)

    override fun createVectorField(field: String, value: String): Field =
        KnnFloatVectorField(field, embedder.apply(value), VectorSimilarityFunction.DOT_PRODUCT)

    override fun createVectorQuery(field: String, q: String): Query =
        KnnFloatVectorQuery(field, embedder.apply(q), topK)
}