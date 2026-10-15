
package com.codearena.codearena.controller;

import com.codearena.codearena.service.ProblemTestCaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProblemTestCaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

   @MockitoBean
private ProblemTestCaseService problemTestCaseService;
   
@Test
void regularUserCannotAccessHiddenTestCases() throws Exception {

    mockMvc.perform(
            get("/api/problems/{problemId}/test-cases", 1L)
                    .with(user("normalUser").roles("USER"))
                    .accept(MediaType.APPLICATION_JSON)
    )
    .andExpect(status().isForbidden());

    verifyNoInteractions(problemTestCaseService);
}

}
