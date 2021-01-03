package kielce.tu.weaii.telelearn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.ArticleNotFound;
import kielce.tu.weaii.telelearn.exceptions.NotFoundException;
import kielce.tu.weaii.telelearn.models.GlobalNews;
import kielce.tu.weaii.telelearn.requests.GlobalNewsRequest;
import kielce.tu.weaii.telelearn.services.ports.GlobalNewsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "admin1", roles = "ADMIN")
@Tag(INTEGRATION_TEST)
class GlobalNewsControllerTest {

    @MockBean
    private GlobalNewsService globalNewsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void should_ask_for_article_with_id_and_return_200() throws Exception {
        //given
        final Long id = 1L;
        when(globalNewsService.getById(id)).thenReturn(TestData.getGlobalNews(TestData.getAdmin()));
        //when & then
        mockMvc.perform(get("/api/news/get/" + id))
                .andExpect(status().isOk());
        verify(globalNewsService).getById(id);
    }

    @Test
    void should_ask_for_article_with_id_and_return_404_when_it_doesnt_exist() throws Exception {
        //given
        final Long id = 1L;
        when(globalNewsService.getById(id)).thenThrow(new ArticleNotFound(id));
        //when & then
        mockMvc.perform(get("/api/news/get/" + id))
                .andExpect(status().isNotFound());
        verify(globalNewsService).getById(id);
    }

    @Test
    void should_ask_for_page_and_return_200() throws Exception {
        //given
        final Integer pageSize = 10;
        final Integer pageNo = 1;
        when(globalNewsService.getPage(pageSize, pageNo))
                .thenReturn(new PageImpl<>(Arrays.asList((TestData.getGlobalNews(TestData.getAdmin())))));
        //when & then
        mockMvc.perform(get("/api/news/get")
                .param("pageNo", pageNo.toString())
                .param("pageSize", pageSize.toString()))
                .andExpect(status().isOk());
        verify(globalNewsService).getPage(pageSize, pageNo);
    }

    @Test
    void should_ask_for_delete_and_return_204() throws Exception {
        //given
        final long id = 1L;
        //when & then
        mockMvc.perform(delete("/api/news/" + id))
                .andExpect(status().isNoContent());
        verify(globalNewsService).delete(id);
    }

    @Test
    void should_ask_for_delete_and_return_404_when_article_doesnt_exist() throws Exception {
        //given
        final long id = 1L;
        doThrow(new ArticleNotFound(id)).when(globalNewsService).delete(id);
        //when & then
        mockMvc.perform(delete("/api/news/" + id))
                .andExpect(status().isNotFound());
        verify(globalNewsService).delete(id);
    }

    @Test
    void should_ask_to_edit_end_return_204() throws Exception {
        //given
        final Long id = 5L;
        GlobalNewsRequest request = TestData.getGlobalNewsRequest();
        GlobalNews out = TestData.getGlobalNews(TestData.getAdmin());
        when(globalNewsService.edit(any(), any())).thenReturn(out);
        //when & then
        mockMvc.perform(put("/api/news/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        verify(globalNewsService).edit(eq(id), eq(request));
    }

    @Test
    void should_ask_to_edit_end_return_404_when_article_doesnt_exist() throws Exception {
        //given
        final Long id = 5L;
        GlobalNewsRequest request = TestData.getGlobalNewsRequest();
        GlobalNews out = TestData.getGlobalNews(TestData.getAdmin());
        when(globalNewsService.edit(any(), any())).thenThrow(new NotFoundException("article"));
        //when & then
        mockMvc.perform(put("/api/news/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        verify(globalNewsService).edit(eq(id), eq(request));
    }

    @Test
    void should_ask_to_add_article_and_return_201() throws Exception {
        //given
        final Long outId = 5L;
        GlobalNewsRequest request = TestData.getGlobalNewsRequest();
        GlobalNews out = TestData.getGlobalNews(TestData.getAdmin());
        out.setId(outId);
        when(globalNewsService.add(any())).thenReturn(out);
        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/news/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        //then
        verify(globalNewsService).add(eq(request));
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);
        Assertions.assertThat(mvcResult.getResponse().getHeader("Location")).contains(outId.toString());
    }
}