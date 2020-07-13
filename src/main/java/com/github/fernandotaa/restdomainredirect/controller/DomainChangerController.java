package com.github.fernandotaa.restdomainredirect.controller;

import com.github.fernandotaa.restdomainredirect.repository.DomainRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
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
    ) throws IOException {
        final Map<String, String> headers = extractHeaders(request);
        final byte[] bodyToPass = Objects.nonNull(body) ? body : new byte[0];
        final String uri = request.getRequestURI();
        var response = switch (request.getMethod()) {
            case "POST" -> domainRepository.post(uri, headers, bodyToPass);
            case "PUT" -> domainRepository.put(uri, headers, bodyToPass);
            case "DELETE" -> domainRepository.delete(uri, headers, bodyToPass);
            case "PATCH" -> domainRepository.patch(uri, headers, bodyToPass);
            default -> domainRepository.get(uri, headers, bodyToPass);
        };

        byte[] responseBody = new byte[0];
        if (Objects.nonNull(response.body())) {
            responseBody = new byte[response.body().length()];
            IOUtils.readFully(response.body().asInputStream(), responseBody);
        }

        return ResponseEntity
                .status(response.status())
                .headers(extractHeaders(response.headers()))
                .body(responseBody);
    }

    private HttpHeaders extractHeaders(Map<String, Collection<String>> headers) {
        return headers.entrySet().stream()
                .filter(entry -> !"host".equalsIgnoreCase(entry.getKey()))
                .filter(entry -> !"location".equalsIgnoreCase(entry.getKey()))
                .collect(
                        HttpHeaders::new,
                        (reponseHeaders, entry) -> reponseHeaders.addAll(entry.getKey(), new ArrayList<>(entry.getValue())),
                        (headers1, headers2) -> headers1.addAll(headers2)
                );
    }

    private Map<String, String> extractHeaders(HttpServletRequest request) {
        var headers = new HashMap<String, String>();
        request.getHeaderNames()
                .asIterator()
                .forEachRemaining(name -> headers.put(name, request.getHeader(name)));
        return headers.entrySet().stream()
                .filter(entry -> !entry.getKey().toLowerCase().contains("x-forwarded-"))
                .filter(entry -> !"host".equalsIgnoreCase(entry.getKey()))
                .filter(entry -> !"x-real-ip".equalsIgnoreCase(entry.getKey()))
                .filter(entry -> !"accept-encoding".equalsIgnoreCase(entry.getKey()))
                .filter(entry -> !"x-scheme".equalsIgnoreCase(entry.getKey()))
                .filter(entry -> !"x-request-id".equalsIgnoreCase(entry.getKey()))
                .filter(entry -> !"accept".equalsIgnoreCase(entry.getKey()))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
    }
}
