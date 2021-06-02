package com.software.exchange.domain;

public class Exchange {

    private Currency from;
    private Currency to;
    private String chart;

    public Exchange(Currency from, Currency to) {
        this.from = from;
        this.to = to;
        this.chart = String.format("https://www.xe.com/currencycharts/?from=%s&to=%s", from.getName(), to.getName());
    }

    public static Exchange createEURTo(String currencyName, double amount) {
        return new Exchange(new Currency("EUR", 1.0d), new Currency(currencyName, amount));
    }

    public Currency getFrom() {
        return from;
    }

    public Currency getTo() {
        return to;
    }

    public String getChart() {
        return chart;
    }

    public void normalize(){
        if(from.getAmount() != 1.0d){
            double divisor = from.getAmount();
            from = from.divideBy(divisor);
            to = to.divideBy(divisor);
        }
    }

    public void multiplyBy(double factor){
        from = from.multiplyBy(factor);
        to = to.multiplyBy(factor);
    }
}
