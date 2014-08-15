BEGIN;

DROP SCHEMA IF EXISTS dxcrs CASCADE;

CREATE SCHEMA dxcrs;

ALTER USER dxcrs SET search_path = dxcrs;

END;