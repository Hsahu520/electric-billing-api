---- Users table (UUID primary key; email unique)
--create table if not exists users (
--  id uuid primary key,
--  email text not null unique,
--  username text,
--  first_name text,
--  last_name text,
--  phone text,
--  password_hash text not null,
--  created_at timestamptz not null default now()
--);
--
---- Ensure profiles.user_id references users.id (if not already)
--alter table profiles
--  add constraint if not exists profiles_user_fk
--    foreign key (user_id) references users(id) on delete cascade;
--
---- Seed the dev user you’ve already been using so existing profiles still resolve
--insert into users (id, email, password_hash, first_name, last_name)
--values ('00000000-0000-0000-0000-000000000001', 'dev@example.com', 'noop', 'Dev', 'User')
--on conflict (id) do nothing;

-- V2__users_auth.sql
-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS pgcrypto;   -- gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS citext;     -- case-insensitive text

-- Users table
CREATE TABLE IF NOT EXISTS public.users (
  id            uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
  email         citext      NOT NULL UNIQUE,
  password_hash text        NOT NULL,
  created_at    timestamptz NOT NULL DEFAULT now()
);

-- Helpful index
CREATE INDEX IF NOT EXISTS users_created_at_idx ON public.users (created_at);
