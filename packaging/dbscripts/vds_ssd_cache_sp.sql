----------------------------------------------------------------
-- [vds_ssd_cache] Table
--

Create or replace FUNCTION GetVdsSsdCacheByVdsId(v_vds_id UUID) RETURNS SETOF vds_ssd_cache STABLE
   AS $procedure$
BEGIN
RETURN QUERY SELECT *
   FROM vds_ssd_cache
   WHERE vds_id = v_vds_id;
END; $procedure$
LANGUAGE plpgsql;


Create or replace FUNCTION GetVdsSsdCache(v_vds_id UUID, v_ssd_name VARCHAR(40)) RETURNS SETOF vds_ssd_cache STABLE
   AS $procedure$
BEGIN
RETURN QUERY SELECT *
   FROM vds_ssd_cache
   WHERE vds_id = v_vds_id and ssd_name = v_ssd_name;
END; $procedure$
LANGUAGE plpgsql;


Create or replace FUNCTION DeleteVdsSsdCache(v_vds_id UUID, v_ssd_name VARCHAR(40))
RETURNS VOID
   AS $procedure$
   DECLARE
   v_val  UUID;
BEGIN
      DELETE FROM vds_ssd_cache
      WHERE vds_id = v_vds_id
      AND ssd_name = v_ssd_name;
END; $procedure$
LANGUAGE plpgsql;


Create or replace FUNCTION DeleteVdsSsdCacheByVdsId(v_vds_id UUID)
RETURNS VOID
   AS $procedure$
   DECLARE
   v_val  UUID;
BEGIN
      DELETE FROM vds_ssd_cache
      WHERE vds_id = v_vds_id;
END; $procedure$
LANGUAGE plpgsql;

Create or replace FUNCTION DeleteAllVdsSsdCache()
RETURNS VOID
   AS $procedure$
   DECLARE
   v_val  UUID;
BEGIN
      DELETE FROM vds_ssd_cache;
END; $procedure$
LANGUAGE plpgsql;


Create or replace FUNCTION InsertVdsSsdCache(
   v_vds_id UUID,
   v_ssd_name VARCHAR(40),
   v_cache_status BOOLEAN,
   v_ssd_size int,
   v_used_size int,
   v_multi_path VARCHAR(255)
)
RETURNS VOID

   AS $procedure$
BEGIN
INSERT
INTO vds_ssd_cache (
   vds_id,
   ssd_name,
   cache_status,
   ssd_size,
   used_size,
   multi_path
)VALUES(
   v_vds_id,
   v_ssd_name,
   v_cache_status,
   v_ssd_size,
   v_used_size,
   v_multi_path
);
END; $procedure$
LANGUAGE plpgsql;

Create or replace FUNCTION UpdateVdsSsdCache(
        v_vds_id UUID,
        v_ssd_name VARCHAR(40),
        v_cache_status BOOLEAN
        )
RETURNS VOID

   AS $procedure$
BEGIN
      UPDATE vds_ssd_cache
      SET    cache_status = v_cache_status
      WHERE  vds_id = v_vds_id
      AND    ssd_name = v_ssd_name;
END; $procedure$
LANGUAGE plpgsql;
