package com.software.exchangerateservice.domain;

/**
 * TODO The currency show know his referenceRate.
 * reference Rate is calculated from EUR.
 */
public class Currency {
    private String name;

    public Currency(String name, double rateToEUR) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
