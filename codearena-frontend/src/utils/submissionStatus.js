
const STATUS_CONFIG = {
  QUEUED: {
    label: "Queued",
    color: "text-yellow-400",
  },
  PENDING: {
    label: "Pending",
    color: "text-yellow-400",
  },
  RUNNING: {
    label: "Running",
    color: "text-blue-400",
  },
  ACCEPTED: {
    label: "Accepted",
    color: "text-green-400",
  },
  WRONG_ANSWER: {
    label: "Wrong Answer",
    color: "text-red-400",
  },
  COMPILATION_ERROR: {
    label: "Compilation Error",
    color: "text-red-400",
  },
  RUNTIME_ERROR: {
    label: "Runtime Error",
    color: "text-red-400",
  },
  TIME_LIMIT_EXCEEDED: {
    label: "Time Limit Exceeded",
    color: "text-orange-400",
  },
  MEMORY_LIMIT_EXCEEDED: {
    label: "Memory Limit Exceeded",
    color: "text-orange-400",
  },
  FAILED: {
    label: "Execution Failed",
    color: "text-red-400",
  },
};

export function getStatusConfig(status) {
  const normalized = String(status ?? "").toUpperCase();

  return (
    STATUS_CONFIG[normalized] ?? {
      label: normalized || "Unknown",
      color: "text-gray-400",
    }
  );
}

export function isTerminalStatus(status) {
  return [
    "ACCEPTED",
    "WRONG_ANSWER",
    "COMPILATION_ERROR",
    "RUNTIME_ERROR",
    "TIME_LIMIT_EXCEEDED",
    "MEMORY_LIMIT_EXCEEDED",
    "FAILED",
  ].includes(String(status ?? "").toUpperCase());
}
