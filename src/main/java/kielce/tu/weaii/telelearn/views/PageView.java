package kielce.tu.weaii.telelearn.views;

import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value
public class PageView<T> {
    int currentPage;
    long totalItems;
    List<T> content;

    public static <T, R> PageView<R> of(Page<T> page, Function<T, R> ofView) {
        return new PageView<>(page.getNumber(), page.getTotalElements(), page.getContent().stream().map(ofView).collect(Collectors.toList()));
    }
}
