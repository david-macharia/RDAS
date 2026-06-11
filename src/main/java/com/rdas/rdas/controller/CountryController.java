package com.rdas.rdas.controller;

import com.rdas.rdas.model.CountryDetails;
import com.rdas.rdas.service.CountryService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections; // Fixed: Missing collection utility import added here
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Validated
@RestController
@RequestMapping("/api/v1/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryDataService;

    @GetMapping
    public ResponseEntity<Page<CountryDetails>> getCountries(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String continent,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort
    ) {
        // Fetch raw data from the proxy cache service layer
        List<CountryDetails> dataset = countryDataService.getAllCountries();
        Stream<CountryDetails> stream = dataset.stream();

        // ── IN-MEMORY FILTERING & SEARCHING ENGINE ──
        if (search != null && !search.isBlank()) {
            String searchLower = search.toLowerCase().trim();
            stream = stream.filter(c -> c.name().toLowerCase().contains(searchLower) || 
                                        c.isoCode().toLowerCase().contains(searchLower));
        }
        if (continent != null && !continent.isBlank()) {
            stream = stream.filter(c -> c.continent().equalsIgnoreCase(continent.trim()));
        }
        if (currency != null && !currency.isBlank()) {
            stream = stream.filter(c -> c.currencyCode().equalsIgnoreCase(currency.trim()) || 
                                        c.currencyName().toLowerCase().contains(currency.toLowerCase().trim()));
        }
        if (language != null && !language.isBlank()) {
            stream = stream.filter(c -> c.languages().stream()
                    .anyMatch(l -> l.equalsIgnoreCase(language.trim())));
        }

        List<CountryDetails> filteredList = stream.collect(Collectors.toList());

        // ── DYNAMIC SORTING ENGINE ──
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "asc";

        Comparator<CountryDetails> comparator = switch (sortField.toLowerCase()) {
            case "code" -> Comparator.comparing(CountryDetails::isoCode);
            case "continent" -> Comparator.comparing(CountryDetails::continent);
            case "currency" -> Comparator.comparing(CountryDetails::currencyCode);
            default -> Comparator.comparing(CountryDetails::name); // default fallback to sort by name
        };

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }
        filteredList.sort(comparator);

        // ── PAGINATION SLICING ──
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredList.size());

        List<CountryDetails> paginatedContent = Collections.emptyList();
        if (start <= filteredList.size() && start >= 0) {
            paginatedContent = filteredList.subList(start, end);
        }

        return ResponseEntity.ok(new PageImpl<>(paginatedContent, pageable, filteredList.size()));
    }

    @GetMapping("/{isoCode}")
    public ResponseEntity<CountryDetails> getCountryByIsoCode(@PathVariable String isoCode) {
        return countryDataService.getAllCountries().stream()
                .filter(c -> c.isoCode().equalsIgnoreCase(isoCode.trim()))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}