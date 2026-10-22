
import { useState } from "react";
import Editor from "@monaco-editor/react";
import {
  Play,
  Send,
  RotateCcw,
  ChevronDown,
  FileCode2,
  Terminal,
  CheckCircle2,
} from "lucide-react";

import { submissionService } from "../services/submissionService";
import { LANGUAGES, getLanguageConfig } from "../constants/languages";
import { useSubmissionPolling } from "../hooks/useSubmissionPolling";
import SubmissionResult from "./SubmissionResult";

const starterCode = `class Solution {
    public int[] twoSum(int[] nums, int target) {
        // Write your solution here

        return new int[]{};
    }
};`;

const sampleInput = `4
2 7 11 15
9`;
const [language, setLanguage] = useState("JAVA");

const [code, setCode] = useState(
  getLanguageConfig("JAVA").defaultCode
);

const selectedLanguage = getLanguageConfig(language);

function ProblemDescription({ problem }) {
  if (!problem) {
    return (
      <div className="p-6 text-sm text-arena-muted">
        Problem details are unavailable.
      </div>
    );
  }

  const difficultyStyles = {
    EASY: "bg-green-500/10 text-green-400",
    MEDIUM: "bg-yellow-500/10 text-yellow-400",
    HARD: "bg-red-500/10 text-red-400",
  };

  return (
    <div className="space-y-6 overflow-y-auto p-6">
      <div>
        <div className="mb-3 flex flex-wrap items-center gap-3">
          <span className="text-sm text-arena-muted">
            Problem #{problem.id}
          </span>

          <span
            className={`rounded-full px-3 py-1 text-xs font-semibold ${
              difficultyStyles[problem.difficulty] ||
              "bg-slate-500/10 text-slate-400"
            }`}
          >
            {problem.difficulty || "UNKNOWN"}
          </span>
        </div>

        <h1 className="text-2xl font-bold text-white">
          {problem.title}
        </h1>
      </div>

      <section>
        <h2 className="mb-3 text-lg font-semibold">Description</h2>
        <p className="whitespace-pre-wrap leading-7 text-slate-300">
          {problem.description || "No description available."}
        </p>
      </section>

      {problem.inputFormat && (
        <section>
          <h2 className="mb-2 text-lg font-semibold">Input Format</h2>
          <pre className="whitespace-pre-wrap rounded-lg border border-arena-border bg-arena-panel p-4 text-sm text-slate-300">
            {problem.inputFormat}
          </pre>
        </section>
      )}

      {problem.outputFormat && (
        <section>
          <h2 className="mb-2 text-lg font-semibold">Output Format</h2>
          <pre className="whitespace-pre-wrap rounded-lg border border-arena-border bg-arena-panel p-4 text-sm text-slate-300">
            {problem.outputFormat}
          </pre>
        </section>
      )}

      {problem.constraints && (
        <section>
          <h2 className="mb-2 text-lg font-semibold">Constraints</h2>
          <pre className="whitespace-pre-wrap rounded-lg border border-arena-border bg-arena-panel p-4 text-sm text-slate-300">
            {problem.constraints}
          </pre>
        </section>
      )}
    </div>
  );
}

