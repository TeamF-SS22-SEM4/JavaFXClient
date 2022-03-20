package at.fhv.ec.javafxclient.application.dto;

import java.util.Objects;

//TODO move this to shared lib
public class ProductOverviewDTO {
    private String name;
    private String artistName;
    private String releaseYear;

    public static Builder builder() {
        return new Builder();
    }

    public String name() {
        return this.name;
    }

    public String artistName() {
        return this.artistName;
    }

    public String releaseYear() {
        return this.releaseYear;
    }

    public static class Builder {
        private final ProductOverviewDTO instance;

        private Builder() {
            this.instance = new ProductOverviewDTO();
        }

        public Builder withName(String name) {
            this.instance.name = name;
            return this;
        }

        public Builder withArtistName(String artistName) {
            this.instance.artistName = artistName;
            return this;
        }

        public Builder withReleaseYear(String releaseYear) {
            this.instance.releaseYear = releaseYear;
            return this;
        }

        public ProductOverviewDTO build() {
            Objects.requireNonNull(this.instance.name, "name must be set in ProductOverviewDTO");
            Objects.requireNonNull(this.instance.artistName, "artistFirstname must be set in ProductOverviewDTO");
            Objects.requireNonNull(this.instance.releaseYear, "releaseYear must be set in ProductOverviewDTO");

            return this.instance;
        }
    }
}
