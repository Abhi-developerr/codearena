
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Search, Code2 } from "lucide-react";
import { problemService } from "../services/problemService";

export default function ProblemsPage() {
  const navigate = useNavigate();

  const [problems, setProblems] = useState([]);
  const [search, setSearch] = useState("");
  const [difficulty, setDifficulty] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function loadProblems() {
      setLoading(true);
      setError("");

      try {
        const data = await problemService.getProblems({
          page,
          size: 10,
          search,
          difficulty: difficulty || undefined,
        });

        if (cancelled) return;

        setProblems(data.content ?? []);
        setTotalPages(data.totalPages ?? 0);
      } catch (err) {
        if (cancelled) return;

        setError(
          err.response?.data?.message ||
            "Problems load nahi ho paaye. Backend aur login status check karo."
        );
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    loadProblems();

    return () => {
      cancelled = true;
    };
  }, [page, search, difficulty]);

  function getDifficultyClass(value) {
    switch (String(value ?? "").toUpperCase()) {
      case "EASY":
        return "text-green-400";
      case "MEDIUM":
        return "text-yellow-400";
      case "HARD":
        return "text-red-400";
      default:
        return "text-gray-400";
    }
  }

  return (
    <main className="min-h-screen bg-arena-bg p-6 text-white">
      <div className="mx-auto max-w-6xl">
        <div className="mb-6">
          <h1 className="text-3xl font-bold">Problems</h1>
          <p className="mt-2 text-sm text-gray-400">
            Practice coding problems on CodeArena.
          </p>
        </div>

        <div className="mb-5 flex flex-col gap-3 sm:flex-row">
          <div className="flex flex-1 items-center gap-2 rounded-lg border border-arena-border bg-arena-panel px-3">
            <Search size={18} className="text-gray-400" />

            <input
              value={search}
              onChange={(event) => {
                setSearch(event.target.value);
                setPage(0);
              }}
              placeholder="Search problems..."
              maxLength={100}
              className="w-full bg-transparent py-3 text-sm outline-none"
            />
          </div>

          <select
            value={difficulty}
            onChange={(event) => {
              setDifficulty(event.target.value);
              setPage(0);
            }}
            className="rounded-lg border border-arena-border bg-arena-panel px-4 py-3 text-sm"
          >
            <option value="">All difficulties</option>
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
          </select>
        </div>

        <div className="overflow-hidden rounded-xl border border-arena-border bg-arena-panel">
          {loading ? (
            <p className="p-6 text-gray-400">Loading problems...</p>
          ) : error ? (
            <div className="p-6">
              <p className="text-red-400">{error}</p>
              <button
                onClick={() => setPage((current) => current)}
                className="mt-3 text-sm text-green-400"
              >
                Retry
              </button>
            </div>
          ) : problems.length === 0 ? (
            <p className="p-6 text-gray-400">
              No problems found.
            </p>
          ) : (
            <div className="divide-y divide-arena-border">
              {problems.map((problem) => (
                <button
                  key={problem.id}
                  onClick={() => navigate(`/problems/${problem.id}`)}
                  className="flex w-full items-center justify-between gap-4 p-4 text-left hover:bg-white/[0.03]"
                >
                  <div className="flex min-w-0 items-center gap-3">
                    <Code2
                      size={18}
                      className="shrink-0 text-gray-400"
                    />

                    <div className="min-w-0">
                      <p className="truncate font-medium">
                        {problem.title ?? `Problem #${problem.id}`}
                      </p>

                      <p className="mt-1 text-xs text-gray-500">
                        Problem ID: {problem.id}
                      </p>
                    </div>
                  </div>

                  <span
                    className={`shrink-0 text-sm ${getDifficultyClass(
                      problem.difficulty
                    )}`}
                  >
                    {problem.difficulty ?? "Unknown"}
                  </span>
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="mt-4 flex items-center justify-between">
          <span className="text-sm text-gray-400">
            Page {page + 1} of {Math.max(totalPages, 1)}
          </span>

          <div className="flex gap-2">
            <button
              disabled={page === 0 || loading}
              onClick={() => setPage((current) => current - 1)}
              className="rounded-md border border-arena-border px-3 py-2 text-sm disabled:opacity-40"
            >
              Previous
            </button>

            <button
              disabled={
                loading ||
                totalPages === 0 ||
                page >= totalPages - 1
              }
              onClick={() => setPage((current) => current + 1)}
              className="rounded-md border border-arena-border px-3 py-2 text-sm disabled:opacity-40"
            >
              Next
            </button>
          </div>
        </div>
      </div>
    </main>
  );
}
