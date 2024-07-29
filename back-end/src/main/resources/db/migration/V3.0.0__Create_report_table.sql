CREATE TABLE IF NOT EXISTS public.report
(
    id bigint NOT NULL DEFAULT nextval('report_id_seq'::regclass),
    fields character varying(1000) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    query character varying(4000) COLLATE pg_catalog."default",
    url character varying(255) COLLATE pg_catalog."default",
    username character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT report_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.report
    OWNER to postgres;
