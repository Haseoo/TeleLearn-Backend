package kielce.tu.weaii.telelearn.repositories;

import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.repositories.jpa.UserJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;

@SpringBootTest
@Tag(INTEGRATION_TEST)
class UserRepositoryTest {

    @Autowired
    private UserRepository sut;

    @Autowired
    private UserJPARepository userJPARepository;

    @BeforeEach
    void setUp() {
        userJPARepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userJPARepository.deleteAll();
    }

    @Test
    @Transactional
    void should_save_user() {
        //given
        User in = TestData.GetAdmin();
        //when
        User out = sut.save(in);
        in.setId(out.getId());
        //then
        assertUserEquals(in, out);
        Assertions.assertThat(userJPARepository.findAll()).contains(out);
    }

    @Test
    void should_get_by_id() {
        //given
        User searched = userJPARepository.save(TestData.GetAdmin());
        //when
        Optional<User> out =  sut.getById(searched.getId());
        //then
        Assertions.assertThat(out.isPresent()).isTrue();
        assertUserEquals(searched, out.get());
    }

    @Test
    void should_delete_user() {
        //given
        User user = userJPARepository.save(TestData.GetAdmin());
        userJPARepository.save(TestData.GetAdmin());
        //when
        sut.delete(user);
        //then
        Assertions.assertThat(userJPARepository.findAll()).doesNotContain(user);
    }

    @Test
    @Transactional
    void should_return_all_users() {
        //given
        User user1 = userJPARepository.save(TestData.GetAdmin());
        User user2 = userJPARepository.save(TestData.GetAdmin());
        //when & then
        Assertions.assertThat(sut.getAll()).contains(user1, user2);
    }


    @Test
    void should_find_user_by_login_when_searching_by_login_or_email() {
        //given
        User searched = userJPARepository.save(TestData.GetAdmin());
        //when
        Optional<User> out = sut.getUserByLoginOrEmail(searched.getUsername());
        //then
        Assertions.assertThat(out.isPresent()).isTrue();
        assertUserEquals(searched, out.get());
    }

    @Test
    void should_find_user_by_email_when_searching_by_login_or_email() {
        //given
        User searched = userJPARepository.save(TestData.GetAdmin());
        //when
        Optional<User> out = sut.getUserByLoginOrEmail(searched.getEmail());
        //then
        Assertions.assertThat(out.isPresent()).isTrue();
        assertUserEquals(searched, out.get());
    }
    @Test
    void should_find_user_by_email() {
        //given
        User searched = userJPARepository.save(TestData.GetAdmin());
        //when
        Optional<User> out = sut.getUserByEmail(searched.getEmail());
        //then
        Assertions.assertThat(out.isPresent()).isTrue();
        assertUserEquals(searched, out.get());
    }

    @Test
    void should_find_user_by_login() {
        //given
        User searched = userJPARepository.save(TestData.GetAdmin());
        //when
        Optional<User> out = sut.getUserByLogin(searched.getUsername());
        //then
        Assertions.assertThat(out.isPresent()).isTrue();
        assertUserEquals(searched, out.get());
    }


    private void assertUserEquals(User in, User out) {
        Assertions.assertThat(out.getId()).isEqualTo(in.getId());
        Assertions.assertThat(out.getUsername()).isEqualTo(in.getUsername());
        Assertions.assertThat(out.getEmail()).isEqualTo(in.getEmail());
        Assertions.assertThat(out.getName()).isEqualTo(in.getName());
        Assertions.assertThat(out.getUserRole()).isEqualTo(in.getUserRole());
        Assertions.assertThat(out.isEnabled()).isTrue();
    }
}