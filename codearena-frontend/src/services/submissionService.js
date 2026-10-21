
import api from "./api";

export const submissionService = {

  async submit(payload) {
    const response = await api.post("/submissions", payload);
    return response.data;
  },

  async getSubmission(submissionId) {
    const response = await api.get(
      `/submissions/${encodeURIComponent(submissionId)}`
    );
    return response.data;
  },
};
