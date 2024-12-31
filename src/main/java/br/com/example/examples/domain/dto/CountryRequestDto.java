package br.com.example.examples.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CountryRequestDto {
    @NotBlank(message = "Name is required!")
    private String name;
}
