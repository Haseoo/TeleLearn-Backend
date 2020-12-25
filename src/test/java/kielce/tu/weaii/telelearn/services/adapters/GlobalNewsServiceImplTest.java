package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.Constants;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.NotFoundException;
import kielce.tu.weaii.telelearn.models.GlobalNews;
import kielce.tu.weaii.telelearn.repositories.ports.GlobalNewsRepository;
import kielce.tu.weaii.telelearn.requests.GlobalNewsRequest;
import kielce.tu.weaii.telelearn.services.ports.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;


@Tag(Constants.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
class GlobalNewsServiceImplTest {

    @Mock
    private GlobalNewsRepository globalNewsRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private GlobalNewsServiceImpl sut;

    @Test
    void should_ask_and_return_news_by_id() {
        //given
        final Long id = 1L;
        GlobalNews globalNews = TestData.getGlobalNews(TestData.getAdmin());
        when(globalNewsRepository.getById(id)).thenReturn(Optional.of(globalNews));
        //when
        GlobalNews out = sut.getById(id);
        //then
        verify(globalNewsRepository).getById(id);
        Assertions.assertThat(out).isEqualTo(globalNews);
    }

    @Test
    void should_throw_not_found_exception_when_news_doesnt_exist() {
        //given
        final Long id = 1L;
        when(globalNewsRepository.getById(id)).thenReturn(Optional.empty());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void should_add_new_global_news() {
        //given
        final GlobalNewsRequest request = TestData.getGlobalNewsRequest();
        ArgumentCaptor<GlobalNews> entityToSave = ArgumentCaptor.forClass(GlobalNews.class);
        when(userService.getById(request.getAuthorId())).thenReturn(TestData.getAdmin());
        //when
        sut.add(request);
        //then
        verify(globalNewsRepository).save(entityToSave.capture());
        verify(userService).getById(request.getAuthorId());
        Assertions.assertThat(entityToSave.getValue().getBrief()).isEqualTo(request.getBrief());
        Assertions.assertThat(entityToSave.getValue().getHtmlContent()).isEqualTo(request.getHtmlContent());
        Assertions.assertThat(entityToSave.getValue().getTitle()).isEqualTo(request.getTitle());
        Assertions.assertThat(entityToSave.getValue().getPublicationDate()).isEqualTo(request.getPublicationDate());
    }

    @Test
    void should_edit_global_news() {
        //given
        final GlobalNewsRequest request = TestData.getGlobalNewsRequest();
        final Long idToEdit = 1L;
        GlobalNews mock = TestData.getGlobalNews(TestData.getAdmin());
        mock.getAuthor().setId(2L);
        ArgumentCaptor<GlobalNews> entityToSave = ArgumentCaptor.forClass(GlobalNews.class);
        when(userService.getById(request.getAuthorId())).thenReturn(TestData.getAdmin());
        when(globalNewsRepository.getById(idToEdit)).thenReturn(Optional.of(mock));
        //when
        sut.edit(idToEdit, request);
        //then
        verify(globalNewsRepository).save(entityToSave.capture());
        verify(userService).getById(request.getAuthorId());
        Assertions.assertThat(entityToSave.getValue().getBrief()).isEqualTo(request.getBrief());
        Assertions.assertThat(entityToSave.getValue().getHtmlContent()).isEqualTo(request.getHtmlContent());
        Assertions.assertThat(entityToSave.getValue().getTitle()).isEqualTo(request.getTitle());
        Assertions.assertThat(entityToSave.getValue().getPublicationDate()).isEqualTo(request.getPublicationDate());
    }

    @Test
    void should_not_request_for_user_when_id_is_the_same() {
        //given
        final GlobalNewsRequest request = TestData.getGlobalNewsRequest();
        final Long idToEdit = 1L;
        GlobalNews mock = TestData.getGlobalNews(TestData.getAdmin());
        when(globalNewsRepository.getById(idToEdit)).thenReturn(Optional.of(mock));
        //when
        sut.edit(idToEdit, request);
        //then
        verify(userService, never()).getById(mock.getAuthor().getId());

    }

    @Test
    void should_ask_for_and_return_page() {
        //given
        final int pageNo = 1, pageSize = 1;
        Page<GlobalNews> mock = new PageImpl<>(Arrays.asList(TestData.getGlobalNews(TestData.getAdmin())));
        when(globalNewsRepository.getPage(pageSize, pageNo)).thenReturn(mock);
        //when
        Page<GlobalNews> out = sut.getPage(pageSize, pageNo);
        //then
        verify(globalNewsRepository).getPage(pageSize, pageNo);
        Assertions.assertThat(mock).isEqualTo(out);
    }

    @Test
    void should_ask_for_delete_news() {
        //given
        final Long id = 1L;
        GlobalNews globalNews = TestData.getGlobalNews(TestData.getAdmin());
        ArgumentCaptor<GlobalNews> requestedEntity = ArgumentCaptor.forClass(GlobalNews.class);
        when(globalNewsRepository.getById(id)).thenReturn(Optional.of(globalNews));
        //when
        sut.delete(id);
        //then
        verify(globalNewsRepository).delete(globalNews);
    }
}