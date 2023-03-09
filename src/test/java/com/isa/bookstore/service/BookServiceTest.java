package com.isa.bookstore.service;

import com.isa.bookstore.model.Book;
import com.isa.bookstore.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void givenTitle_whenSaveBook_thenBookIsSavedInRepository() {
        // given
        String title = "1984";

        // when
        Book savedBook = bookService.createBook(title);

        // then
        Optional<Book> findedBook = bookRepository.findById(savedBook.getId());
        assertEquals(title, findedBook.get().getTitle());
    }
}