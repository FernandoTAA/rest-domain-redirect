package com.github.fernandotaa.restdomainredirect.controller;

import com.github.fernandotaa.restdomainredirect.repository.DomainRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class DomainChangerController {

    private static Logger LOGGER = LoggerFactory.getLogger(DomainChangerController.class);

    private final DomainRepository domainRepository;

    public DomainChangerController(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    @RequestMapping(value = "/**", method = {
            RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.DELETE,
            RequestMethod.PATCH
    })
    public ResponseEntity<byte[]> change(
            @RequestBody(required = false) byte[] body,
            HttpServletRequest request
    ) {
        var headers = extractHeaders(request);
        var bodyToPass = Objects.nonNull(body) ? body : new byte[0];
        var uri = request.getRequestURI();
        var method = request.getMethod();
        var response = domainRepository.call(method, uri, headers, bodyToPass);

        byte[] responseBody = Objects.nonNull(response.getBody()) ? response.getBody() : new byte[0];

        return ResponseEntity
                .status(response.getStatusCodeValue())
                .headers(removeHostKeys(response.getHeaders()))
                .body(responseBody);
    }

    private HttpHeaders removeHostKeys(HttpHeaders headers) {
        var keysToKeep = headers.keySet().stream()
                .filter(key -> !"host".equalsIgnoreCase(key))
                .filter(key -> !"location".equalsIgnoreCase(key))
                .collect(Collectors.toList());
        keysToKeep.stream()
                .filter(key -> !keysToKeep.contains(key))
                .forEach(headers::remove);
        return headers;
    }

    private MultiValueMap<String, String> extractHeaders(HttpServletRequest request) {
        var headers = new LinkedMultiValueMap<String, String>();
        request.getHeaderNames()
                .asIterator()
                .forEachRemaining(key -> headers.put(key, List.of(request.getHeader(key))));
        var keysToKeep = headers.entrySet().stream()
                .filter(entry -> !entry.getKey().toLowerCase().contains("x-forwarded-"))
                .filter(entry -> !"host".equalsIgnoreCase(entry.getKey()))
                .filter(entry -> !"x-real-ip".equalsIgnoreCase(entry.getKey()))
                .filter(entry -> !"accept-encoding".equalsIgnoreCase(entry.getKey()))
                .filter(entry -> !"x-scheme".equalsIgnoreCase(entry.getKey()))
                .filter(entry -> !"x-request-id".equalsIgnoreCase(entry.getKey()))
                .filter(entry -> !"accept".equalsIgnoreCase(entry.getKey()))
                .collect(Collectors.toList());
        keysToKeep.stream()
                .filter(key -> !keysToKeep.contains(key))
                .forEach(headers::remove);
        return headers;
    }
}
