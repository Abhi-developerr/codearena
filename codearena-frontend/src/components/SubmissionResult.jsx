
import { CheckCircle2, CircleAlert, LoaderCircle } from "lucide-react";
import { getStatusConfig } from "../utils/submissionStatus";

export default function SubmissionResult({ submission, loading, error }) {
  if (loading) {
    return (
      <div className="flex items-center gap-2 p-4 text-sm text-blue-400">
        <LoaderCircle className="animate-spin" size={18} />
        Loading submission result...
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-start gap-2 p-4 text-sm text-red-400">
        <CircleAlert size={18} />
        <p>{error}</p>
      </div>
    );
  }

  if (!submission) {
    return (
      <p className="p-4 text-sm text-gray-500">
        Submit your solution to see the result.
      </p>
    );
  }

  const config = getStatusConfig(submission.status);

  return (
    <section className="space-y-4 p-4">
      <div className="flex items-center gap-2">
        {submission.status === "ACCEPTED" ? (
          <CheckCircle2 size={22} className="text-green-400" />
        ) : (
          <CircleAlert size={22} className={config.color} />
        )}

        <h3 className={`text-lg font-semibold ${config.color}`}>
          {config.label}
        </h3>
      </div>

      {submission.id != null && (
        <p className="text-xs text-gray-500">
          Submission ID: {submission.id}
        </p>
      )}

      {submission.passedTestCases != null &&
        submission.totalTestCases != null && (
          <p className="text-sm text-gray-300">
            Test cases passed: {submission.passedTestCases}/
            {submission.totalTestCases}
          </p>
        )}

      {submission.executionTimeMs != null && (
        <p className="text-sm text-gray-400">
          Execution time: {submission.executionTimeMs} ms
        </p>
      )}

      {submission.memoryUsedMb != null && (
        <p className="text-sm text-gray-400">
          Memory used: {submission.memoryUsedMb} MB
        </p>
      )}
    </section>
  );
}
