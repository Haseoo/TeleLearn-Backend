package kielce.tu.weaii.telelearn.repositories;

import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.models.GlobalNews;
import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.repositories.jpa.GlobalNewsJPARepository;
import kielce.tu.weaii.telelearn.repositories.jpa.UserJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.GlobalNewsRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import javax.transaction.Transactional;
import java.util.Optional;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;


@SpringBootTest
@Tag(INTEGRATION_TEST)
class GlobalNewsRepositoryTest {

    @Autowired
    private GlobalNewsRepository sut;

    @Autowired
    private GlobalNewsJPARepository newsJpaRepository;
    @Autowired
    private UserJPARepository userJPARepository;

    @BeforeEach
    void setUp() {
        newsJpaRepository.deleteAll();
        userJPARepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        newsJpaRepository.deleteAll();
        userJPARepository.deleteAll();
    }

    @Test
    void should_return_two_pages() {
        //given
        User author = getAdminAuthor();
        newsJpaRepository.save(TestData.getGlobalNews(author));
        newsJpaRepository.save(TestData.getGlobalNews(author));
        //when
        Page<GlobalNews> out = sut.getPage(1, 1);
        //them
        Assertions.assertThat(out.getTotalPages()).isEqualTo(2);

    }

    @Test
    @Transactional
    void getById() {
        //given
        User author = getAdminAuthor();
        GlobalNews searched = newsJpaRepository.save(TestData.getGlobalNews(author));
        //when & then
        Optional<GlobalNews> out = sut.getById(searched.getId());
        Assertions.assertThat(out).isPresent().containsSame(searched);
    }

    @Test
    @Transactional
    void should_save_news() {
        //given
        User author = getAdminAuthor();
        GlobalNews in = TestData.getGlobalNews(author);
        //when
        GlobalNews out = sut.save(in);
        in.setId(out.getId());
        //then
        Assertions.assertThat(newsJpaRepository.findAll()).hasSize(1);
        Assertions.assertThat(in).isEqualTo(out);
    }

    @Test
    void delete() {
        //given
        User author = getAdminAuthor();
        GlobalNews in = newsJpaRepository.save(TestData.getGlobalNews(author));
        //when
        sut.delete(in);
        //then
        Assertions.assertThat(newsJpaRepository.count()).isZero();
    }

    private User getAdminAuthor() {
        User author = TestData.getAdmin();
        author = userJPARepository.saveAndFlush(author);
        return author;
    }
}