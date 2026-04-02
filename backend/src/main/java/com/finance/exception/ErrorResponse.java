package com.finance.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
    private LocalDateTime      timestamp;
    private int                status;
    private String             error;
    private Map<String,String> details;

    public ErrorResponse() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private LocalDateTime      timestamp;
        private int                status;
        private String             error;
        private Map<String,String> details;

        public Builder timestamp(LocalDateTime v)      { this.timestamp = v; return this; }
        public Builder status(int v)                   { this.status    = v; return this; }
        public Builder error(String v)                 { this.error     = v; return this; }
        public Builder details(Map<String,String> v)   { this.details   = v; return this; }

        public ErrorResponse build() {
            ErrorResponse r = new ErrorResponse();
            r.timestamp = this.timestamp;
            r.status    = this.status;
            r.error     = this.error;
            r.details   = this.details;
            return r;
        }
    }

    public LocalDateTime      getTimestamp() { return timestamp; }
    public int                getStatus()    { return status; }
    public String             getError()     { return error; }
    public Map<String,String> getDetails()   { return details; }
}
