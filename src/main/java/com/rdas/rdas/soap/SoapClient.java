package com.rdas.rdas.soap;

import com.rdas.soap.generated.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class SoapClient {

    private final WebServiceTemplate webServiceTemplate;

    @Value("${soap.uri}")
    private String uri;

    // ── LIST OPERATIONS ───────────────────────────────────────────────────

    public ListOfContinentsByNameResponse listContinents() {
        log.debug("SOAP call: ListOfContinentsByName");
        ListOfContinentsByName request = new ListOfContinentsByName();
        return (ListOfContinentsByNameResponse) webServiceTemplate.marshalSendAndReceive(uri, request);
    }

    public ListOfCurrenciesByNameResponse listCurrencies() {
        log.debug("SOAP call: ListOfCurrenciesByName");
        ListOfCurrenciesByName request = new ListOfCurrenciesByName();
        return (ListOfCurrenciesByNameResponse) webServiceTemplate.marshalSendAndReceive(uri, request);
    }

    public ListOfLanguagesByNameResponse listLanguages() {
        log.debug("SOAP call: ListOfLanguagesByName");
        ListOfLanguagesByName request = new ListOfLanguagesByName();
        return (ListOfLanguagesByNameResponse) webServiceTemplate.marshalSendAndReceive(uri, request);
    }

    public ListOfCountryNamesGroupedByContinentResponse listCountriesByContinent() {
        log.debug("SOAP call: ListOfCountryNamesGroupedByContinent");
        ListOfCountryNamesGroupedByContinent request = new ListOfCountryNamesGroupedByContinent();
        return (ListOfCountryNamesGroupedByContinentResponse) webServiceTemplate.marshalSendAndReceive(uri, request);
    }

    // ── SINGLE COUNTRY OPERATIONS ─────────────────────────────────────────

    public CountryNameResponse countryName(String isoCode) {
        log.debug("SOAP call: CountryName [{}]", isoCode);
        CountryName request = new CountryName();
        request.setSCountryISOCode(isoCode);
        return (CountryNameResponse) webServiceTemplate.marshalSendAndReceive(uri, request);
    }

    public CapitalCityResponse capitalCity(String isoCode) {
        log.debug("SOAP call: CapitalCity [{}]", isoCode);
        CapitalCity request = new CapitalCity();
        request.setSCountryISOCode(isoCode);
        return (CapitalCityResponse) webServiceTemplate.marshalSendAndReceive(uri, request);
    }

    public CountryCurrencyResponse countryCurrency(String isoCode) {
        log.debug("SOAP call: CountryCurrency [{}]", isoCode);
        CountryCurrency request = new CountryCurrency();
        request.setSCountryISOCode(isoCode);
        return (CountryCurrencyResponse) webServiceTemplate.marshalSendAndReceive(uri, request);
    }

    public CountryFlagResponse countryFlag(String isoCode) {
        log.debug("SOAP call: CountryFlag [{}]", isoCode);
        CountryFlag request = new CountryFlag();
        request.setSCountryISOCode(isoCode);
        return (CountryFlagResponse) webServiceTemplate.marshalSendAndReceive(uri, request);
    }

    public CountryIntPhoneCodeResponse countryPhoneCode(String isoCode) {
        log.debug("SOAP call: CountryIntPhoneCode [{}]", isoCode);
        CountryIntPhoneCode request = new CountryIntPhoneCode();
        request.setSCountryISOCode(isoCode);
        return (CountryIntPhoneCodeResponse) webServiceTemplate.marshalSendAndReceive(uri, request);
    }

    public CountriesUsingCurrencyResponse countriesUsingCurrency(String currencyCode) {
        log.debug("SOAP call: CountriesUsingCurrency [{}]", currencyCode);
        CountriesUsingCurrency request = new CountriesUsingCurrency();
        request.setSISOCurrencyCode(currencyCode);
        return (CountriesUsingCurrencyResponse) webServiceTemplate.marshalSendAndReceive(uri, request);
    }

    public CurrencyNameResponse currencyName(String currencyCode) {
        log.debug("SOAP call: CurrencyName [{}]", currencyCode);
        CurrencyName request = new CurrencyName();
        request.setSCurrencyISOCode(currencyCode);
        return (CurrencyNameResponse) webServiceTemplate.marshalSendAndReceive(uri, request);
    }

    public LanguageNameResponse languageName(String languageCode) {
        log.debug("SOAP call: LanguageName [{}]", languageCode);
        LanguageName request = new LanguageName();
        request.setSISOCode(languageCode);
        return (LanguageNameResponse) webServiceTemplate.marshalSendAndReceive(uri, request);
    }
}