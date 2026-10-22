
import {
  CheckCircle2,
  XCircle,
  Clock3,
  HardDrive,
  LoaderCircle,
} from "lucide-react";

import {
  getSubmissionLabel,
  getSubmissionColor,
} from "../utils/submissionStatus";

export default function SubmissionResult({
  submission,
  loading,
  error,
}) {
  if (loading && !submission) {
    return (
      <div className="flex items-center gap-3 p-5 text-slate-300">
        <LoaderCircle className="animate-spin" size={20} />
        Judging your submission...
      </div>
    );
  }

  if (error && !submission) {
    return (
      <div className="m-4 rounded-lg border border-red-500/30 bg-red-500/10 p-4">
        <p className="font-medium text-red-400">
          {error}
        </p>
      </div>
    );
  }

  if (!submission) {
    return (
      <div className="p-5 text-sm text-arena-muted">
        Submit your code to see the result here.
      </div>
    );
  }

  const completed = submission.status === "COMPLETED";
  const accepted =
    completed && submission.verdict === "ACCEPTED";

  const Icon = !completed
    ? LoaderCircle
    : accepted
      ? CheckCircle2
      : XCircle;

  return (
    <div className="space-y-4 p-5">
      <div className="flex items-start gap-3">
        <Icon
          size={24}
          className={`${getSubmissionColor(submission)} ${
            !completed ? "animate-spin" : ""
          }`}
        />

        <div>
          <h3
            className={`text-lg font-semibold ${getSubmissionColor(
              submission
            )}`}
          >
            {getSubmissionLabel(submission)}
          </h3>

          <p className="mt-1 text-sm text-arena-muted">
            Submission ID: {submission.id}
          </p>

          {!completed && (
            <p className="mt-1 text-sm text-arena-muted">
              Your code is being judged. Please wait...
            </p>
          )}
        </div>
      </div>

      {completed && (
        <div className="grid grid-cols-2 gap-3">
          <div className="rounded-lg border border-arena-border bg-arena-panel p-4">
            <div className="mb-2 flex items-center gap-2 text-sm text-arena-muted">
              <Clock3 size={16} />
              Execution Time
            </div>

            <p className="font-semibold text-white">
              {submission.executionTime ?? "—"}
              {submission.executionTime != null ? " ms" : ""}
            </p>
          </div>

          <div className="rounded-lg border border-arena-border bg-arena-panel p-4">
            <div className="mb-2 flex items-center gap-2 text-sm text-arena-muted">
              <HardDrive size={16} />
              Memory Used
            </div>

            <p className="font-semibold text-white">
              {submission.memoryUsed ?? "—"}
              {submission.memoryUsed != null ? " KB" : ""}
            </p>
          </div>
        </div>
      )}

      {error && (
        <p className="text-sm text-yellow-400">
          {error}
        </p>
      )}
    </div>
  );
}
