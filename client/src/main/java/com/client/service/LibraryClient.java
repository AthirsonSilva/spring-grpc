package com.client.service;

import com.google.protobuf.Descriptors;
import com.springGrpc.Author;
import com.springGrpc.Book;
import com.springGrpc.BookAuthorServiceGrpc;
import com.springgrpc.TempDB;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class LibraryClient {
    @GrpcClient("grpc-service")
    private BookAuthorServiceGrpc.BookAuthorServiceBlockingStub synchronousClient;

    @GrpcClient("grpc-service")
    private BookAuthorServiceGrpc.BookAuthorServiceStub asynchronousClient;

    public Map<Descriptors.FieldDescriptor, Object> getAuthor(int authorId) {
        // Create a request object with the given authorId
        Author authorRequest = Author.newBuilder().setAuthorId(authorId).build();

        // Send the request to the server and get the response
        Author authorResponse = synchronousClient.getAuthor(authorRequest);

        return authorResponse.getAllFields();
    }

    public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByAuthor(int authorId) throws InterruptedException {
        // Create a request object with the given authorId
        final Author authorRequest = Author.newBuilder().setAuthorId(authorId).build();

        // Instantiate a CountDownLatch object with a count of 1 to wait for the response
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        // Create a list to store the response
        final List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();

        // Send the request to the server and get the response
        asynchronousClient.getBooksByAuthor(authorRequest, new StreamObserver<>() {
            @Override
            public void onNext(Book book) {
                response.add(book.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });

        // Wait for the response for 1 minute
        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);

        // Return the response if the response is received within 1 minute, else return an empty list
        return await ? response : Collections.emptyList();
    }

    public Map<String, Map<Descriptors.FieldDescriptor, Object>> getExpensiveBook() throws InterruptedException {
        // Instantiate a CountDownLatch object with a count of 1 to wait for the response
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        // Create a map to store the response
        final Map<String, Map<Descriptors.FieldDescriptor, Object>> response = new HashMap<>(Collections.singletonMap("expensiveBook", null));

        // Send the request to the server and get the response
        StreamObserver<Book> expensiveBookObserver = asynchronousClient.getExpensiveBook(new StreamObserver<>() {
            @Override
            public void onNext(Book book) {
                response.put("expensiveBook", book.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });

        // Send all the books to the server
        TempDB.books.forEach(expensiveBookObserver::onNext);

        // Tell the server that all the books have been sent
        expensiveBookObserver.onCompleted();

        // Wait for the response for 1 minute
        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);

        // Return the response if the response is received within 1 minute, else return an empty map
        return await ? response : Collections.emptyMap();
    }

    public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByGender(String gender) throws InterruptedException {
        // Instantiate a CountDownLatch object with a count of 1 to wait for the response
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        // Create a list to store the response
        final List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();

        StreamObserver<Book> responseObserver = asynchronousClient.getBooksByGender(new StreamObserver<>() {
            @Override
            public void onNext(Book book) {
                response.add(book.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });

        // Send all the authors to the server
        TempDB.authors.stream().filter(
                author -> author.getGender().equalsIgnoreCase(gender)).forEach(
                author -> TempDB.books.stream().filter(
                        book -> book.getAuthorId() == author.getAuthorId()).forEach(
                        responseObserver::onNext));

        // Tell the server that all the authors have been sent
        responseObserver.onCompleted();

        // Wait for the response for 1 minute
        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);

        // Return the response if the response is received within 1 minute, else return an empty list
        return await ? response : Collections.emptyList();
    }
}
