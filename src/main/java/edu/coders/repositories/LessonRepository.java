package edu.coders.repositories;

import edu.coders.entities.Lesson;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("SELECT l.title FROM Lesson l WHERE LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<String> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Lesson findByTitle(String title);
}
