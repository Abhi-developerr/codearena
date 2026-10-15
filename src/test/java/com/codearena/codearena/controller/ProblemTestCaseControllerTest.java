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

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import com.codearena.codearena.dto.ProblemTestCasePublicResponse;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
@Test
@WithMockUser(roles = "USER")
void regularUserCanAccessSampleTestCasesWithoutHiddenField() throws Exception {

    ProblemTestCasePublicResponse sampleCase =
            new ProblemTestCasePublicResponse(
                    1L,
                    "[2,7,11,15],9",
                    "[0,1]"
            );

    when(problemTestCaseService.getSampleTestCases(1L))
            .thenReturn(List.of(sampleCase));

    mockMvc.perform(
            get("/api/problems/1/test-cases/samples")
                    .with(user("testuser").roles("USER"))
    )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].input")
                    .value("[2,7,11,15],9"))
            .andExpect(jsonPath("$[0].expectedOutput")
                    .value("[0,1]"))
            .andExpect(jsonPath("$[0].hidden").doesNotExist());

    verify(problemTestCaseService).getSampleTestCases(1L);
}


}
