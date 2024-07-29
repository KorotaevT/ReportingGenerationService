CREATE TABLE IF NOT EXISTS public.authority
(
    id bigint NOT NULL DEFAULT nextval('authority_id_seq'::regclass),
    role character varying(255) COLLATE pg_catalog."default",
    user_id bigint NOT NULL,
    CONSTRAINT authority_pkey PRIMARY KEY (id),
    CONSTRAINT fkka37hl6mopj61rfbe97si18p8 FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT authority_role_check CHECK (role::text = ANY (ARRAY['GUEST'::character varying, 'USER'::character varying, 'ADMIN'::character varying]::text[]))
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.authority
    OWNER to postgres;
