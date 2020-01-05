package com.fundwit.sys.shikra;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class VerifierContract {
    @Rule
    public PactProviderRule mockProvider =
            new PactProviderRule("shikra", "localhost", 8081, this);

    @Pact(provider = "shikra", consumer = "shikra-ui")
    public RequestResponsePact createFragment(PactDslWithProvider builder) {
        return builder.uponReceiving("verify")
                .path("/verifier/email")
                .method("POST")
                .body(new PactDslJsonBody().stringType("email"))
                .willRespondWith()
                .status(HttpStatus.NO_CONTENT.value())
                .toPact();
    }

    @Test
    @PactVerification
    public void runTest() {
        String url = mockProvider.getUrl();
        URI productInfoUri = URI.create(String.format("%s/%s", url, "product?id=537"));

    }
}
