package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    //할일과 유저는 ManyToOne 관계이다
    //1개의 쿼리문으로 N개의 할일을 조회하고 N개의 할일에 대한 유저를 조회하며 N+1 문제가 발생하게 된다
    //Fetch Join은 연관관계를 한 번에 조회하여 N+1 문제를 방지한다
    //@Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")

    //EntityGrape는 Fetch Join을 직접 작성하지 않아도 된다.
    @Query("SELECT t FROM Todo t ORDER BY t.modifiedAt DESC")
    @EntityGraph(attributePaths = {"user"})
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN FETCH t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    int countById(Long todoId);
}
