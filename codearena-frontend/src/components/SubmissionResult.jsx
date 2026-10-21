
import {
  getSubmissionLabel,
  isSubmissionFinished,
} from "../utils/submissionStatus";

export default function SubmissionResult({
  submission,
  loading,
  error,
}) {
  if (loading && !submission) {
    return (
      <div className="rounded-lg border border-slate-700 bg-slate-900 p-4 text-slate-300">
        Submission result load ho raha hai...
      </div>
    );
  }

  if (error && !submission) {
    return (
      <div className="rounded-lg border border-red-800 bg-red-950/40 p-4 text-red-300">
        {error}
      </div>
    );
  }

  if (!submission) return null;

  const finished = isSubmissionFinished(submission);
  const label = getSubmissionLabel(submission);
  const accepted = submission.verdict === "ACCEPTED";

  return (
    <section className="space-y-4 rounded-lg border border-slate-700 bg-slate-900 p-5 text-slate-100">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h2 className="text-lg font-semibold">Submission Result</h2>

        <span
          className={`rounded-full px-3 py-1 text-sm font-medium ${
            accepted
              ? "bg-green-500/15 text-green-400"
              : finished
                ? "bg-red-500/15 text-red-400"
                : "bg-yellow-500/15 text-yellow-300"
          }`}
        >
          {label}
        </span>
      </div>

      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
        <Metric label="Submission ID" value={submission.id} />
        <Metric label="Problem ID" value={submission.problemId} />
        <Metric
          label="Execution Time"
          value={
            submission.executionTime == null
              ? "—"
              : `${submission.executionTime} ms`
          }
        />
        <Metric
          label="Memory Used"
          value={
            submission.memoryUsed == null
              ? "—"
              : `${submission.memoryUsed} KB`
          }
        />
      </div>

      {!finished && (
        <p className="text-sm text-slate-400">
          Judge tumhara code process kar raha hai. Result automatically
          refresh hoga.
        </p>
      )}

      {error && (
        <p className="text-sm text-amber-300">{error}</p>
      )}

      <p className="text-xs text-slate-500">
        Updated:{" "}
        {submission.updatedAt
          ? new Date(submission.updatedAt).toLocaleString()
          : "Not available"}
      </p>
    </section>
  );
}

function Metric({ label, value }) {
  return (
    <div className="rounded-md border border-slate-800 bg-slate-950 p-3">
      <p className="text-sm text-slate-400">{label}</p>
      <p className="mt-1 break-words font-medium">
        {value ?? "—"}
      </p>
    </div>
  );
}
