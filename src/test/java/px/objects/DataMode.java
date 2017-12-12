package px.objects;

/**
 * Created by kgr on 2/2/2017.
 */
public class DataMode {
    private String mode;
    private boolean isPositive;
    private boolean isInitByJSON;

    private DataMode(Builder builder) {
        this.mode = builder.mode;
        this.isPositive = builder.isPositive;
    }

    public String getMode() {
        return mode;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public boolean isInitByJSON() {
        return isInitByJSON;
    }

    public static final class Builder {
        String mode;
        boolean isPositive;
        boolean isInitByJSON;

        public Builder() {
        }

        public Builder createData() {
            this.mode = DataModeEnum.CREATE.name();
            return this;
        }

        public Builder updateData() {
            this.mode = DataModeEnum.UPDATE.name();
            return this;
        }

        public Builder deleteData() {
            this.mode = DataModeEnum.DELETE.name();
            return this;
        }

        public Builder positiveData() {
            this.isPositive = true;
            return this;
        }

        public Builder negativeData() {
            this.isPositive = false;
            return this;
        }

        public Builder createByResponse() {
            this.isInitByJSON = true;
            return this;
        }

        public DataMode build() {
            return new DataMode(this);
        }

    }

    public static DataMode getCreatedByResponse() {
        return new DataMode.Builder()
                .createByResponse()
                .positiveData()
                .updateData()
                .build();
    }
}
