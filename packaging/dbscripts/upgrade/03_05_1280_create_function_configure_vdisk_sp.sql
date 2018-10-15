Create or replace FUNCTION InsertConfigureVdisk(v_description VARCHAR(4000),
        v_id UUID,
        v_name VARCHAR(64),
        v_ip VARCHAR(48),
        v_port INTEGER)
RETURNS VOID
   AS $procedure$
BEGIN
INSERT INTO configure_vdisk(description, id, name, ip, port)
        VALUES(v_description, v_id, v_name, v_ip, v_port);
END; $procedure$
LANGUAGE plpgsql;


Create or replace FUNCTION UpdateConfigureVdisk(v_description VARCHAR(4000),
       v_id UUID,
       v_name VARCHAR(64),
       v_ip VARCHAR(48),
       v_port INTEGER)
RETURNS VOID
   AS $procedure$
BEGIN
       UPDATE configure_vdisk
       SET description   = v_description,
           name          = v_name,
           ip            = v_ip,
           port          = v_port
       WHERE id = v_id;
END; $procedure$
LANGUAGE plpgsql;

Create or replace FUNCTION DeleteConfigureVdisk(v_id UUID)
RETURNS VOID
   AS $procedure$
   DECLARE
   v_val UUID;
BEGIN
   select id INTO v_val FROM configure_vdisk WHERE id = v_id FOR UPDATE;

   DELETE FROM configure_vdisk
   WHERE id = v_id;
END; $procedure$
LANGUAGE plpgsql;


Create or replace FUNCTION GetAllFromConfigureVdisk() RETURNS SETOF configure_vdisk STABLE
   AS $procedure$
BEGIN
   RETURN QUERY SELECT *
   FROM configure_vdisk;
END; $procedure$
LANGUAGE plpgsql;


Create or replace FUNCTION GetConfigureVdiskByConfigureVdiskId(v_id UUID)
RETURNS SETOF configure_vdisk STABLE
   AS $procedure$
BEGIN
   RETURN QUERY SELECT *
   FROM configure_vdisk
   WHERE id = v_id;
END; $procedure$
LANGUAGE plpgsql;
