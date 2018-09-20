-- ----------------------------------------------------------------------
--  tab vds_ssd_cache
-- ----------------------------------------------------------------------

CREATE TABLE vds_ssd_cache
(
    vds_id UUID,
    ssd_name VARCHAR(40),
    cache_status BOOLEAN,
    ssd_size int,
    used_size int,
    multi_path VARCHAR(255)
) WITH OIDS;
