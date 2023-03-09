package com.isa.bookstore.service;

import com.isa.bookstore.model.Book;
import com.isa.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book createBook(String title) {
        Book book = new Book();
        book.setTitle(title);

        bookRepository.save(book);
        return book;
    }
}
