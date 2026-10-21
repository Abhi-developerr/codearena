
import api from "./api";

export const problemService = {
 
  async getProblems({
    page = 0,
    size = 10,
    difficulty,
    search,
    sort = "createdAt,desc",
  } = {}) {
    const params = { page, size, sort };

    if (difficulty) params.difficulty = difficulty;
    if (search?.trim()) params.search = search.trim();

    const response = await api.get("/problems", { params });
    return response.data;
  },

  // GET /api/problems/{id}
  async getProblemById(id) {
    const response = await api.get(
      `/problems/${encodeURIComponent(id)}`
    );

    return response.data;
  },
};
