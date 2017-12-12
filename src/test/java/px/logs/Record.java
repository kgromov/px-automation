package px.logs;

import java.util.Date;

/**
 * Created by konstantin on 24.09.2017.
 */
public class Record {
    private Date time_;
    private String time;
    private String name;
    private String type;
    private String stackTrace;
    private String previousSteps;
    // pattern to parse
    public static final String LOG_DATE_TIME = "yyyy-MM-dd HH:mm:ss.S";

    private Record(Builder builder) {
        this.time = builder.time;
        this.name = builder.name;
        this.type = builder.type;
        this.stackTrace = builder.stackTrace;
        this.previousSteps = builder.previousSteps;
    }

    public Date getDate() {
        return time_;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public String getPreviousSteps() {
        return previousSteps;
    }

    public static class Builder {
        String time;
        String name;
        String type;
        String stackTrace;
        String previousSteps;

        public Builder() {
        }

        public Builder withDate(String time){
            this.time = time;
            return this;
        }

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public Builder withType(String type){
            this.type = type;
            return this;
        }

        public Builder withStackTrace(String stackTrace){
            this.stackTrace = stackTrace;
            return this;
        }

        public Builder withPreviousSteps(String previousSteps){
            this.previousSteps = previousSteps;
            return this;
        }

        public Record build() {
            return new Record(this);
        }
    }
}