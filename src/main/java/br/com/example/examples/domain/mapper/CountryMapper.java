package br.com.example.examples.domain.mapper;

import br.com.example.examples.domain.Country;
import br.com.example.examples.domain.dto.CountryResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    CountryResponseDto toCountryResponseDto(Country country);

    Country toCountry(String name);
}
