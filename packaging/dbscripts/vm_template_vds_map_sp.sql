----------------------------------------------------------------
-- [vm_template_vds_map] Table
--

Create or replace FUNCTION GetVmTemplateVdsMapByVdsId(v_vds_id UUID) RETURNS SETOF vm_template_vds_map STABLE
   AS $procedure$
BEGIN
RETURN QUERY SELECT *
   FROM vm_template_vds_map
   WHERE vds_id = v_vds_id;
END; $procedure$
LANGUAGE plpgsql;

Create or replace FUNCTION GetVmTemplateVdsMapByVdsGroupId(v_vds_group_id UUID) RETURNS SETOF vm_template_vds_map STABLE
   AS $procedure$
BEGIN
RETURN QUERY SELECT vm_template_vds_map.*
   FROM vm_template_vds_map
   RIGHT JOIN vds_static
   ON vds_static.vds_group_id = v_vds_group_id and vm_template_vds_map.vds_id = vds_static.vds_id and vds_static.flash_cache = true;
END; $procedure$
LANGUAGE plpgsql;

Create or replace FUNCTION GetVmTemplateVdsMapByVmTemplateId(v_vm_template_id UUID) RETURNS SETOF vm_template_vds_map STABLE
   AS $procedure$
BEGIN
RETURN QUERY SELECT *
   FROM vm_template_vds_map
   WHERE vm_template_id = v_vm_template_id;
END; $procedure$
LANGUAGE plpgsql;

Create or replace FUNCTION GetVmTemplateVdsMap(v_vm_template_id UUID, v_vds_id UUID) RETURNS SETOF vm_template_vds_map STABLE
   AS $procedure$
BEGIN
RETURN QUERY SELECT *
   FROM vm_template_vds_map
   WHERE vm_template_id = v_vm_template_id and vds_id = v_vds_id;
END; $procedure$
LANGUAGE plpgsql;


Create or replace FUNCTION InsertVmTemplateVdsMap(
   v_vds_id UUID,
   v_vm_template_id UUID,
   v_operation_status boolean
)
RETURNS VOID

   AS $procedure$
BEGIN
INSERT
INTO vm_template_vds_map(
   vds_id,
   vm_template_id,
   operation_status
)VALUES(
    v_vds_id,
    v_vm_template_id,
    v_operation_status
);
END; $procedure$
LANGUAGE plpgsql;

Create or replace FUNCTION DeleteVmTemplateVdsMap(v_vds_id UUID, v_vm_template_id UUID)
RETURNS VOID
   AS $procedure$
   DECLARE
   v_val  UUID;
BEGIN
      DELETE FROM vm_template_vds_map
      WHERE vds_id = v_vds_id
      AND vm_template_id = v_vm_template_id;
END; $procedure$
LANGUAGE plpgsql;
