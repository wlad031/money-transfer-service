package org.wlad031.money.transfer.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateNewAccountRequestBody {
    private String name;
    private String currency;

    public CreateNewAccountRequestBody() {
        this(null, null);
    }

    public CreateNewAccountRequestBody(String name, String currency) {
        this.name = name;
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    @JsonProperty(value = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    @JsonProperty(value = "currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
