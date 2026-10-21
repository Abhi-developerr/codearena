
import {
  Code2,
  Search,
  Terminal,
  Trophy,
  BookOpen,
  ChevronRight,
  CircleDot,
} from "lucide-react";

const problems = [
  {
    id: 1,
    title: "Two Sum",
    difficulty: "Easy",
    topic: "Array, HashMap",
    acceptance: "56.4%",
  },
  {
    id: 2,
    title: "Binary Search",
    difficulty: "Easy",
    topic: "Binary Search",
    acceptance: "59.1%",
  },
  {
    id: 3,
    title: "Longest Substring Without Repeating Characters",
    difficulty: "Medium",
    topic: "String, Sliding Window",
    acceptance: "35.8%",
  },
  {
    id: 4,
    title: "Median of Two Sorted Arrays",
    difficulty: "Hard",
    topic: "Array, Binary Search",
    acceptance: "42.1%",
  },
];

const difficultyStyles = {
  Easy: "text-green-400 bg-green-400/10",
  Medium: "text-yellow-400 bg-yellow-400/10",
  Hard: "text-red-400 bg-red-400/10",
};

function App() {
  return (
    <div className="min-h-screen bg-arena-bg text-gray-200">
      <header className="sticky top-0 z-10 border-b border-arena-border bg-arena-bg/95 backdrop-blur">
        <div className="mx-auto flex h-16 max-w-[1440px] items-center justify-between px-5">
          <a href="/" className="flex items-center gap-2">
            <div className="rounded-lg bg-green-500 p-2 text-gray-950">
              <Code2 size={22} />
            </div>
            <span className="text-xl font-bold tracking-tight">
              Code<span className="text-green-400">Arena</span>
            </span>
          </a>

          <nav className="hidden items-center gap-7 text-sm text-gray-400 md:flex">
            <a href="/" className="text-green-400">
              Problems
            </a>
            <a href="#study" className="hover:text-white">
              Study
            </a>
            <a href="#contests" className="hover:text-white">
              Contests
            </a>
          </nav>

          <button className="rounded-lg border border-arena-border px-4 py-2 text-sm hover:bg-white/5">
            Sign in
          </button>
        </div>
      </header>

      <main className="mx-auto max-w-[1440px] px-5 py-10">
        <section className="mb-9">
          <div className="mb-3 inline-flex items-center gap-2 rounded-full border border-green-500/20 bg-green-500/10 px-3 py-1.5 text-xs text-green-400">
            <CircleDot size={13} />
            Practice. Solve. Improve.
          </div>

          <h1 className="text-3xl font-bold tracking-tight sm:text-4xl">
            Sharpen your coding skills.
          </h1>

          <p className="mt-3 max-w-2xl text-sm leading-6 text-gray-400 sm:text-base">
            Solve programming challenges, write Java solutions,
            and track your progress in one place.
          </p>
        </section>

        <section className="mb-8 grid gap-4 sm:grid-cols-3">
          <StatCard
            icon={<BookOpen size={20} />}
            label="Available problems"
            value="Explore"
          />
          <StatCard
            icon={<Terminal size={20} />}
            label="Execution environment"
            value="Java Sandbox"
          />
          <StatCard
            icon={<Trophy size={20} />}
            label="Your progress"
            value="Get started"
          />
        </section>

        <section className="overflow-hidden rounded-xl border border-arena-border bg-arena-panel">
          <div className="flex flex-col gap-4 border-b border-arena-border p-5 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="text-lg font-semibold">Problem Set</h2>
              <p className="mt-1 text-sm text-gray-400">
                Find a challenge and start coding.
              </p>
            </div>

            <label className="flex items-center gap-2 rounded-lg border border-arena-border bg-arena-bg px-3 py-2">
              <Search size={17} className="text-gray-500" />
              <input
                placeholder="Search problems..."
                className="w-full bg-transparent text-sm outline-none placeholder:text-gray-500 sm:w-52"
                aria-label="Search problems"
              />
            </label>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full min-w-[650px] text-left text-sm">
              <thead className="border-b border-arena-border bg-white/[0.02] text-xs uppercase tracking-wider text-gray-500">
                <tr>
                  <th className="px-5 py-4 font-medium">Problem</th>
                  <th className="px-5 py-4 font-medium">Difficulty</th>
                  <th className="px-5 py-4 font-medium">Topics</th>
                  <th className="px-5 py-4 font-medium">Acceptance</th>
                  <th className="px-5 py-4"></th>
                </tr>
              </thead>

              <tbody className="divide-y divide-arena-border">
                {problems.map((problem) => (
                  <tr
                    key={problem.id}
                    className="transition hover:bg-white/[0.025]"
                  >
                    <td className="px-5 py-4 font-medium text-gray-200">
                      {problem.id}. {problem.title}
                    </td>
                    <td className="px-5 py-4">
                      <span
                        className={`rounded-md px-2 py-1 text-xs ${
                          difficultyStyles[problem.difficulty]
                        }`}
                      >
                        {problem.difficulty}
                      </span>
                    </td>
                    <td className="px-5 py-4 text-gray-400">
                      {problem.topic}
                    </td>
                    <td className="px-5 py-4 text-gray-400">
                      {problem.acceptance}
                    </td>
                    <td className="px-5 py-4">
                      <button
                        aria-label={`Open ${problem.title}`}
                        className="text-gray-500 hover:text-green-400"
                      >
                        <ChevronRight size={18} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="border-t border-arena-border px-5 py-4 text-xs text-gray-500">
            Demo problems · Backend integration coming next
          </div>
        </section>
      </main>
    </div>
  );
}

function StatCard({ icon, label, value }) {
  return (
    <div className="rounded-xl border border-arena-border bg-arena-panel p-5">
      <div className="mb-4 flex items-center gap-3 text-green-400">
        {icon}
        <span className="text-sm text-gray-400">{label}</span>
      </div>
      <p className="text-lg font-semibold">{value}</p>
    </div>
  );
}

export default App;
