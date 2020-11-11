package kielce.tu.weaii.telelearn.controllers;

import kielce.tu.weaii.telelearn.requests.courses.PostRequest;
import kielce.tu.weaii.telelearn.requests.courses.TaskRequest;
import kielce.tu.weaii.telelearn.services.ports.TaskService;
import kielce.tu.weaii.telelearn.views.courses.TaskView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/task")
public class TaskController {
    private final TaskService taskService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<TaskView> getById(@PathVariable Long id) {
        return new ResponseEntity<>(TaskView.from(taskService.getById(id)), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Object> add(@Valid @ModelAttribute TaskRequest request,
                                      @RequestParam(required = false) List<MultipartFile> files){
        try {
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(taskService.add(request, files).getId()).toUri();
            return ResponseEntity.created(location).build();
        } catch (IOException e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @Valid @ModelAttribute TaskRequest request,
                                         @RequestParam(required = false) List<MultipartFile> files) {
        try {
            taskService.update(id, request, files);
        } catch (IOException e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Object> remove(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
