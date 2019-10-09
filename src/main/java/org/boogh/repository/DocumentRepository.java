package org.boogh.repository;

import org.boogh.domain.Document;
import org.boogh.domain.enumeration.DocType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Document entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("select document from Document document where document.type = ?1")
    List<Document> findDocumentByType(DocType type);
}
