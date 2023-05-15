package com.client.controller;

import com.client.service.LibraryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/library")
@RequiredArgsConstructor
public class LibraryController {
    final LibraryClient libraryClient;

    @GetMapping("/author/{authorId}")
    public ResponseEntity<?> getAuthor(@PathVariable String authorId) {
        return ResponseEntity.status(HttpStatus.OK).body(libraryClient.getAuthor(Integer.parseInt(authorId)));
    }

    @GetMapping("/author/{authorId}/books")
    public ResponseEntity<?> getBooksByAuthor(@PathVariable String authorId) throws InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(libraryClient.getBooksByAuthor(Integer.parseInt(authorId)));
    }

    @GetMapping("/book")
    public ResponseEntity<?> getExpensiveBooks() throws InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(libraryClient.getExpensiveBook());
    }

    @GetMapping("/author/books")
    public ResponseEntity<?> getBooksByGender(@RequestParam("gender") String gender) throws InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(libraryClient.getBooksByGender(gender));
    }
}
