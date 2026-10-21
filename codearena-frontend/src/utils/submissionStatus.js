
const TERMINAL_STATUSES = new Set([
  "ACCEPTED",
  "REJECTED",
  "COMPLETED",
  "FINISHED",
  "FAILED",
  "ERROR",
  "CANCELLED",
]);

const TERMINAL_VERDICTS = new Set([
  "ACCEPTED",
  "WRONG_ANSWER",
  "COMPILATION_ERROR",
  "RUNTIME_ERROR",
  "TIME_LIMIT_EXCEEDED",
  "MEMORY_LIMIT_EXCEEDED",
  "FAILED",
]);

export function isSubmissionFinished(submission) {
  if (!submission) return false;

  const status = String(submission.status ?? "").toUpperCase();
  const verdict = String(submission.verdict ?? "").toUpperCase();

  return (
    TERMINAL_STATUSES.has(status) ||
    TERMINAL_VERDICTS.has(verdict)
  );
}

export function getSubmissionLabel(submission) {
  if (!submission) return "Waiting for submission...";

  const verdict = submission.verdict;
  if (verdict) {
    return String(verdict).replaceAll("_", " ");
  }

  const status = submission.status ?? "UNKNOWN";
  return String(status).replaceAll("_", " ");
}
