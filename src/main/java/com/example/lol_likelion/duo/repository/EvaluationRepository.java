package com.example.lol_likelion.duo.repository;

import com.example.lol_likelion.duo.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    boolean existsByEvaluatorAndBeingEvaluatedAndPostId(Long evaluator, Long beingEvaluated, Long postId);
}
