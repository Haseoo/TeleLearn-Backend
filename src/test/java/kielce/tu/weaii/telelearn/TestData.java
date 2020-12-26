package kielce.tu.weaii.telelearn;

import kielce.tu.weaii.telelearn.models.*;
import kielce.tu.weaii.telelearn.models.courses.*;
import kielce.tu.weaii.telelearn.requests.*;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

@UtilityClass
public class TestData {
    public User getAdmin() {
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

    public Attachment getAttachment() {
        Attachment attachment = new Attachment();
        AttachmentData attachmentData = new AttachmentData();
        attachmentData.setData(new byte[]{0x21, 0x37, 0x4A, 0x50, 0x32, 0x20, 0x47, 0x4d, 0x44});
        attachmentData.setAttachment(attachment);
        attachment.setAttachmentData(Arrays.asList(attachmentData));
        attachment.setUploadTime(LocalDateTime.now());
        attachment.setFileType("Json Partial II Graphical Module Data");
        attachment.setFileName("File");
        return attachment;
    }

    public StudentStatsRecord getStudentStats(Student student) {
        StudentStatsRecord studentStatsRecord = new StudentStatsRecord();
        studentStatsRecord.setScheduleId(1L);
        studentStatsRecord.setCourseId(1L);
        studentStatsRecord.setDate(LocalDate.of(2010, 4, 10));
        studentStatsRecord.setStartTime(LocalTime.of(21, 37));
        studentStatsRecord.setLearningTime(Duration.ZERO);
        studentStatsRecord.setStudent(student);
        return studentStatsRecord;
    }

    public Course getCourse(Teacher owner, Student student) {
        Course course = new Course();
        course.setOwner(owner);
        course.setName("Test name");
        course.setWelcomePageHtmlContent("<br>");
        course.setPublicCourse(true);
        course.setAutoAccept(true);
        course.setStudentsAllowedToPost(true);
        course.setStudents(new ArrayList<>());
        course.setPosts(new ArrayList<>());
        course.setTasks(new ArrayList<>());

        CourseStudent cs = new CourseStudent();
        cs.setAccepted(true);
        cs.setStudent(student);
        cs.setCourse(course);
        course.getStudents().add(cs);

        return course;
    }

    public Post getPost(Course course, User author) {
        Post post = new Post();
        post.setContent("content");
        post.setPostVisibility(PostVisibility.EVERYONE);
        post.setPublicationTime(LocalDateTime.now());
        post.setCommentingAllowed(true);
        post.setCourse(course);
        post.setAuthor(author);
        post.setAttachments(new ArrayList<>());
        post.setComments(new ArrayList<>());

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setContent("comment");
        comment.setPost(post);
        comment.setPublicationTime(LocalDateTime.now());
        post.getComments().add(comment);

        return post;
    }

    public Task getTask(Course course) {
        Task task = new Task();
        task.setName("name");
        task.setDescription("description");
        task.setLearningTime(Duration.ofMinutes(65));
        task.setDueDate(LocalDate.of(2020, 12, 12));
        task.setCourse(course);
        task.setPlanRecords(new ArrayList<>());
        task.setPreviousTasks(new ArrayList<>());
        task.setNextTasks(new ArrayList<>());
        task.setStudents(new ArrayList<>());
        return task;
    }

    public TaskScheduleRecord getTaskScheduleRecord(Task task, Student student) {
        TaskScheduleRecord taskScheduleRecord = new TaskScheduleRecord();
        taskScheduleRecord.setDate(task.getDueDate());
        taskScheduleRecord.setScheduleTime(LocalTime.of(12, 12));
        taskScheduleRecord.setPlannedTime(task.getLearningTime());
        taskScheduleRecord.setLearningTime(Duration.ZERO);
        taskScheduleRecord.setStudent(student);
        taskScheduleRecord.setTask(task);
        return taskScheduleRecord;
    }

    public GlobalNewsRequest getGlobalNewsRequest() {
        return new GlobalNewsRequest("title",
                1L,
                "brief",
                "<br>brrr",
                LocalDateTime.now());
    }

    public TeacherRegisterRequest getTeacherRegisterRequest() {
        return new TeacherRegisterRequest("teacher",
                "brrrr".toCharArray(),
                "test@t.t",
                "name",
                "surname",
                "unit",
                "titile");
    }

    public TeacherUpdateRequest getTeacherUpdateRequest() {
        return new TeacherUpdateRequest("new@email",
                "newName",
                "newSurname",
                "newUnit",
                "newTitle");
    }

    public StudentRegisterRequest getStudentRegisterRequest() {
        return new StudentRegisterRequest("student",
                "brrrr".toCharArray(),
                "test@s.s",
                "name",
                "surname",
                "unit");
    }

    public StudentUpdateRequest getStudentUpdateRequest() {
        return new StudentUpdateRequest("new@email",
                "newName",
                "newSurname",
                "newUnit",
                12,
                24);
    }
}
