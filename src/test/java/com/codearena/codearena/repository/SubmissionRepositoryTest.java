
package com.codearena.codearena.repository;

import com.codearena.codearena.entity.Difficulty;
import com.codearena.codearena.entity.Problem;
import com.codearena.codearena.entity.Role;
import com.codearena.codearena.entity.Submission;
import com.codearena.codearena.entity.SubmissionLanguage;
import com.codearena.codearena.entity.SubmissionStatus;
import com.codearena.codearena.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SubmissionRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Test
    void findByStatusReturnsOnlyMatchingSubmissions() {

        User user = new User();
        user.setName("Repository Test User");
        user.setEmail("submission-test-" + UUID.randomUUID() + "@example.com");
        user.setPassword("test-password");
        user.setRole(Role.USER);

        User savedUser = userRepository.saveAndFlush(user);

        Problem problem = new Problem();
        problem.setTitle("Submission Status Test " + UUID.randomUUID());
        problem.setDescription("Testing submission status filtering");
        problem.setDifficulty(Difficulty.EASY);

        Problem savedProblem = problemRepository.saveAndFlush(problem);

        Submission queuedSubmission =
                createSubmission(savedUser, savedProblem, SubmissionStatus.QUEUED);

        Submission runningSubmission =
                createSubmission(savedUser, savedProblem, SubmissionStatus.RUNNING);

        Submission completedSubmission =
                createSubmission(savedUser, savedProblem, SubmissionStatus.COMPLETED);

        submissionRepository.save(queuedSubmission);
        submissionRepository.save(runningSubmission);
        submissionRepository.save(completedSubmission);
        submissionRepository.flush();

        List<Submission> result =
                submissionRepository.findByStatus(SubmissionStatus.RUNNING);

        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(
                submission -> submission.getStatus() == SubmissionStatus.RUNNING
        ));
        assertTrue(result.stream().anyMatch(
                submission -> submission.getId().equals(runningSubmission.getId())
        ));
        assertFalse(result.stream().anyMatch(
                submission -> submission.getId().equals(queuedSubmission.getId())
        ));
        assertFalse(result.stream().anyMatch(
                submission -> submission.getId().equals(completedSubmission.getId())
        ));

        assertEquals(1, result.stream()
                .filter(submission -> submission.getId().equals(runningSubmission.getId()))
                .count());
    }

    private Submission createSubmission(
            User user,
            Problem problem,
            SubmissionStatus status) {

        Submission submission = new Submission();
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setLanguage(SubmissionLanguage.JAVA);
        submission.setSourceCode("public class Main { public static void main(String[] args) {} }");
        submission.setStatus(status);

        return submission;
    }
}
