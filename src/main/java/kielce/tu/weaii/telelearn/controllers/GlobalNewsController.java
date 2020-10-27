package kielce.tu.weaii.telelearn.controllers;

import kielce.tu.weaii.telelearn.models.GlobalNews;
import kielce.tu.weaii.telelearn.repositories.jpa.GlobalNewsJPARepository;
import kielce.tu.weaii.telelearn.services.adapters.GlobalNewsService;
import kielce.tu.weaii.telelearn.views.NewsBriefView;
import kielce.tu.weaii.telelearn.views.PageView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/news")
public class GlobalNewsController {
    private final GlobalNewsService globalNewsService;
    private int i = 0;
    @GetMapping(path = "/get")
    public ResponseEntity<PageView<NewsBriefView>> getBriefPage(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return new ResponseEntity<>(PageView.of(globalNewsService.getPage(pageSize, pageNo), NewsBriefView::of), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> add() {
        GlobalNews g = new GlobalNews();
        g.setTitle("Artykuł" + ++i);
        g.setAuthor("Admin");
        g.setPublicationDate(LocalDateTime.now());
        g.setBrief("Vestibulum ultrices neque vel ipsum commodo, vitae venenatis dolor interdum. Quisque cursus ante ac nisi varius, quis scelerisque lectus consequat. Ut consectetur est eu laoreet mattis. Ut fringilla iaculis mi quis rhoncus. Suspendisse scelerisque nisl sit amet purus facilisis malesuada. Aenean faucibus mauris sed neque elementum, ut suscipit ligula bibendum. Ut quis venenatis velit. Nulla luctus convallis suscipit. Aenean auctor iaculis odio in viverra. Donec justo metus, aliquet sit amet risus eu, tempor tincidunt metus. Mauris rhoncus ultrices urna, ac fringilla libero varius nec. Ut sem magna, rutrum sed aliquet non, vestibulum non mauris. In est nulla, hendrerit at nisl tempor, mattis aliquam mauris. Praesent dapibus sem orci, nec tincidunt purus fermentum eget. Vivamus eleifend dapibus magna sodales faucibus.");
        g.setHtmlContent("<b> Vestibulum ultrices neque vel ipsum commodo </b>");
        g = globalNewsService.add(g);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(g.getId()).toUri();
        return ResponseEntity.created(location).build();
    }
}
