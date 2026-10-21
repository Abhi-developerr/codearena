
import { useEffect, useState } from "react";
import { submissionService } from "../services/submissionService";
import { isSubmissionFinished } from "../utils/submissionStatus";

export function useSubmissionPolling(submissionId, enabled = true) {
  const [submission, setSubmission] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!submissionId || !enabled) return;

    let cancelled = false;
    let timerId;
    let attempts = 0;

    const maxAttempts = 60;
    const intervalMs = 2000;

    async function poll() {
      if (cancelled) return;

      setLoading(true);

      try {
        const data = await submissionService.getSubmission(
          submissionId
        );

        if (cancelled) return;

        setSubmission(data);
        setError("");

        if (isSubmissionFinished(data)) {
          setLoading(false);
          return;
        }

        attempts += 1;

        if (attempts >= maxAttempts) {
          setError(
            "Result abhi pending hai. Refresh karke dobara check karo."
          );
          setLoading(false);
          return;
        }

        timerId = setTimeout(poll, intervalMs);
      } catch (err) {
        if (cancelled) return;

        setError(
          err.response?.data?.message ||
            "Submission status fetch nahi ho saka."
        );
        setLoading(false);
      }
    }

    setSubmission(null);
    setError("");
    poll();

    return () => {
      cancelled = true;
      clearTimeout(timerId);
    };
  }, [submissionId, enabled]);

  return { submission, loading, error };
}
