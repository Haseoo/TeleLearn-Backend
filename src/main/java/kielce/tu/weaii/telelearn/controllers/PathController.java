package kielce.tu.weaii.telelearn.controllers;

import kielce.tu.weaii.telelearn.requests.courses.PathRequest;
import kielce.tu.weaii.telelearn.services.ports.PathService;
import kielce.tu.weaii.telelearn.views.courses.PathView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/path")
public class PathController {

    private final PathService pathService;

    @GetMapping(path = "{id}")
    public ResponseEntity<PathView> getById(@PathVariable Long id) {
        return new ResponseEntity<>(PathView.from(pathService.getById(id)), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Object> add(@Valid @RequestBody PathRequest request) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(pathService.add(request).getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @Valid @RequestBody PathRequest request) {
        pathService.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        pathService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
