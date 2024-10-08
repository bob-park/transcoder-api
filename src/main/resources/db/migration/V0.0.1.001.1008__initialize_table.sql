/**
  create table
 */
create table jobs
(
    id                 bigserial                     not null primary key,
    type               varchar(50)                   not null,
    status             varchar(50) default 'WAITING' not null,
    source             varchar(1000)                 not null,
    dest               varchar(1000)                 not null,
    options            json,
    start_datetime     timestamp,
    end_datetime       timestamp,
    created_date       timestamp   default now()     not null,
    last_modified_date timestamp
);