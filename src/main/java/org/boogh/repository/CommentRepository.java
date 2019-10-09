package org.boogh.repository;

import org.boogh.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    @Query("select comment from Comment comment where comment.commenter.login = ?#{principal.username}")
    List<Comment> findByCommenterIsCurrentUser();

    @Query("select comment from Comment comment where comment.parent.id = ?1")
    List<Comment> findAllChildReports(long id);

}
