package com.checkmarx.sdk.dto.cxgo;
import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "severityId",
        "amount"
})
public class OdScanQuerySeverity {
    @JsonProperty("severityId")
    private String severityId;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("severityId")
    public String getSeverityId() {
        return severityId;
    }

    @JsonProperty("severityId")
    public void setSeverityId(String severityId) {
        this.severityId = severityId;
    }

    @JsonProperty("amount")
    public Integer getAmount() {
        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}