package com.github.lukaszbudnik.orderpreserver;

import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class OrderPreserverTest {

    @Test
    public void simpleTest() throws Exception {
        List<Book> books = new ArrayList<>();
        books.add(new Book("first"));
        books.add(new Book("second"));
        books.add(new Book("third"));

        Object[] original = books.toArray();

        OrderSnapshot orderSnapshot = OrderPreserver.createOrderSnapshot(books, new Function<Book, String>() {
            @Override
            public String apply(Book book) {
                return book.getIsbn();
            }
        });

        assertThat(orderSnapshot.getSnapshot(), equalTo(Arrays.asList("first", "second", "third")));

        // do some parallel processing which destroys the original order
        List<Book> processed = books.parallelStream().unordered().map((b) -> {
            long sleep = 0;
            switch (b.getIsbn()) {
                case "first": sleep += 500; break;
                case "second": sleep += 1000; break;
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return b;
        }).map((b) -> {
            System.out.println(b);
            return b;
        }).collect(Collectors.toList());


        assertThat(processed.toArray(), not(equalTo(original)));

        List<Book> restored = OrderPreserver.restoreOrder(books, orderSnapshot);

        assertThat(restored.toArray(), equalTo(original));
    }

    @Test
    public void randomTest() throws Exception {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            books.add(new Book(UUID.randomUUID().toString()));
        }

        Object[] original = books.toArray();

        OrderSnapshot orderSnapshot = OrderPreserver.createOrderSnapshot(books, new Function<Book, String>() {
            @Override
            public String apply(Book book) {
                return book.getIsbn();
            }
        });

        // pretend to do some parallel processing which destroys the original order
        Collections.shuffle(books);
        assertThat(books.toArray(), not(equalTo(original)));

        List<Book> restored = OrderPreserver.restoreOrder(books, orderSnapshot);

        assertThat(restored.toArray(), equalTo(original));

    }

    private class Book {

        private String isbn;

        private Book(String isbn) {
            this.isbn = isbn;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        @Override
        public String toString() {
            return "Book{" +
                    "isbn='" + isbn + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Book book = (Book) o;

            if (!isbn.equals(book.isbn)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return isbn.hashCode();
        }
    }

} 
