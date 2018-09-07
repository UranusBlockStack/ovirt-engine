Create or replace FUNCTION __temp_fn_db_add_schedules_tables() returns void
AS $procedure$
begin
  if (not exists (select 1 from INFORMATION_SCHEMA.TABLES where table_name='schedules')) then
  begin
    CREATE TABLE schedules
    (
      schedule_id uuid NOT NULL,
      schedule_name VARCHAR(256) NOT NULL,
      schedule_type VARCHAR(32) NOT NULL,
      schedule_object text,
      schedule_strategy text,
       _create_date TIMESTAMP WITH TIME ZONE default LOCALTIMESTAMP,
       _update_date TIMESTAMP WITH TIME ZONE default NULL,
      CONSTRAINT PK_schedule_id PRIMARY KEY (schedule_id)
    )
    WITH OIDS;
  end;
  end if;
END; $procedure$
LANGUAGE plpgsql;

select __temp_fn_db_add_schedules_tables();
drop function __temp_fn_db_add_schedules_tables();

Create or replace FUNCTION GetSchedules() RETURNS SETOF schedules
   AS $procedure$
BEGIN
      RETURN QUERY SELECT schedules.*
      FROM schedules;
END; $procedure$
LANGUAGE plpgsql;

Create or replace FUNCTION GetSchedulesByID(v_schedule_id UUID) RETURNS SETOF schedules
   AS $procedure$
BEGIN
      RETURN QUERY SELECT schedules.*
      FROM schedules
      WHERE  schedule_id = v_schedule_id;
END; $procedure$
LANGUAGE plpgsql;


Create or replace FUNCTION DeleteSchedules(v_schedule_id UUID) RETURNS VOID
   AS $procedure$
BEGIN
      DELETE
      FROM   schedules
      WHERE  schedule_id = v_schedule_id;
END; $procedure$
LANGUAGE plpgsql;


Create or replace FUNCTION InsertSchedules(v_schedule_id UUID,v_schedule_name character varying, v_schedule_type character varying, v_schedule_object text, v_schedule_strategy text) RETURNS VOID
   AS $procedure$
BEGIN
      INSERT
      INTO schedules (
            schedule_id,
            schedule_name,
            schedule_type,
            schedule_object,
            schedule_strategy
            )
    VALUES (
            v_schedule_id,
            v_schedule_name,
            v_schedule_type,
            v_schedule_object,
            v_schedule_strategy
            );
END; $procedure$
LANGUAGE plpgsql;
