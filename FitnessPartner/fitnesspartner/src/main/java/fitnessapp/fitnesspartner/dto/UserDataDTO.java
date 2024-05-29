package fitnessapp.fitnesspartner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserDataDTO {
    private LocalDate date;
    private int height;
    private int weight;

    public UserDataDTO(LocalDate date, int height, int weight) {
        this.date = date;
        this.height = height;
        this.weight = weight;
    }
}
