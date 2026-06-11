package com.rdas.rdas.model;


import java.io.Serializable;
import java.util.List;

public record CountryDetails(
    String isoCode,
    String name,
    String capital,
    String continent,
    String currencyCode,
    String currencyName,
    String phoneCode,
    String flagUrl,
    List<String> languages
) implements Serializable {}