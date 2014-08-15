package dataxu.intranet.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Client {
    @JsonProperty("Recommandations")
    @Size(max = 2, message = "Please choose 2 recommendations at maximum")
    private final List<Recommendation> recommendations = new ArrayList<>();
    @JsonProperty("Id")
    private UUID id;
    @JsonProperty("RegistrationDate")
    private OffsetDateTime registrationDate;
    @JsonProperty("Company")
    private String company;
    @JsonProperty("FirstName")
    @NotNull
    @Size(min = 1, max = 100)
    private String firstName;
    @JsonProperty("LastName")
    @NotNull
    @Size(min = 1, max = 100)
    private String lastName;

    public UUID getId() {
        return id;
    }

    public OffsetDateTime getRegistrationDate() {
        return registrationDate;
    }

    public String getCompany() {
        return company;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    void updateFrom(Client other) {
        registrationDate = other.registrationDate;
        company = other.company;
        firstName = other.firstName;
        lastName = other.lastName;
        recommendations.clear();
        recommendations.addAll(other.recommendations);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Client that = (Client) other;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("id", id)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("company", company)
                .add("registrationDate", registrationDate)
                .toString();
    }
}
