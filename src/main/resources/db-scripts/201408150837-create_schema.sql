BEGIN;

DROP SCHEMA IF EXISTS dxintranet CASCADE;

CREATE SCHEMA dxintranet;

ALTER USER dxintranet SET search_path = dxintranet;

END;