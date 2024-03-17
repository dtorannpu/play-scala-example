-- Articles schema
-- !Ups
CREATE TABLE Article (
    id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title varchar(255) NOT NULL,
    body text NOT NULL
);

-- !Downs
DROP TABLE Article;
