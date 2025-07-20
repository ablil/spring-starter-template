package com.example.lucene

import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.ByteBuffersDirectory

class SearchEngine(val strategy: SearchStrategy) {

    fun search(q: String, data: Collection<Coupon>): Collection<Coupon> {
        val documents = data.map { buildDocument(it) }
        val index: ByteBuffersDirectory = buildIndex(documents)

        val reader = DirectoryReader.open(index)
        val searcher = IndexSearcher(reader)
        val query = strategy.createQuery("description", q)

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
        return document
    }
}
