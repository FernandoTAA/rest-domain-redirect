package com.github.fernandotaa.restdomainredirect.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Repository
public class DomainRepository {
    private final RestTemplate restTemplate;
    private final String url;

    public DomainRepository(
            RestTemplate restTemplate,
            @Value("${destination.url}") String url
    ) {
        this.restTemplate = restTemplate;
        this.url = url;
    }

    public ResponseEntity<byte[]> call(
            String method,
            String uri,
            MultiValueMap<String, String> headers,
            byte[] body
    ) {
        var route = url + uri;
        var httpMethod = HttpMethod.resolve(method);
        var httpEntity = new HttpEntity<byte[]>(body, headers);
        return restTemplate.exchange(route, httpMethod, httpEntity, byte[].class);
    }
}