export default function CodeWorkspace({ problemId, problem }) {
  const [code, setCode] = useState(starterCode);
  const [language, setLanguage] = useState("JAVA");
  const [activeTab, setActiveTab] = useState("description");
  const [bottomTab, setBottomTab] = useState("testcases");
  const [customInput, setCustomInput] = useState(sampleInput);
  const [output, setOutput] = useState("");
  const [status, setStatus] = useState("idle");

  const [submissionId, setSubmissionId] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState("");

  const {
    submission,
    loading: polling,
    error: pollingError,
  } = useSubmissionPolling(submissionId);

  function handleReset() {
    setCode(starterCode);
    setOutput("");
    setStatus("idle");
    setSubmitError("");
    setSubmissionId(null);
    setBottomTab("testcases");
  }

  // Run API abhi backend mein available nahi hai.
  function handleRun() {
    setStatus("demo");
    setOutput(
      "Demo mode: code execute karne ke liye backend Run API connect karni hogi."
    );
    setBottomTab("output");
  }
  async function handleSubmit() {
    if (submitting) return;

    if (!problemId) {
      setSubmitError(
        "Problem ID missing hai. App.jsx se actual problemId pass karo."
      );
      setBottomTab("output");
      return;
    }

    if (!code.trim()) {
      setSubmitError("Source code empty nahi ho sakta.");
      setBottomTab("output");
      return;
    }

    setSubmitting(true);
    setSubmitError("");
    setSubmissionId(null);
    setStatus("submitting");
    setBottomTab("output");

    try {
     const result = await submissionService.submit({
  problemId: Number(problemId),
  language,
  sourceCode: code,
});

      if (result?.id == null) {
        throw new Error(
          "Submission create hui, lekin response mein submission ID nahi mili."
        );
      }

      setSubmissionId(result.id);
      setStatus("submitted");
    } catch (error) {
      setSubmitError(
        error.response?.data?.message ||
          error.message ||
          "Submission create nahi ho saka."
      );
      setStatus("error");
    } finally {
      setSubmitting(false);
    }
  }

  const resultError = submitError || pollingError;

  return (
    <div className="flex h-[calc(100vh-65px)] min-h-[600px] flex-col bg-arena-bg">
      {/* Header */}
      <header className="flex h-14 shrink-0 items-center justify-between border-b border-arena-border px-4">
        <div className="flex items-center gap-3">
          <FileCode2 size={18} className="text-green-400" />

          <span className="text-sm font-medium text-white">
            Two Sum
          </span>

          <span className="rounded bg-green-500/10 px-2 py-1 text-xs text-green-400">
            Easy
          </span>
        </div>

        <button
          type="button"
          onClick={handleReset}
          className="flex items-center gap-2 rounded-lg border border-arena-border px-3 py-2 text-xs text-gray-300 hover:bg-white/5"
        >
          <RotateCcw size={14} />
          Reset
        </button>
      </header>

      <div className="flex min-h-0 flex-1 flex-col gap-2 p-2 lg:flex-row">
        {/* Problem description */}
        <section className="flex min-h-0 flex-1 flex-col overflow-hidden rounded-xl border border-arena-border bg-arena-panel lg:w-1/2">
          <div className="flex border-b border-arena-border">
            <button
              type="button"
              onClick={() => setActiveTab("description")}
              className={`px-4 py-3 text-sm ${
                activeTab === "description"
                  ? "border-b-2 border-green-400 text-white"
                  : "text-gray-400"
              }`}
            >
              Description
            </button>

            <button
              type="button"
              onClick={() => setActiveTab("examples")}
              className={`px-4 py-3 text-sm ${
                activeTab === "examples"
                  ? "border-b-2 border-green-400 text-white"
                  : "text-gray-400"
              }`}
            >
              Examples
            </button>
          </div>

          <div className="flex-1 overflow-y-auto p-5">
            {activeTab === "description" ? (
              <ProblemDescription problem={problem} />
            ) : (
              <div className="space-y-5 text-sm">
                <div>
                  <h3 className="mb-2 font-semibold text-white">
                    Example 1
                  </h3>

                  <pre className="overflow-x-auto rounded-lg bg-arena-bg p-3 text-gray-300">
{`Input: nums = [2,7,11,15], target = 9
Output: [0,1]`}
                  </pre>
                </div>

                <div>
                  <h3 className="mb-2 font-semibold text-white">
                    Example 2
                  </h3>

                  <pre className="overflow-x-auto rounded-lg bg-arena-bg p-3 text-gray-300">
{`Input: nums = [3,2,4], target = 6
Output: [1,2]`}
                  </pre>
                </div>
              </div>
            )}
          </div>
        </section>

        {/* Editor and output */}
        <section className="flex min-h-[350px] flex-1 flex-col overflow-hidden rounded-xl border border-arena-border bg-arena-panel lg:w-1/2">
          <div className="flex h-12 shrink-0 items-center justify-between border-b border-arena-border px-4">
            <div className="flex items-center gap-2 text-sm text-white">
              <FileCode2 size={16} className="text-blue-400" />
              <span>Solution</span>
            </div>
            <label className="flex items-center gap-2 rounded-md border border-arena-border px-2 py-1.5">
              <select
  value={language}
  onChange={(event) => {
    const nextLanguage = event.target.value;

    setLanguage(nextLanguage);
    setCode(getLanguageConfig(nextLanguage).defaultCode);
  }}
  className="rounded-lg border border-arena-border bg-arena-panel px-3 py-2 text-sm text-white outline-none"
>
  {LANGUAGES.map((item) => (
    <option key={item.value} value={item.value}>
      {item.label}
    </option>
  ))}
</select>

              <ChevronDown size={13} className="text-gray-400" />
            </label>
          </div>

          <div className="min-h-[250px] flex-1">
            <Editor
  height="100%"
  language={selectedLanguage.editorLanguage}
  theme="vs-dark"
  value={code}
  onChange={(value) => setCode(value ?? "")}
  options={{
    minimap: { enabled: false },
    fontSize: 14,
    automaticLayout: true,
    scrollBeyondLastLine: false,
    padding: { top: 16 },
  }}
/>
          </div>

          {/* Bottom tabs */}
          <div className="flex h-12 shrink-0 items-center gap-2 border-y border-arena-border px-3">
            <Terminal size={16} className="text-gray-400" />

            <button
              type="button"
              onClick={() => setBottomTab("testcases")}
              className={`px-2 py-3 text-xs ${
                bottomTab === "testcases"
                  ? "text-white"
                  : "text-gray-400"
              }`}
            >
              Test cases
            </button>

            <button
              type="button"
              onClick={() => setBottomTab("output")}
              className={`px-2 py-3 text-xs ${
                bottomTab === "output"
                  ? "text-white"
                  : "text-gray-400"
              }`}
            >
              Output
            </button>
          </div>

          {/* Bottom panel */}
          <div className="min-h-[130px] flex-1 overflow-y-auto p-4">
            {bottomTab === "testcases" ? (
              <div>
                <label className="mb-2 block text-xs text-gray-400">
                  Custom input
                </label>

                <textarea
                  value={customInput}
                  onChange={(event) =>
                    setCustomInput(event.target.value)
                  }
                  className="min-h-24 w-full resize-y rounded-lg border border-arena-border bg-arena-bg p-3 font-mono text-xs text-gray-200 outline-none focus:border-green-500"
                  spellCheck={false}
                />
              </div>
            ) : (
              <div className="space-y-3 text-sm">
                {submissionId || submitting || resultError ? (
                  <SubmissionResult
                    submission={submission}
                    loading={submitting || polling}
                    error={resultError}
                  />
                ) : status === "idle" ? (
                  <p className="text-gray-500">
                    Run or submit your code to see the result here.
                  </p>
                ) : (
                  <div className="flex items-start gap-2 text-yellow-300">
                    <CheckCircle2 size={17} />

                    <pre className="whitespace-pre-wrap font-sans text-xs">
                      {output}
                    </pre>
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Actions */}
          <div className="flex shrink-0 items-center justify-between border-t border-arena-border p-3">
            <span className="text-xs text-gray-500">
              {submitting
                ? "Submitting..."
                : polling
                  ? "Judge processing..."
                  : status === "error"
                    ? "Submission failed"
                    : status === "submitted"
                      ? "Submission created"
                      : status === "demo"
                        ? "Demo mode"
                        : "Ready"}
            </span>

            <div className="flex gap-2">
              <button
                type="button"
                onClick={handleRun}
                className="flex items-center gap-2 rounded-lg border border-arena-border px-3 py-2 text-sm text-gray-200 hover:bg-white/5"
              >
                <Play size={15} />
                Run
              </button>

              <button
                type="button"
                onClick={handleSubmit}
                disabled={submitting}
                className="flex items-center gap-2 rounded-lg bg-green-500 px-3 py-2 text-sm font-semibold text-gray-950 hover:bg-green-400 disabled:cursor-not-allowed disabled:opacity-50"
              >
                <Send size={15} />
                {submitting ? "Submitting..." : "Submit"}
              </button>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
