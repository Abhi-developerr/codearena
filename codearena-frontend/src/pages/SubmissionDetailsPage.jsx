
import { useCallback, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  ArrowLeft,
  Clock3,
  HardDrive,
  LoaderCircle,
  RefreshCw,
  FileCode2,
} from "lucide-react";
import Editor from "@monaco-editor/react";

import { submissionService } from "../services/submissionService";
import {
  getSubmissionLabel,
  getSubmissionColor,
} from "../utils/submissionStatus";
import { getLanguageConfig } from "../constants/languages";

export default function SubmissionDetailsPage() {
  const { submissionId } = useParams();
  const navigate = useNavigate();

  const [submission, setSubmission] = useState(null);
  const [sourceCode, setSourceCode] = useState("");
  const [loading, setLoading] = useState(true);
  const [codeLoading, setCodeLoading] = useState(true);
  const [error, setError] = useState("");
  const [codeError, setCodeError] = useState("");

  const fetchDetails = useCallback(async () => {
    try {
      setLoading(true);
      setError("");

      const result =
        await submissionService.getSubmission(submissionId);

      setSubmission(result);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Submission details load nahi ho sake."
      );
    } finally {
      setLoading(false);
    }
  }, [submissionId]);

  const fetchSourceCode = useCallback(async () => {
    try {
      setCodeLoading(true);
      setCodeError("");

      const response = await submissionService.getSubmissionCode(
        submissionId
      );

      // Endpoint agar plain text return kare, toh response.data string hoga.
      // Agar object return kare, toh common source-code fields check karo.
      const code =
        typeof response === "string"
          ? response
          : response?.sourceCode ?? response?.code ?? "";

      setSourceCode(code);

      if (!code) {
        setCodeError("Source code response mein available nahi hai.");
      }
    } catch (err) {
      setCodeError(
        err.response?.data?.message ||
          "Source code load nahi ho saka."
      );
    } finally {
      setCodeLoading(false);
    }
  }, [submissionId]);

  useEffect(() => {
    fetchDetails();
    fetchSourceCode();
  }, [fetchDetails, fetchSourceCode]);

  if (loading && !submission) {
    return (
      <div className="flex min-h-screen items-center justify-center gap-3 bg-arena-bg text-white">
        <LoaderCircle className="animate-spin" size={22} />
        Loading submission...
      </div>
    );
  }

  if (error && !submission) {
    return (
      <div className="min-h-screen bg-arena-bg p-8 text-white">
        <button
          onClick={() => navigate("/my-submissions")}
          className="mb-6 flex items-center gap-2 text-arena-muted hover:text-white"
        >
          <ArrowLeft size={18} />
          My Submissions
        </button>

        <div className="max-w-xl rounded-xl border border-red-500/30 bg-arena-panel p-6">
          <p className="text-red-400">{error}</p>

          <button
            onClick={fetchDetails}
            className="mt-4 rounded-lg bg-green-500 px-4 py-2 font-semibold text-black"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  const language = getLanguageConfig(
    submission?.language ?? "JAVA"
  );

  return (
    <div className="min-h-screen bg-arena-bg text-white">
      <header className="flex h-16 items-center justify-between border-b border-arena-border px-5">
        <button
          onClick={() => navigate("/my-submissions")}
          className="flex items-center gap-2 text-sm text-arena-muted hover:text-white"
        >
          <ArrowLeft size={18} />
          My Submissions
        </button>

        <span className="font-bold">CodeArena</span>

        <span className="text-sm text-arena-muted">
          Submission #{submission?.id ?? submissionId}
        </span>
      </header>

      <main className="mx-auto max-w-6xl space-y-6 px-4 py-8 sm:px-6">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div>
            <h1 className="text-3xl font-bold">
              Submission Details
            </h1>

            <p className="mt-2 text-sm text-arena-muted">
              Problem ID: {submission?.problemId ?? "—"}
            </p>
          </div>

          <button
            onClick={() => {
              fetchDetails();
              fetchSourceCode();
            }}
            disabled={loading || codeLoading}
            className="flex items-center gap-2 rounded-lg border border-arena-border px-4 py-2 text-sm hover:bg-arena-panel disabled:opacity-50"
          >
            <RefreshCw size={16} />
            Refresh
          </button>
        </div>

        {error && (
          <p className="text-sm text-red-400">{error}</p>
        )}

        <section className="rounded-xl border border-arena-border bg-arena-panel p-5">
          <div className="flex flex-wrap items-center justify-between gap-4">
            <div>
              <p className="mb-2 text-sm text-arena-muted">
                Verdict
              </p>

              <h2
                className={`text-2xl font-bold ${getSubmissionColor(
                  submission
                )}`}
              >
                {getSubmissionLabel(submission)}
              </h2>

              <p className="mt-2 text-sm text-arena-muted">
                Status: {submission?.status ?? "—"}
              </p>
            </div>

            <div className="flex flex-wrap gap-3">
              <div className="min-w-36 rounded-lg border border-arena-border p-4">
                <div className="mb-2 flex items-center gap-2 text-sm text-arena-muted">
                  <Clock3 size={16} />
                  Execution Time
                </div>

                <p className="font-semibold">
                  {submission?.executionTime ?? "—"}
                  {submission?.executionTime != null ? " ms" : ""}
                </p>
              </div>

              <div className="min-w-36 rounded-lg border border-arena-border p-4">
                <div className="mb-2 flex items-center gap-2 text-sm text-arena-muted">
                  <HardDrive size={16} />
                  Memory
                </div>

                <p className="font-semibold">
                  {submission?.memoryUsed ?? "—"}
                  {submission?.memoryUsed != null ? " KB" : ""}
                </p>
              </div>
            </div>
          </div>

          <div className="mt-5 border-t border-arena-border pt-4 text-sm text-arena-muted">
            <p>Language: {submission?.language ?? "—"}</p>
            <p className="mt-2">
              Submitted:{" "}
              {submission?.createdAt
                ? new Date(submission.createdAt).toLocaleString()
                : "—"}
            </p>
            <p className="mt-2">
              Last Updated:{" "}
              {submission?.updatedAt
                ? new Date(submission.updatedAt).toLocaleString()
                : "—"}
            </p>
          </div>
        </section>

        <section className="overflow-hidden rounded-xl border border-arena-border">
          <div className="flex items-center justify-between border-b border-arena-border bg-arena-panel px-5 py-4">
            <h2 className="flex items-center gap-2 font-semibold">
              <FileCode2 size={18} />
              Submitted Source Code
            </h2>

            <span className="text-xs text-arena-muted">
              {submission?.language ?? "JAVA"}
            </span>
          </div>

          {codeError && (
            <div className="flex items-center justify-between gap-4 p-4">
              <p className="text-sm text-red-400">{codeError}</p>

              <button
                onClick={fetchSourceCode}
                className="shrink-0 rounded-lg border border-arena-border px-3 py-2 text-sm hover:bg-arena-panel"
              >
                Retry
              </button>
            </div>
          )}

          {codeLoading ? (
            <div className="flex items-center gap-3 p-6 text-arena-muted">
              <LoaderCircle className="animate-spin" size={20} />
              Loading source code...
            </div>
          ) : sourceCode ? (
            <Editor
              height="450px"
              language={language.editorLanguage}
              theme="vs-dark"
              value={sourceCode}
              options={{
                readOnly: true,
                minimap: { enabled: false },
                fontSize: 14,
                automaticLayout: true,
                scrollBeyondLastLine: false,
                padding: { top: 16 },
              }}
            />
          ) : (
            !codeError && (
              <div className="p-6 text-sm text-arena-muted">
                No source code available.
              </div>
            )
          )}
        </section>
      </main>
    </div>
  );
}
