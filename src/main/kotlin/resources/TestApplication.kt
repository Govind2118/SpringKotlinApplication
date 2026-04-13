package com.example

import com.example.config.AppProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class)
class TestApplication

fun main(args: Array<String>) {
    runApplication<TestApplication>(*args)
}
