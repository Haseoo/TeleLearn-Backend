package kielce.tu.weaii.telelearn.controllers;

import kielce.tu.weaii.telelearn.requests.GlobalNewsRequest;
import kielce.tu.weaii.telelearn.services.adapters.GlobalNewsServiceImpl;
import kielce.tu.weaii.telelearn.services.ports.UserService;
import kielce.tu.weaii.telelearn.views.GlobalNewsView;
import kielce.tu.weaii.telelearn.views.PageView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/news")
public class GlobalNewsController {
    private final GlobalNewsServiceImpl globalNewsService;
    private final UserService userService;
    private int i = 0;

    @GetMapping(path = "/get/{id}")
    public ResponseEntity<GlobalNewsView> getById(@PathVariable Long id) {
        return new ResponseEntity<>(GlobalNewsView.from(globalNewsService.getById(id)), HttpStatus.OK);
    }

    @GetMapping(path = "/get")
    public ResponseEntity<PageView<GlobalNewsView>> getBriefPage(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return new ResponseEntity<>(PageView.of(globalNewsService.getPage(pageSize, pageNo), GlobalNewsView::from), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        globalNewsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> edit(@PathVariable Long id,@Valid @RequestBody GlobalNewsRequest request) {
        globalNewsService.edit(id, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> add(@RequestBody @Valid GlobalNewsRequest request) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/get/{id}")
                .buildAndExpand(globalNewsService.add(request).getId()).toUri();
        return ResponseEntity.created(location).build();
    }
}
