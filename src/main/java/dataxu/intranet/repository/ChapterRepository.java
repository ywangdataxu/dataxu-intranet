package dataxu.intranet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dataxu.intranet.entity.Chapter;

public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
}
