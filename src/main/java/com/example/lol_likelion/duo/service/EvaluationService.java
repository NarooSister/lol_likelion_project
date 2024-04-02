package com.example.lol_likelion.duo.service;

import com.example.lol_likelion.duo.dto.EvaluationDto;
import com.example.lol_likelion.duo.dto.PostDto;
import com.example.lol_likelion.duo.entity.Evaluation;
import com.example.lol_likelion.duo.entity.Post;
import com.example.lol_likelion.duo.repository.EvaluationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<EvaluationDto> readMyEvaluation(Long enterUserId){

        List<EvaluationDto> evaluationDto = new ArrayList<>();
        for (Evaluation evaluation : evaluationRepository.findAllByEvaluator(enterUserId)){
            evaluationDto.add(EvaluationDto.fromEntity(evaluation));
        }
        return evaluationDto;
    }

    public void updateStatus(Long evaluationId, String status){
        Evaluation evaluation = evaluationRepository.findById(evaluationId).orElseThrow();

        evaluation.setStatus(status);
        evaluationRepository.save(evaluation);
    }
}
