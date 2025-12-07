package spring.hugme.global.config;

import com.mongodb.client.MongoClient;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.mongodb.IndexMapping;
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {

  @Bean
  public EmbeddingModel embeddingModel(){
    return new AllMiniLmL6V2EmbeddingModel();
  }

  // 질문하신 코드 수정 버전
  @Bean
  public MongoDbEmbeddingStore embeddingStore(EmbeddingModel embeddingModel, MongoClient mongoClient) {

    // "userId"라는 필드로 검색할 거라고 미리 알려줌
    Set<String> metadataFields = new HashSet<>();
    metadataFields.add("dogId");

    return MongoDbEmbeddingStore.builder()
        .fromClient(mongoClient)
        .databaseName("hugme_db")
        .collectionName("dog_memory")
        .indexName("vector_index")
        .createIndex(true)
        .indexMapping(IndexMapping.builder()
            .dimension(384)
            .metadataFieldNames(metadataFields) // [중요] 필터링할 필드 등록
            .build())
        .build();
  }

  @Bean
  public RetrievalAugmentor retrievalAugmentor(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {

    // 1. 기본 검색기 생성 (여기서 필터를 정의하지 않음)
    EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
        .embeddingStore(embeddingStore)
        .embeddingModel(embeddingModel)
        .maxResults(3)
        .minScore(0.6)
        .build();

    return DefaultRetrievalAugmentor.builder()
        .contentRetriever(contentRetriever)
        .build();
  }

}
