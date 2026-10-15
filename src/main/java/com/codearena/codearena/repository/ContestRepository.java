
package com.codearena.codearena.repository;

import com.codearena.codearena.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestRepository extends JpaRepository<Contest, Long> {

    List<Contest> findByPublishedTrue();
}
