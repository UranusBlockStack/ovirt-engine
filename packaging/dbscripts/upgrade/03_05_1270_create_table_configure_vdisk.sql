CREATE TABLE configure_vdisk (
    id uuid NOT NULL,
    name character varying(64) NOT NULL,
    ip character varying(48) NOT NULL,
    port integer NOT NULL,
    description character varying(4000)
);
