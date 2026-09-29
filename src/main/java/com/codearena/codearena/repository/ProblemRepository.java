public interface ProblemRepository
        extends JpaRepository<Problem, Long> {

    Page<Problem> findByDifficulty(
            Difficulty difficulty,
            Pageable pageable
    );

    Page<Problem> findByTitleContainingIgnoreCase(
            String title,
            Pageable pageable
    );

    Page<Problem> findByTitleContainingIgnoreCaseAndDifficulty(
            String title,
            Difficulty difficulty,
            Pageable pageable
    );
}