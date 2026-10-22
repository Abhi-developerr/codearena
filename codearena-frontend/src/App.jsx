
import { Navigate, Route, Routes } from "react-router-dom";
import ProblemsPage from "./pages/ProblemsPage";
import ProblemWorkspacePage from "./pages/ProblemWorkspacePage";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/problems" replace />} />

      <Route path="/problems" element={<ProblemsPage />} />

      <Route
        path="/problems/:problemId"
        element={<ProblemWorkspacePage />}
      />
    </Routes>
  );
}
