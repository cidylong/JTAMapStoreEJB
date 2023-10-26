--
-- PostgreSQL database dump
--

-- Dumped from database version 15.3
-- Dumped by pg_dump version 15.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: event; Type: TABLE; Schema: public; Owner: tomcat
--

CREATE TABLE public.event (
    event_id text NOT NULL,
    event_feeder_model text NOT NULL,
    event_title text NOT NULL,
    event_info text NOT NULL,
    event_triger text,
    event_created timestamp without time zone,
    event_app text NOT NULL
);


ALTER TABLE public.event OWNER TO tomcat;

--
-- Name: event event_id; Type: CONSTRAINT; Schema: public; Owner: tomcat
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_id PRIMARY KEY (event_id);


--
-- PostgreSQL database dump complete
--

