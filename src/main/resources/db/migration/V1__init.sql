-- V1__init.sql
-- Minimal starting schema so Flyway + JPA work

-- Users table
create table if not exists users (
  id            uuid primary key,
  email         text unique not null,
  username      text unique not null,
  password_hash text not null,
  created_at    timestamptz default now()
);

-- Profiles table (each user can have many profiles; name unique per user)
create table if not exists profiles (
  id         uuid primary key,
  user_id    uuid not null references users(id) on delete cascade,
  name       text not null,
  created_at timestamptz default now(),
  unique (user_id, name)
);

-- Seed a dummy user so we can test endpoints before auth is built
insert into users (id, email, username, password_hash)
values ('00000000-0000-0000-0000-000000000001', 'seed@example.com', 'seed', 'dev-seed')
on conflict (id) do nothing;
