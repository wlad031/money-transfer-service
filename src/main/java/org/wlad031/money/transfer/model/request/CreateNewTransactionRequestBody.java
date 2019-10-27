package org.wlad031.money.transfer.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class CreateNewTransactionRequestBody {
    private String senderId;
    private String receiverId;
    private BigDecimal amountSent;
    private BigDecimal amountReceived;
    private ZonedDateTime dateTime;

    public CreateNewTransactionRequestBody(String senderId, String receiverId, BigDecimal amountSent, BigDecimal amountReceived, ZonedDateTime dateTime) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amountSent = amountSent;
        this.amountReceived = amountReceived;
        this.dateTime = dateTime;
    }

    public CreateNewTransactionRequestBody(String senderId, String receiverId, BigDecimal amountSent, BigDecimal amountReceived) {
        this(senderId, receiverId, amountSent, amountReceived, null);
    }

    public CreateNewTransactionRequestBody() {
        this(null, null, null, null, null);
    }

    public String getSenderId() {
        return senderId;
    }

    @JsonProperty(value = "senderId")
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    @JsonProperty(value = "receiverId")
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public BigDecimal getAmountSent() {
        return amountSent;
    }

    @JsonProperty(value = "amountSent")
    public void setAmountSent(BigDecimal amountSent) {
        this.amountSent = amountSent;
    }

    public BigDecimal getAmountReceived() {
        return amountReceived;
    }

    @JsonProperty(value = "amountReceived")
    public void setAmountReceived(BigDecimal amountReceived) {
        this.amountReceived = amountReceived;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    @JsonProperty(value = "dateTime")
    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
