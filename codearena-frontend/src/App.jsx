
import { Code2 } from "lucide-react";
import CodeWorkspace from "./components/CodeWorkspace";

export default function App() {
  return (
    <div className="min-h-screen bg-arena-bg text-gray-200">
      <header className="flex h-16 items-center justify-between border-b border-arena-border px-5">
        <a href="/" className="flex items-center gap-2">
          <div className="rounded-lg bg-green-500 p-2 text-gray-950">
            <Code2 size={21} />
          </div>
          <span className="text-xl font-bold">
            Code<span className="text-green-400">Arena</span>
          </span>
        </a>

        <span className="text-xs text-gray-400">
          Coding workspace
        </span>
      </header>
      <CodeWorkspace problemId={YOUR_ACTUAL_TWO_SUM_ID} />
    </div>
  );
}
