package com.cooksys.quiz_api.services.impl;

import java.util.List;

import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.exceptions.ResourceNotFoundException;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.AnswerRepository;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

  private final QuizRepository quizRepository;
  private final QuestionRepository questionRepository;
  private final AnswerRepository answerRepository;
  private final QuizMapper quizMapper;

  @Override
  public List<QuizResponseDto> getAllQuizzes() {
    return quizMapper.entitiesToDtos(quizRepository.findAll());
  }

  @Override
  public QuizResponseDto getQuizById(Long id) {
    return quizMapper.entityToDto(quizRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + id)));
  }

  @Override
  public QuizResponseDto createQuiz(QuizRequestDto quizRequestDto) {
    Quiz quiz = quizMapper.requestDtoToEntity(quizRequestDto);
    Quiz savedQuiz = quizRepository.saveAndFlush(quiz);
    return quizMapper.entityToDto(savedQuiz);
  }

  @Override
  public QuizResponseDto deleteQuiz(Long id) {
    Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + id));

    // Delete associated answers first
    for (Question question : quiz.getQuestions()) {
      answerRepository.deleteAll(question.getAnswers());
    }

    // Delete associated questions
    questionRepository.deleteAll(quiz.getQuestions());

    // Now delete the quiz
    quizRepository.delete(quiz);

    return quizMapper.entityToDto(quiz);
  }

  @Override
  public QuizResponseDto renameQuiz(Long id, String newName) {
    Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + id));
    quiz.setName(newName);
    Quiz updatedQuiz = quizRepository.saveAndFlush(quiz);
    return quizMapper.entityToDto(updatedQuiz);
  }
}

