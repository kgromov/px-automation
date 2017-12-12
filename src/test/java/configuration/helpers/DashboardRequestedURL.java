package configuration.helpers;

import config.Config;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

/**
 * Created by konstantin on 05.02.2017.
 */
public class DashboardRequestedURL {
    private String url;
    private String params;
    private String filter;
    private String sorting;
    private String page;
    private String count;

    private DashboardRequestedURL(Builder builder) {
        this.url = builder.url;
        this.params = builder.params;
        this.filter = builder.filter;
        this.sorting = builder.sorting;
        this.page = builder.page;
        this.count = builder.count;
    }

    public String getRequestedURL() {
        StringBuilder builder = new StringBuilder(url);
        if (params != null && !params.isEmpty()) builder.append(params);
        if (filter != null && !filter.isEmpty()) builder.append(filter);
        if (sorting != null && !sorting.isEmpty()) builder.append(sorting);
        if (page != null && !page.isEmpty()) builder.append(page);
        if (count != null && !count.isEmpty()) builder.append(count);
        return builder.toString().replaceFirst("&", "?");
    }

    public static final class Builder {
        String url;
        String params;
        String filter;
        String sorting;
        String page;
        String count;

        public Builder() {
        }

        public Builder withAbsoluteURL(String url) {
            this.url = url;
            return this;
        }

        public Builder withRelativeURL(String url) {
            this.url = Config.testUrl + url;
            return this;
        }

        public Builder withParams(String key, String value) {
            this.params = "&" + key + "=" + value;
            return this;
        }

        public Builder withParams(List<String> keys, List<String> values) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < keys.size(); i++) {
                builder.append("&").append(keys.get(i)).append("=").append(values.get(i));
            }
            this.params = builder.toString();
            return this;
        }

        public Builder withPage(int page) {
            this.page = "&page=" + page;
            return this;
        }

        public Builder withCount(int count) {
            this.count = "&count=" + count;
            return this;
        }

        public Builder filter(String key, String value) {
            filter(Collections.singletonList(key), Collections.singletonList(value));
            return this;
        }

        public Builder filter(List<String> keys, List<String> values) {
            StringBuilder builder = new StringBuilder("&filter=");
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < keys.size(); i++) {
                builder.append(keys.get(i)).append(",")
                        .append(values.get(i)).append(";");
            }
            builder.append(jsonObject.toString());
            this.filter = builder.toString();
            return this;
        }

        public Builder sort(String by, String how) {
//            this.sorting = "&sorting=" + by + "," + how + ";";
            this.sorting = "&sorting=" + by + ":" + how + ",;";
            return this;
        }

        public DashboardRequestedURL build() {
            return new DashboardRequestedURL(this);
        }
    }

}
