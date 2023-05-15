package com.service.grpc;

import com.springGrpc.Author;
import com.springGrpc.Book;
import com.springGrpc.BookAuthorServiceGrpc;
import com.springgrpc.TempDB;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class LibraryService extends BookAuthorServiceGrpc.BookAuthorServiceImplBase {
    @Override
    public void getAuthor(Author request, StreamObserver<Author> responseObserver) {
        TempDB.authors.stream().findFirst().ifPresent(responseObserver::onNext);
    }

    @Override
    public void getBooksByAuthor(Author request, StreamObserver<Book> responseObserver) {
        TempDB.books.stream().filter(
                book -> book.getAuthorId() == request.getAuthorId()).forEach(responseObserver::onNext);
    }

    @Override
    public StreamObserver<Book> getExpensiveBook(StreamObserver<Book> responseObserver) {
        return new StreamObserver<>() {
            Book expensiveBook = null;
            float priceTrack = 0;

            @Override
            public void onNext(Book book) {
                if (book.getPrice() > priceTrack) {
                    priceTrack = book.getPrice();
                    expensiveBook = book;
                }
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(expensiveBook);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<Book> getBooksByGender(StreamObserver<Book> responseObserver) {
        return new StreamObserver<>() {
            final List<Book> books = new ArrayList<>();

            @Override
            public void onNext(Book book) {
                TempDB.authors.stream().filter(
                        author -> author.getAuthorId() == book.getAuthorId()).forEach(
                        author -> books.add(book.toBuilder().setAuthorId(author.getAuthorId()).build()));
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                books.forEach(responseObserver::onNext);
                responseObserver.onCompleted();
            }
        };
    }
}
