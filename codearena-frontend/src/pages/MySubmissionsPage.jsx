
import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  ArrowLeft,
  ChevronLeft,
  ChevronRight,
  RefreshCw,
  LoaderCircle,
  FileCode2,
} from "lucide-react";

import { submissionService } from "../services/submissionService";
import {
  getSubmissionLabel,
  getSubmissionColor,
} from "../utils/submissionStatus";

function formatDate(value) {
  if (!value) return "—";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) return "—";

  return date.toLocaleString();
}

export default function MySubmissionsPage() {
  const navigate = useNavigate();

  const [data, setData] = useState(null);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchSubmissions = useCallback(async () => {
    try {
      setLoading(true);
      setError("");

      const result = await submissionService.getMySubmissions({
        page,
        size: 10,
      });

      setData(result);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Submissions load nahi ho sakin."
      );
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    const timer = setTimeout(() => {
      void fetchSubmissions();
    }, 0);

    return () => clearTimeout(timer);
  }, [fetchSubmissions]);

  const submissions = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;

  return (
    <div className="min-h-screen bg-arena-bg text-white">
      <header className="flex h-16 items-center justify-between border-b border-arena-border px-5">
        <button
          onClick={() => navigate("/problems")}
          className="flex items-center gap-2 text-sm text-arena-muted hover:text-white"
        >
          <ArrowLeft size={18} />
          Problems
        </button>

        <span className="font-bold">CodeArena</span>

        <button
          onClick={fetchSubmissions}
          disabled={loading}
          className="flex items-center gap-2 rounded-lg border border-arena-border px-3 py-2 text-sm hover:bg-arena-panel disabled:opacity-50"
        >
          <RefreshCw
            size={15}
            className={loading ? "animate-spin" : ""}
          />
          Refresh
        </button>
      </header>

      <main className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
        <div className="mb-7">
          <h1 className="text-3xl font-bold">My Submissions</h1>
          <p className="mt-2 text-sm text-arena-muted">
            Apne submitted solutions aur unke results dekho.
          </p>
        </div>

        {loading && !data ? (
          <div className="flex items-center justify-center gap-3 py-20 text-arena-muted">
            <LoaderCircle className="animate-spin" size={22} />
            Loading submissions...
          </div>
        ) : error && !data ? (
          <div className="rounded-xl border border-red-500/30 bg-arena-panel p-6">
            <p className="text-red-400">{error}</p>

            <button
              onClick={fetchSubmissions}
              className="mt-4 rounded-lg bg-green-500 px-4 py-2 font-medium text-black hover:bg-green-400"
            >
              Try Again
            </button>
          </div>
        ) : submissions.length === 0 ? (
          <div className="rounded-xl border border-arena-border bg-arena-panel p-10 text-center">
            <FileCode2
              size={36}
              className="mx-auto text-arena-muted"
            />

            <h2 className="mt-4 text-lg font-semibold">
              No submissions yet
            </h2>

            <p className="mt-2 text-sm text-arena-muted">
              Kisi problem ko solve karke apni first submission bhejo.
            </p>

            <button
              onClick={() => navigate("/problems")}
              className="mt-5 rounded-lg bg-green-500 px-5 py-2 font-semibold text-black hover:bg-green-400"
            >
              Explore Problems
            </button>
          </div>
        ) : (
          <>
            {error && (
              <p className="mb-4 text-sm text-red-400">{error}</p>
            )}

            <div className="overflow-x-auto rounded-xl border border-arena-border">
              <table className="w-full min-w-[700px] text-left text-sm">
                <thead className="bg-arena-panel text-arena-muted">
                  <tr>
                    <th className="px-5 py-4 font-medium">Submission</th>
                    <th className="px-5 py-4 font-medium">Problem ID</th>
                    <th className="px-5 py-4 font-medium">Language</th>
                    <th className="px-5 py-4 font-medium">Verdict</th>
                    <th className="px-5 py-4 font-medium">Time</th>
                    <th className="px-5 py-4 font-medium">Memory</th>
                    <th className="px-5 py-4 font-medium">Submitted At</th>
                  </tr>
                </thead>

                <tbody>
                  {submissions.map((submission) => (
                    <tr
                      key={submission.id}
                      className="border-t border-arena-border hover:bg-arena-panel/70"
                    >
                      <td className="px-5 py-4">
                        <button
                          onClick={() =>
                            navigate(
                              `/submissions/${submission.id}`
                            )
                          }
                          className="font-medium text-green-400 hover:underline"
                        >
                          #{submission.id}
                        </button>
                      </td>

                      <td className="px-5 py-4">
                        {submission.problemId ?? "—"}
                      </td>

                      <td className="px-5 py-4">
                        {submission.language ?? "—"}
                      </td>

                      <td
                        className={`px-5 py-4 font-semibold ${getSubmissionColor(
                          submission
                        )}`}
                      >
                        {getSubmissionLabel(submission)}
                      </td>

                      <td className="px-5 py-4">
                        {submission.executionTime ?? "—"}
                        {submission.executionTime != null ? " ms" : ""}
                      </td>

                      <td className="px-5 py-4">
                        {submission.memoryUsed ?? "—"}
                        {submission.memoryUsed != null ? " KB" : ""}
                      </td>

                      <td className="whitespace-nowrap px-5 py-4 text-arena-muted">
                        {formatDate(submission.createdAt)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className="mt-5 flex items-center justify-between gap-4">
              <p className="text-sm text-arena-muted">
                Page {page + 1} of {Math.max(totalPages, 1)}
              </p>

              <div className="flex gap-2">
                <button
                  onClick={() => setPage((current) => current - 1)}
                  disabled={page === 0 || loading}
                  className="flex items-center gap-1 rounded-lg border border-arena-border px-3 py-2 text-sm hover:bg-arena-panel disabled:cursor-not-allowed disabled:opacity-40"
                >
                  <ChevronLeft size={16} />
                  Previous
                </button>

                <button
                  onClick={() => setPage((current) => current + 1)}
                  disabled={
                    loading ||
                    totalPages === 0 ||
                    page >= totalPages - 1
                  }
                  className="flex items-center gap-1 rounded-lg border border-arena-border px-3 py-2 text-sm hover:bg-arena-panel disabled:cursor-not-allowed disabled:opacity-40"
                >
                  Next
                  <ChevronRight size={16} />
                </button>
              </div>
            </div>
          </>
        )}
      </main>
    </div>
  );
}
