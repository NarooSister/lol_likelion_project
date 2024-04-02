package com.example.lol_likelion.duo.service;

import com.example.lol_likelion.duo.entity.Evaluation;
import com.example.lol_likelion.duo.repository.EvaluationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;

    public void createEvaluation(Long postUserId, Long offerUserId, Long postId){
        if(!evaluationRepository.existsByEvaluatorAndBeingEvaluatedAndPostId(postUserId, offerUserId, postId)){
            Evaluation firstEvaluation = new Evaluation();
            firstEvaluation.setEvaluator(postUserId);
            firstEvaluation.setBeingEvaluated(offerUserId);
            firstEvaluation.setStatus("미평가");
            firstEvaluation.setPostId(postId);
            evaluationRepository.save(firstEvaluation);

        }

        if (!evaluationRepository.existsByEvaluatorAndBeingEvaluatedAndPostId(offerUserId, postUserId, postId)){
            Evaluation secondEvaluation = new Evaluation();
            secondEvaluation.setEvaluator(offerUserId);
            secondEvaluation.setBeingEvaluated(postUserId);
            secondEvaluation.setStatus("미평가");
            secondEvaluation.setPostId(postId);
            evaluationRepository.save(secondEvaluation);
        }

    }
}
