package com.example.web

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {

    @GetMapping(produces = [MediaType.TEXT_PLAIN_VALUE]) fun index(): String = "It works!"
}
