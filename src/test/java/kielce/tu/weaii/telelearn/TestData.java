package kielce.tu.weaii.telelearn;

import kielce.tu.weaii.telelearn.models.*;
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

    public Teacher getTeacher() {
        Teacher teacher = new Teacher();
        teacher.setId(2L);
        teacher.setUsername("Admin");
        teacher.setPassword("xxx".toCharArray());
        teacher.setEmail("t@t.com");
        teacher.setEnabled(true);
        teacher.setName("Admin");
        teacher.setSurname("Admin");
        teacher.setReceivedMessages(new ArrayList<>());
        teacher.setSendMessages(new ArrayList<>());
        teacher.setUserRole(UserRole.TEACHER);
        teacher.setCourses(new ArrayList<>());
        teacher.setTitle("dr inż.");
        teacher.setUnit("unit");
        return teacher;
    }

    public Student getStudent() {
        Student student = new Student();
        student.setId(3L);
        student.setUsername("Admin");
        student.setPassword("xxx".toCharArray());
        student.setEmail("t@t.com");
        student.setEnabled(true);
        student.setName("Admin");
        student.setSurname("Admin");
        student.setReceivedMessages(new ArrayList<>());
        student.setSendMessages(new ArrayList<>());
        student.setUserRole(UserRole.STUDENT);
        student.setDailyLearningTime(Student.DEFAULT_DAILY_LEARNING_TIME);
        student.setCourses(new ArrayList<>());
        student.setPlanRecords(new ArrayList<>());
        student.setUnit("unit");
        student.setTasks(new ArrayList<>());
        student.setLearningTime(new ArrayList<>());
        return student;
    }

    public Message getMessage(User sender, User receiver) {
        Message message = new Message();
        message.setReceiver(receiver);
        message.setSender(sender);
        message.setSendTime(LocalDateTime.now());
        message.setContent("A message!");
        message.setRead(false);
        return message;
    }
}
