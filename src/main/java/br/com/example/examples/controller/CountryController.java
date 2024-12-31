package br.com.example.examples.controller;

import br.com.example.examples.domain.dto.CountryRequestDto;
import br.com.example.examples.domain.dto.CountryResponseDto;
import br.com.example.examples.services.CountryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Validated
@RestController
@RequestMapping("${domain}")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @PostMapping("/countries")
    public ResponseEntity<CountryResponseDto> create(@RequestBody @Valid CountryRequestDto countryRequestDto) {
        return ResponseEntity.ok(countryService.create(countryRequestDto));
    }

    @GetMapping("/countries")
    public String get() {
        return "/countries";
    }
}
