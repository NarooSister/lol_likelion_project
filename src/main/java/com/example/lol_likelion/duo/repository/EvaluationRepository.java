package com.example.lol_likelion.duo.repository;

import com.example.lol_likelion.duo.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    boolean existsByEvaluatorAndBeingEvaluatedAndPostId(Long evaluator, Long beingEvaluated, Long postId);

    List<Evaluation> findAllByEvaluator(Long evaluator);
}
