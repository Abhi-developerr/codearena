
import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, LoaderCircle, RefreshCw } from "lucide-react";
import { problemService } from "../services/problemService";
import CodeWorkspace from "../components/CodeWorkspace";

export default function ProblemWorkspacePage() {
  const { problemId } = useParams();
  const navigate = useNavigate();

  const [problem, setProblem] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function fetchProblem() {
    try {
      setLoading(true);
      setError("");

      const data = await problemService.getProblemById(problemId);
      setProblem(data);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Problem load nahi hui. Please try again."
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchProblem();
  }, [problemId]);

  if (loading) {
    return (
      <div className="min-h-screen bg-arena-bg text-white flex items-center justify-center">
        <LoaderCircle className="mr-3 animate-spin" size={22} />
        Loading problem...
      </div>
    );
  }

  if (error || !problem) {
    return (
      <div className="min-h-screen bg-arena-bg text-white p-8">
        <button
          onClick={() => navigate("/problems")}
          className="mb-6 flex items-center gap-2 text-arena-muted hover:text-white"
        >
          <ArrowLeft size={18} />
          Back to Problems
        </button>

        <div className="mx-auto max-w-lg rounded-xl border border-arena-border bg-arena-panel p-6">
          <h2 className="text-xl font-semibold">Unable to load problem</h2>

          <p className="mt-2 text-sm text-red-400">
            {error || "Problem nahi mili."}
          </p>

          <button
            onClick={fetchProblem}
            className="mt-5 flex items-center gap-2 rounded-lg bg-green-500 px-4 py-2 font-medium text-black hover:bg-green-400"
          >
            <RefreshCw size={16} />
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-arena-bg text-white">
      <header className="flex h-14 items-center justify-between border-b border-arena-border px-5">
        <button
          onClick={() => navigate("/problems")}
          className="flex items-center gap-2 text-sm text-arena-muted hover:text-white"
        >
          <ArrowLeft size={18} />
          All Problems
        </button>

        <span className="text-sm font-semibold">
          CodeArena
        </span>

        <span className="text-xs text-arena-muted">
          Problem #{problem.id}
        </span>
      </header>

      <CodeWorkspace problemId={problem.id} />
    </div>
  );
}
