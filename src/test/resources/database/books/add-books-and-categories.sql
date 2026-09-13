INSERT INTO categories (id, name, description, is_deleted)
VALUES (100, 'Fiction', 'Fiction books', false);
INSERT INTO categories (id, name, description, is_deleted)
VALUES (101, 'Science', 'Science books', false);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (100, 'Dune', 'Frank Herbert', '9780441013593', 19.99,
        'Sci-fi classic', 'dune.jpg', false);
INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (101, 'Cosmos', 'Carl Sagan', '9780345331359', 24.99,
        'Popular science', 'cosmos.jpg', false);
INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (102, 'Deleted Book', 'Nobody', '9780000000001', 1.99,
        'Soft deleted', 'none.jpg', true);

INSERT INTO books_categories (book_id, category_id) VALUES (100, 100);
INSERT INTO books_categories (book_id, category_id) VALUES (101, 101);
