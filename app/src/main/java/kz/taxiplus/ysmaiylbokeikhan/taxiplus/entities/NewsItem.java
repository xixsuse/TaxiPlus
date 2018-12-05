package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import java.util.List;

public class NewsItem {
    private String id;
    private String text;
    private String title;
    private String created;

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public String getCreated() {
        return created;
    }

    public class NewsResponse{
        private List<NewsItem> messages;

        public List<NewsItem> getMessages() {
            return messages;
        }
    }
}
