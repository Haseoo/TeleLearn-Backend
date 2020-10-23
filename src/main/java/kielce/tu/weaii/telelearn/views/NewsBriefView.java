package kielce.tu.weaii.telelearn.views;

import kielce.tu.weaii.telelearn.models.GlobalNews;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class NewsBriefView {
    Long id;
    String title;
    String author;
    String brief;
    LocalDateTime publicationDate;
    boolean isMore;

    public static NewsBriefView of(GlobalNews globalNews) {
        return new NewsBriefView(globalNews.getId(),
                globalNews.getTitle(),
                globalNews.getAuthor(),
                globalNews.getBrief(),
                globalNews.getPublicationDate(),
                globalNews.getHtmlContent() != null);
    }
}
