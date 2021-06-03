# exchange-rate-service

## Introduction
This Service offers an API for delivering ExchangeRates, the supported currencies and calculating the Exchanges 
from one currencies to another. The API alway working with the offical exchange rates published daily by the
european central bank. These are accessible under: https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html

## Starting the Applicaation
You may build and start with docker-compose (build/up).
When container started, you may access application under container
adress which is visible by the command docker ps.

## Accessing API
The api has two different endpoints.

###Endpoint: /rest/currencies
Delivers a Response with all supported currencies with a counter how often they were accessed since Application start.
#### Response:
    {
        "name":"AUD",
        "amount":1.5792,
        "accessCounter":0
    },
    {
        "name":"EUR",
        "amount":1.0,
        "accessCounter":0
    },
    ...

###Endpoint: /rest/currencies?from={from}&to={to}(&amount={amount})
Delivers a the exchange between from currency and to currency for a given amount. The Amount is an optional parameter with a default value of 1.0
So, when ignoring the amount parameter it's basically the ExchangeRate between the two currencies. You get a 404, if at least one of the currencies 
in from or to parameter is not supported. In addition this Endpoint is returning a link to a dynamic chart for the value pair.
#### Response:
    {
        "from":
            {
                "name":"USD",
                "amount":7.42,
                "accessCounter":1
            },
        "to":
            {
                "name":"DKK",
                "amount":45.27679496,
                "accessCounter":1
            },
        "chart":"https://www.xe.com/currencycharts/?from=USD&to=DKK",
        "exchangeRate":6.10199393
}