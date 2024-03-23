-- Comment schema
-- !Ups
CREATE TABLE comment
(
    id         bigint       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    article_id bigint       NOT NULL,
    commenter  varchar(255) NOT NULL,
    body       text         NOT NULL,
    CONSTRAINT article_fk FOREIGN KEY (article_id) REFERENCES article (id)
);

-- !Downs
DROP TABLE comment;
