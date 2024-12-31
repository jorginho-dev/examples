package br.com.example.examples.services;


import br.com.example.examples.domain.dto.CountryRequestDto;
import br.com.example.examples.domain.dto.CountryResponseDto;
import br.com.example.examples.domain.mapper.CountryMapper;
import br.com.example.examples.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public CountryResponseDto create(CountryRequestDto countryRequestDto) {
        var country = countryMapper.toCountry(countryRequestDto.getName());
        var countryEntity = countryRepository.save(country);
        return countryMapper.toCountryResponseDto(countryEntity);
    }
}
