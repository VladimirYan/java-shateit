package ru.practicum.shareit.item.repository.jpa;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.comment.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItem_Id(Long itemId);

    @Query("""
        SELECT c
        FROM Comment c
        WHERE c.item.owner.id = :userId
        """)
    List<Comment> findAllByItemsUserId(@Param("userId") Long userId, Sort sort);
}
