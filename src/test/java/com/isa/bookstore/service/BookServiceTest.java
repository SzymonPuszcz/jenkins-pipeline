package com.isa.bookstore.service;

import com.isa.bookstore.model.Book;
import com.isa.bookstore.repository.BookRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookServiceTest {
    private final BookRepository bookRepository = mock(BookRepository.class);
    private final BookService bookService = new BookService(bookRepository);

    @Test
    public void givenBookName_whenCreateBook_thenSaveRepositoryIsCalled() {
        // given
        String title = "Gone with the wind";
        when(bookRepository.save(any())).thenReturn(mock(Book.class));

        //when
        bookService.createBook(title);

        // then
//        verify(bookRepository).save(any());
        verifyNoInteractions(bookRepository);
    }

    @Test
    public void givenBookName_whenCreateBook_thenSaveRepositoryIsCalled2() {
        // given
        String title = "Gone with the wind";
        when(bookRepository.save(any())).thenReturn(mock(Book.class));

        //when
        bookService.createBook(title);

        // then
        verify(bookRepository).save(any());
//        verifyNoInteractions(bookRepository);
    }

}