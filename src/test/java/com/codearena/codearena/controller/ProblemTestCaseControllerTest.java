package com.codearena.codearena.controller;

import com.codearena.codearena.service.ProblemTestCaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import com.codearena.codearena.dto.ProblemTestCasePublicResponse;
import java.util.List;
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


@Test
void adminCanAccessHiddenTestCases() throws Exception {

    mockMvc.perform(
            get("/api/problems/{problemId}/test-cases", 1L)
                    .with(user("admin").roles("ADMIN"))
                    .accept(MediaType.APPLICATION_JSON)
    )
    .andExpect(status().isOk());

    org.mockito.Mockito.verify(problemTestCaseService)
            .getTestCases(1L);
}

@Test
void unauthenticatedUserCannotAccessHiddenTestCases()
        throws Exception {

    mockMvc.perform(
            get("/api/problems/{problemId}/test-cases", 1L)
                    .accept(MediaType.APPLICATION_JSON)
    )
    .andExpect(status().isUnauthorized());

    verifyNoInteractions(problemTestCaseService);
}


@Test
void regularUserCanAccessSampleTestCases() throws Exception {

    when(problemTestCaseService.getSampleTestCases(1L))
            .thenReturn(List.of());

    mockMvc.perform(
            get("/api/problems/{problemId}/test-cases/samples", 1L)
                    .with(user("normalUser").roles("USER"))
                    .accept(MediaType.APPLICATION_JSON)
    )
    .andExpect(status().isOk());

    verify(problemTestCaseService)
            .getSampleTestCases(1L);
}



}
