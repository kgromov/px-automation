package configuration.helpers;

import config.Config;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

/**
 * Created by konstantin on 05.02.2017.
 */
public class RequestedURL {
    private String url;
    private String params;
    private String filter;
    private String sorting;
    private String count;

    private RequestedURL(Builder builder) {
        this.url = builder.url;
        this.params = builder.params;
        this.filter = builder.filter;
        this.sorting = builder.sorting;
        this.count = "&count=" + builder.count;
    }

    public String getRequestedURL() {
        StringBuilder builder = new StringBuilder(url);
        if (params != null && !params.isEmpty()) builder.append(params);
        if (filter != null && !filter.isEmpty()) builder.append(filter);
        if (sorting != null && !sorting.isEmpty()) builder.append(sorting);
        if ((filter != null && !filter.isEmpty()) || (sorting != null && !sorting.isEmpty()))
            builder.append(count).append("&page=1");
        return builder.toString().replaceFirst("&", "?");
    }

    public static class Builder {
        String url;
        String params;
        String filter;
        String sorting;
        int count = 100;

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

        public Builder filter(String key, String value) {
            filter(Collections.singletonList(key), Collections.singletonList(value));
            return this;
        }

        public Builder filter(List<String> keys, List<String> values) {
            StringBuilder builder = new StringBuilder("&filter=");
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < keys.size(); i++) {
                jsonObject.put(keys.get(i), values.get(i));
            }
            builder.append(jsonObject.toString());
            this.filter = builder.toString();
            return this;
        }

        public Builder filterByKey(String key) {
            this.filter = String.format("&filter={\"%s\"}", key);
            return this;
        }

        public Builder withEmptyFilter() {
            this.filter = "&filter={}";
            return this;
        }

        public Builder sort(String by, String how) {
            StringBuilder builder = new StringBuilder("&sorting=");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(by, how);
            builder.append(jsonObject.toString());
            this.sorting = builder.toString();
            return this;
        }

        public Builder withEmptySorting() {
            this.sorting = "&sorting={}";
            return this;
        }

        public Builder withCount(int count) {
            this.count = count;
            return this;
        }

        public RequestedURL build() {
            return new RequestedURL(this);
        }
    }

}
