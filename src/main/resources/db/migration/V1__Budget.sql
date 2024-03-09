create table budget
(
    id     serial primary key,
    year   int  not null,
    month  int  not null,
    amount int  not null,
    type   text not null,
    author_id int
);

create table author
(
    id SERIAL PRIMARY KEY,
    fio varchar(100),
    "createdAt" timestamp
);