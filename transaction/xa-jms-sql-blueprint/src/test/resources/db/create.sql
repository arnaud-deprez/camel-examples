CREATE TABLE message (
  id BIGINT auto_increment primary key,
  createddate TIMESTAMP,
  lastmodifieddate TIMESTAMP,
  content varchar(255)
);