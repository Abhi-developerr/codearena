
import api from "./api";

export const submissionService = {
  // POST /api/submissions
  async submit({ problemId, language, sourceCode }) {
    const response = await api.post("/submissions", {
      problemId: Number(problemId),
      language,
      sourceCode,
    });

    return response.data;
  },

  // GET /api/submissions/{submissionId}
  async getSubmission(submissionId) {
    const response = await api.get(
      `/submissions/${encodeURIComponent(submissionId)}`
    );

    return response.data;
  },

  // GET /api/submissions?page=0&size=10
  async getMySubmissions({ page = 0, size = 10 } = {}) {
    const response = await api.get("/submissions", {
      params: { page, size },
    });

    return response.data;
  },

  async getProblemSubmissions(
    problemId,
    { page = 0, size = 10 } = {}
  ) {
    const response = await api.get(
      `/submissions/problems/${encodeURIComponent(problemId)}/submissions`,
      { params: { page, size } }
    );

    return response.data;
  },
};
