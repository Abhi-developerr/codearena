
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

const starterCode = `class Solution {
    public int[] twoSum(int[] nums, int target) {
        // Write your solution here

        return new int[]{};
    }
}`;

const sampleInput = `4
2 7 11 15
9`;

export default function CodeWorkspace() {
  const [code, setCode] = useState(starterCode);
  const [language, setLanguage] = useState("java");
  const [activeTab, setActiveTab] = useState("description");
  const [bottomTab, setBottomTab] = useState("testcases");
  const [customInput, setCustomInput] = useState(sampleInput);
  const [output, setOutput] = useState("");
  const [status, setStatus] = useState("idle");

  function handleReset() {
    setCode(starterCode);
    setOutput("");
    setStatus("idle");
  }

  // Backend integration will be added in a later step.
  function handleRun() {
    setStatus("demo");
    setOutput(
      "Demo mode: connect the Spring Boot execution API to run code."
    );
    setBottomTab("output");
  }

  function handleSubmit() {
    setStatus("demo");
    setOutput(
      "Demo mode: connect the submission API to submit your solution."
    );
    setBottomTab("output");
  }

  return (
    <div className="flex h-[calc(100vh-65px)] min-h-[600px] flex-col bg-arena-bg">
      <header className="flex h-14 shrink-0 items-center justify-between border-b border-arena-border px-4">
        <div className="flex items-center gap-3">
          <FileCode2 size={18} className="text-green-400" />
          <span className="text-sm font-medium">
            1. Two Sum
          </span>
          <span className="rounded bg-green-500/10 px-2 py-1 text-xs text-green-400">
            Easy
          </span>
        </div>

        <button
          onClick={handleReset}
          className="flex items-center gap-2 rounded-lg border border-arena-border px-3 py-2 text-xs text-gray-300 hover:bg-white/5"
        >
          <RotateCcw size={14} />
          Reset
        </button>
      </header>

      <div className="flex min-h-0 flex-1 flex-col gap-2 p-2 lg:flex-row">
        {/* Problem panel */}
        <section className="flex min-h-0 flex-1 flex-col overflow-hidden rounded-xl border border-arena-border bg-arena-panel lg:w-1/2">
          <div className="flex border-b border-arena-border">
            <button
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
              <>
                <h1 className="mb-4 text-2xl font-bold text-white">
                  Two Sum
                </h1>

                <p className="mb-5 text-sm leading-7 text-gray-300">
                  Given an array of integers nums and an
                  integer target, return the indices of the
                  two numbers such that they add up to target.
                </p>

                <p className="mb-5 text-sm leading-7 text-gray-300">
                  You may assume that each input has exactly
                  one solution, and you may not use the same
                  element twice.
                </p>

                <h2 className="mb-3 font-semibold text-white">
                  Constraints
                </h2>

                <ul className="list-inside list-disc space-y-2 text-sm text-gray-400">
                  <li>2 ≤ nums.length ≤ 10⁴</li>
                  <li>-10⁹ ≤ nums[i] ≤ 10⁹</li>
                  <li>Exactly one valid answer exists.</li>
                </ul>
              </>
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
            <div className="flex items-center gap-2 text-sm">
              <FileCode2 size={16} className="text-blue-400" />
              <span>Solution</span>
            </div>

            <label className="flex items-center gap-2 rounded-md border border-arena-border px-2 py-1.5">
              <select
                value={language}
                onChange={(event) => setLanguage(event.target.value)}
                className="bg-transparent text-xs text-gray-200 outline-none"
              >
                <option value="java" className="bg-gray-900">
                  Java
                </option>
              </select>
              <ChevronDown size={13} className="text-gray-400" />
            </label>
          </div>

          <div className="min-h-[250px] flex-1">
            <Editor
              height="100%"
              language={language}
              theme="vs-dark"
              value={code}
              onChange={(value) => setCode(value ?? "")}
              options={{
                minimap: { enabled: false },
                fontSize: 14,
                fontFamily: "JetBrains Mono, monospace",
                lineNumbers: "on",
                scrollBeyondLastLine: false,
                automaticLayout: true,
                tabSize: 4,
                padding: { top: 16 },
                wordWrap: "on",
              }}
            />
          </div>

          <div className="flex h-12 shrink-0 items-center gap-2 border-y border-arena-border px-3">
            <Terminal size={16} className="text-gray-400" />
            <button
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
              <div className="text-sm">
                {status === "idle" ? (
                  <p className="text-gray-500">
                    Run your code to see output here.
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

          <div className="flex shrink-0 items-center justify-between border-t border-arena-border p-3">
            <span className="text-xs text-gray-500">
              {status === "idle" ? "Ready" : "Demo mode"}
            </span>

            <div className="flex gap-2">
              <button
                onClick={handleRun}
                className="flex items-center gap-2 rounded-lg border border-arena-border px-3 py-2 text-sm hover:bg-white/5"
              >
                <Play size={15} />
                Run
              </button>

              <button
                onClick={handleSubmit}
                className="flex items-center gap-2 rounded-lg bg-green-500 px-3 py-2 text-sm font-semibold text-gray-950 hover:bg-green-400"
              >
                <Send size={15} />
                Submit
              </button>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
