
import { useEffect, useState } from "react";
import { submissionService } from "../services/submissionService";
import { isSubmissionFinished } from "../utils/submissionStatus";

const POLLING_INTERVAL = 2000;
const MAX_ATTEMPTS = 60;

export default function useSubmissionPolling(submissionId) {
  const [submission, setSubmission] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;
    let timer;
    let attempts = 0;

    async function pollSubmission() {
      if (cancelled) return;

      try {
        attempts += 1;

        const data = await submissionService.getSubmission(submissionId);

        if (cancelled) return;

        setSubmission(data);

        if (isSubmissionFinished(data)) {
          setLoading(false);
          return;
        }

        if (attempts >= MAX_ATTEMPTS) {
          setLoading(false);
          setError(
            "Result abhi pending hai. Please refresh or check your submissions."
          );
          return;
        }

        timer = setTimeout(pollSubmission, POLLING_INTERVAL);
      } catch (err) {
        if (cancelled) return;

        setLoading(false);
        setError(
          err.response?.data?.message ||
            "Submission status fetch nahi ho saka."
        );
      }
    }

    timer = setTimeout(() => {
      if (!submissionId) {
        setSubmission(null);
        setLoading(false);
        setError("");
        return;
      }

      setSubmission(null);
      setLoading(true);
      setError("");
      pollSubmission();
    }, 0);

    return () => {
      cancelled = true;
      clearTimeout(timer);
    };
  }, [submissionId]);

  return { submission, loading, error };
}
