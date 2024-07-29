CREATE TABLE IF NOT EXISTS public.report_request
(
    id bigint NOT NULL DEFAULT nextval('report_request_id_seq'::regclass),
    request_time timestamp(6) without time zone,
    report_id bigint,
    user_id bigint,
    CONSTRAINT report_request_pkey PRIMARY KEY (id),
    CONSTRAINT fk344v9talw7j4fwndkhyivdfd9 FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fknq4w0hyhb7twsk2ah5ta8iphw FOREIGN KEY (report_id)
        REFERENCES public.report (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.report_request
    OWNER to postgres;
