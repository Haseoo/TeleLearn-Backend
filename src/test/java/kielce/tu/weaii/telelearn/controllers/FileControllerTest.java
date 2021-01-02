package kielce.tu.weaii.telelearn.controllers;

import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.models.Attachment;
import kielce.tu.weaii.telelearn.services.ports.AttachmentService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "admin1", roles = "ADMIN")
@Tag(INTEGRATION_TEST)
class FileControllerTest {

    @MockBean
    private AttachmentService attachmentService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_ask_for_file_by_id_and_return_403_when_user_doesnt_have_permission_to_it() throws Exception {
        //given
        final Long id = 1L;
        when(attachmentService.getById(id)).thenThrow(new AuthorizationException("attachment", null, id));
        //when & then
        mockMvc.perform(get("/api/file/" + id)).andExpect(status().isForbidden());
        verify(attachmentService).getById(id);
    }

    @Test
    void should_ask_for_file_by_id_and_return_200() throws Exception {
        //given
        final Long id = 1L;
        Attachment attachment = TestData.getAttachment();
        when(attachmentService.getById(id)).thenReturn(attachment);
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/file/" + id)).andReturn();
        //when
        verify(attachmentService).getById(id);
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(mvcResult.getResponse().getContentType()).isEqualTo(attachment.getFileType());
        Assertions.assertThat(mvcResult.getResponse().getContentAsByteArray())
                .isEqualTo(attachment.getAttachmentData().get(0).getData());

    }

}