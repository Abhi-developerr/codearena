
import { Navigate, Route, Routes } from "react-router-dom";
import ProblemsPage from "./pages/ProblemsPage";
import ProblemWorkspacePage from "./pages/ProblemWorkspacePage";
import MySubmissionsPage from "./pages/MySubmissionsPage";
import SubmissionDetailsPage from "./pages/SubmissionDetailsPage";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/problems" replace />} />

      <Route path="/problems" element={<ProblemsPage />} />

      <Route
        path="/problems/:problemId"
        element={<ProblemWorkspacePage />}
      />

      <Route
  path="/submissions/:submissionId"
  element={<SubmissionDetailsPage />}
/>

      <Route
        path="/my-submissions"
        element={<MySubmissionsPage />}
      />
    </Routes>
  );
}
