
export function isSubmissionFinished(submission) {
  return submission?.status === "COMPLETED";
}

export function getSubmissionLabel(submission) {
  if (!submission) {
    return "Waiting for submission...";
  }

  if (submission.status === "QUEUED") {
    return "Queued";
  }

  if (submission.status === "RUNNING") {
    return "Running";
  }

  if (submission.status !== "COMPLETED") {
    return submission.status || "Unknown status";
  }

  const labels = {
    ACCEPTED: "Accepted",
    WRONG_ANSWER: "Wrong Answer",
    TIME_LIMIT_EXCEEDED: "Time Limit Exceeded",
    MEMORY_LIMIT_EXCEEDED: "Memory Limit Exceeded",
    RUNTIME_ERROR: "Runtime Error",
    COMPILATION_ERROR: "Compilation Error",
  };

  return labels[submission.verdict] || "Unknown Verdict";
}

export function getSubmissionColor(submission) {
  if (submission?.status !== "COMPLETED") {
    return "text-yellow-400";
  }

  if (submission.verdict === "ACCEPTED") {
    return "text-green-400";
  }

  return "text-red-400";
}
