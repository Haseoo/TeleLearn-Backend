package kielce.tu.weaii.telelearn;

import kielce.tu.weaii.telelearn.models.GlobalNews;
import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.models.UserRole;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;

@UtilityClass
public class TestData {
    public User GetAdmin() {
        User user = new User();
        user.setId(1L);
        user.setUsername("Admin");
        user.setPassword("xxx".toCharArray());
        user.setEmail("t@t.com");
        user.setEnabled(true);
        user.setName("Admin");
        user.setSurname("Admin");
        user.setReceivedMessages(new ArrayList<>());
        user.setSendMessages(new ArrayList<>());
        user.setUserRole(UserRole.ADMIN);
        return user;
    }

    public GlobalNews getGlobalNews(User author) {
        GlobalNews globalNews = new GlobalNews();
        globalNews.setId(1L);
        globalNews.setAuthor(author);
        globalNews.setHtmlContent("<br>");
        globalNews.setBrief("brrr");
        globalNews.setPublicationDate(LocalDateTime.now());
        globalNews.setTitle("title");
        return globalNews;
    }
}
