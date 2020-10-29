package kielce.tu.weaii.telelearn.views;

import kielce.tu.weaii.telelearn.models.GlobalNews;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class GlobalNewsView {
    Long id;
    String title;
    UserView author;
    String brief;
    String htmlContent;
    LocalDateTime publicationDate;

    public static GlobalNewsView from(GlobalNews model) {
        return new GlobalNewsView(model.getId(),
                model.getTitle(),
                UserView.from(model.getAuthor(), false),
                model.getBrief(),
                model.getHtmlContent(),
                model.getPublicationDate());
    }
}
