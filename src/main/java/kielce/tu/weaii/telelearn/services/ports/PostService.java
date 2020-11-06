package kielce.tu.weaii.telelearn.services.ports;

import kielce.tu.weaii.telelearn.models.courses.Comment;
import kielce.tu.weaii.telelearn.models.courses.Post;
import kielce.tu.weaii.telelearn.requests.courses.PostRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    Post getById(Long id);
    List<Post> getCoursePosts(Long courseId);
    Post addPost(PostRequest request, List<MultipartFile> attachments) throws IOException;
    Post updatePost(Long id, PostRequest postRequest, List<MultipartFile> newAttachments) throws IOException;
    List<Comment> getComments(Long postId);
    void removePost(Long id);
}
