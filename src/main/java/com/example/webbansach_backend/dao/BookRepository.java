package com.example.webbansach_backend.dao;

import com.example.webbansach_backend.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestResource(path = "books")
public interface BookRepository extends JpaRepository<Book, Integer> {
    Page<Book> findByNameBookContaining( String nameBook, Pageable pageable);
    Page<Book> findByListGenres_idGenre( int idGenre, Pageable pageable);
    Page<Book> findByNameBookContainingAndListGenres_idGenre( String nameBook , int idGenre, Pageable pageable);
    long count();
}