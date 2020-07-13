package com.github.fernandotaa.restdomainredirect.repository;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "DomainRepository", url = "${destination.url}")
public interface DomainRepository {
    @GetMapping(
            value = "{uri}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    Response get(
            @PathVariable("uri") String uri,
            @RequestHeader Map<String, String> header,
            @RequestBody(required = false) byte[] body
    );

    @PostMapping(
            value = "{uri}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    Response post(
            @PathVariable("uri") String uri,
            @RequestHeader Map<String, String> header,
            @RequestBody(required = false) byte[] body
    );

    @PutMapping(
            value = "{uri}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    Response put(
            @PathVariable("uri") String uri,
            @RequestHeader Map<String, String> header,
            @RequestBody(required = false) byte[] body
    );

    @DeleteMapping(
            value = "{uri}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    Response delete(
            @PathVariable("uri") String uri,
            @RequestHeader Map<String, String> header,
            @RequestBody(required = false) byte[] body
    );

    @PatchMapping(
            value = "{uri}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    Response patch(
            @PathVariable("uri") String uri,
            @RequestHeader Map<String, String> header,
            @RequestBody(required = false) byte[] body
    );
}
