--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.5
-- Dumped by pg_dump version 9.4.4
-- Started on 2016-01-20 11:58:59

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 186 (class 3079 OID 11893)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2154 (class 0 OID 0)
-- Dependencies: 186
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 199 (class 1255 OID 16604)
-- Name: f_actualizar_posicion(integer, integer, numeric, numeric); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_actualizar_posicion(integer, integer, numeric, numeric) RETURNS integer
    LANGUAGE plpgsql
    AS $_$
DECLARE

BEGIN
INSERT INTO hist_ubicacion (recursoid,estado,ubicacionlat,ubicacionlng)
VALUES ($1,$2,$3,$4);
RETURN 1;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;
END
$_$;


ALTER FUNCTION public.f_actualizar_posicion(integer, integer, numeric, numeric) OWNER TO postgres;

--
-- TOC entry 200 (class 1255 OID 16605)
-- Name: f_check_recurso(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_check_recurso(integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$
    DECLARE
count integer;
BEGIN
count:=-1;

PERFORM *
FROM recursos
WHERE recursoid = $1;

GET DIAGNOSTICS count = ROW_COUNT;
return count;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;

END
$_$;


ALTER FUNCTION public.f_check_recurso(integer) OWNER TO postgres;

--
-- TOC entry 201 (class 1255 OID 16606)
-- Name: f_check_user(character varying, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_check_user(v_user character varying, pass text) RETURNS TABLE(usuarioid integer, privilegios integer, nombre character varying, apellido character varying, direccion character varying, telefono character varying, dni character varying, notas text, username character varying, password text)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT *
FROM USUARIOS U
WHERE U.USERNAME = v_user AND U.PASSWORD = pass;
END
$$;


ALTER FUNCTION public.f_check_user(v_user character varying, pass text) OWNER TO postgres;

--
-- TOC entry 202 (class 1255 OID 16607)
-- Name: f_check_user_email(character varying, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_check_user_email(v_email character varying, pass text) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
i_email VARCHAR;
BEGIN
IF (hashed == 1) THEN
SELECT USERNAME INTO i_email
FROM USUARIOS
WHERE USERNAME = v_email AND PASSWORD = pass;
ELSE
SELECT USERNAME INTO i_email
FROM USUARIOS
WHERE USERNAME = v_email AND PASSWORD = crypt(pass, PASSWORD);
END IF;
IF (i_user == NULL) THEN
RETURN 1;
ELSE
RETURN 0;
END IF;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;
END
$$;


ALTER FUNCTION public.f_check_user_email(v_email character varying, pass text) OWNER TO postgres;

--
-- TOC entry 203 (class 1255 OID 16608)
-- Name: f_check_user_username(character varying, text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_check_user_username(v_user character varying, pass text, hashed integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
i_user VARCHAR;
BEGIN
IF (hashed == 1) THEN
SELECT USERNAME INTO i_user
FROM USUARIOS
WHERE USERNAME = v_user AND PASSWORD = pass;
ELSE
SELECT USERNAME INTO i_user
FROM USUARIOS
WHERE USERNAME = v_user AND PASSWORD = crypt(pass, PASSWORD);
END IF;
IF (i_user == NULL) THEN
RETURN 1;
ELSE
RETURN 0;
END IF;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;
END
$$;


ALTER FUNCTION public.f_check_user_username(v_user character varying, pass text, hashed integer) OWNER TO postgres;

--
-- TOC entry 204 (class 1255 OID 16609)
-- Name: f_delete_user_username(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_delete_user_username(v_user character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
id INTEGER := -1;
BEGIN
DELETE FROM USUARIOS WHERE USERNAME = v_user
RETURNING USUARIOID INTO id;
RETURN result;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;
END
$$;


ALTER FUNCTION public.f_delete_user_username(v_user character varying) OWNER TO postgres;

--
-- TOC entry 205 (class 1255 OID 16610)
-- Name: f_edit_user(character varying, character varying, character varying, character varying, character varying, character varying, character varying, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_edit_user(name character varying, ape character varying, dir character varying, tlf character varying, obs character varying, v_dni character varying, v_user character varying, pass text) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
cod INTEGER := -1;
BEGIN
UPDATE USUARIOS
SET NOMBRE = name, APELLIDO = ape, DIRECCION = dir, TELEFONO = tlf, NOTAS = obs, DNI = v_dni, PASSWORD = pass
WHERE USERNAME = v_user
RETURNING USUARIOID INTO cod;
RETURN cod;
END
$$;


ALTER FUNCTION public.f_edit_user(name character varying, ape character varying, dir character varying, tlf character varying, obs character varying, v_dni character varying, v_user character varying, pass text) OWNER TO postgres;

--
-- TOC entry 206 (class 1255 OID 16611)
-- Name: f_fin_incidencia(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_fin_incidencia(integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$
DECLARE
count integer;
BEGIN
UPDATE hist_incidencias SET
fecharesolucion = now(),
resolucion = -1
WHERE incidenciaid = $1;
GET DIAGNOSTICS count = ROW_COUNT;
return count;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;
END
$_$;


ALTER FUNCTION public.f_fin_incidencia(integer) OWNER TO postgres;

--
-- TOC entry 207 (class 1255 OID 16612)
-- Name: f_get_estacion(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_estacion(integer) RETURNS TABLE(estacionid integer, tiporecursoid integer, nombreestacion character varying, ubicacionestacionlat numeric, ubicacionestacionlng numeric)
    LANGUAGE plpgsql
    AS $_$
BEGIN
RETURN QUERY
SELECT *
FROM ESTACIONES
WHERE ESTACIONES.ESTACIONID=$1;
END
$_$;


ALTER FUNCTION public.f_get_estacion(integer) OWNER TO postgres;

--
-- TOC entry 208 (class 1255 OID 16613)
-- Name: f_get_estaciones(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_estaciones() RETURNS TABLE(estacionid integer, tiporecursoid integer, nombreestacion character varying, ubicacionestacionlat numeric, ubicacionestacionlng numeric)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT *
FROM ESTACIONES;
END
$$;


ALTER FUNCTION public.f_get_estaciones() OWNER TO postgres;

--
-- TOC entry 209 (class 1255 OID 16614)
-- Name: f_get_estaciones(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_estaciones(integer) RETURNS TABLE(estacionid integer, tiporecursoid integer, nombreestacion character varying, ubicacionestacionlat numeric, ubicacionestacionlng numeric)
    LANGUAGE plpgsql
    AS $_$
BEGIN
RETURN QUERY
SELECT *
FROM ESTACIONES
WHERE ESTACIONES.tiporecursoid=$1;
END
$_$;


ALTER FUNCTION public.f_get_estaciones(integer) OWNER TO postgres;

--
-- TOC entry 210 (class 1255 OID 16615)
-- Name: f_get_incidencias_abiertas(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_incidencias_abiertas() RETURNS TABLE(incidenciaid integer, tipoincidenciaid integer, fechanotificacion timestamp without time zone, fecharesolucion timestamp without time zone, ubicacionlat numeric, ubicacionlng numeric, usuarioid integer, telefono character varying, notas text, gravedad integer, numeroafectados integer, resolucion integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT *
FROM hist_incidencias
WHERE hist_incidencias.fecharesolucion IS NULL
ORDER BY hist_incidencias.gravedad DESC, hist_incidencias.fechanotificacion;
END
$$;


ALTER FUNCTION public.f_get_incidencias_abiertas() OWNER TO postgres;

--
-- TOC entry 211 (class 1255 OID 16616)
-- Name: f_get_incidencias_abiertas_usuario(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_incidencias_abiertas_usuario(integer) RETURNS TABLE(incidenciaid integer, tipoincidenciaid integer, fechanotificacion timestamp without time zone, fecharesolucion timestamp without time zone, ubicacionlat numeric, ubicacionlng numeric, usuarioid integer, telefono character varying, notas text, gravedad integer, numeroafectados integer, resolucion integer)
    LANGUAGE plpgsql
    AS $_$
BEGIN
RETURN QUERY
SELECT *
FROM hist_incidencias
WHERE hist_incidencias.fecharesolucion IS NULL AND $1>0 AND (hist_incidencias.usuarioid = $1 OR (SELECT privilegios FROM usuarios WHERE usuarios.usuarioid=$1) = 1)
ORDER BY hist_incidencias.fechanotificacion;
END
$_$;


ALTER FUNCTION public.f_get_incidencias_abiertas_usuario(integer) OWNER TO postgres;

--
-- TOC entry 212 (class 1255 OID 16617)
-- Name: f_get_location_resource(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_location_resource(id integer) RETURNS TABLE(estado integer, lat numeric, lng numeric)
    LANGUAGE plpgsql
    AS $$
DECLARE

BEGIN
RETURN QUERY
SELECT H1.ESTADO, H1.UBICACIONLAT, H1.UBICACIONLNG
FROM HIST_UBICACION H1 LEFT JOIN HIST_UBICACION H2
ON (H1.RECURSOID = H2.RECURSOID AND H1.FECHA < H2.FECHA)
WHERE H1.RECURSOID = id AND H2.FECHA IS NULL;
END
$$;


ALTER FUNCTION public.f_get_location_resource(id integer) OWNER TO postgres;

--
-- TOC entry 213 (class 1255 OID 16618)
-- Name: f_get_location_station(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_location_station(id integer) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE
x RECORD;
BEGIN
SELECT E.UBICACIONLAT AS LAT, E.UBICACIONLNG AS LNG INTO x
FROM ESTACIONES E
WHERE E.ESTACIONID = id;
RETURN x;
EXCEPTION
WHEN OTHERS THEN
RETURN NULL;
END
$$;


ALTER FUNCTION public.f_get_location_station(id integer) OWNER TO postgres;

--
-- TOC entry 214 (class 1255 OID 16619)
-- Name: f_get_name_station(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_name_station(id integer) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE
name VARCHAR := 'N/A';
BEGIN
SELECT E.NOMBREESTACION AS name
FROM ESTACIONES E
WHERE E.ESTACIONID = id;
RETURN name;
EXCEPTION
WHEN OTHERS THEN
RETURN NULL;
END
$$;


ALTER FUNCTION public.f_get_name_station(id integer) OWNER TO postgres;

--
-- TOC entry 215 (class 1255 OID 16620)
-- Name: f_get_nearest_resource(numeric, numeric, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_nearest_resource(lat numeric, lng numeric, type integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE

BEGIN

END
$$;


ALTER FUNCTION public.f_get_nearest_resource(lat numeric, lng numeric, type integer) OWNER TO postgres;

--
-- TOC entry 216 (class 1255 OID 16621)
-- Name: f_get_recurso(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_recurso(integer) RETURNS TABLE(recursoid integer, estacionid integer, nombrerecurso character varying)
    LANGUAGE plpgsql
    AS $_$
BEGIN
RETURN QUERY
SELECT *
FROM recursos
WHERE recursos.recursoid = $1;
END
$_$;


ALTER FUNCTION public.f_get_recurso(integer) OWNER TO postgres;

--
-- TOC entry 217 (class 1255 OID 16622)
-- Name: f_get_recursos(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_recursos() RETURNS TABLE(recursoid integer, estacionid integer, nombrerecurso character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT *
FROM recursos;
END
$$;


ALTER FUNCTION public.f_get_recursos() OWNER TO postgres;

--
-- TOC entry 218 (class 1255 OID 16623)
-- Name: f_get_recursos(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_recursos(integer) RETURNS TABLE(recursoid integer, estacionid integer, nombrerecurso character varying)
    LANGUAGE plpgsql
    AS $_$
BEGIN
RETURN QUERY
SELECT recursos.*
FROM recursos INNER JOIN estaciones ON recursos.estacionid = estaciones.estacionid
WHERE estaciones.tiporecursoid = $1;
END
$_$;


ALTER FUNCTION public.f_get_recursos(integer) OWNER TO postgres;

--
-- TOC entry 219 (class 1255 OID 16624)
-- Name: f_get_recursos_working(); Type: FUNCTION; Schema: public; Owner: postgres
--
CREATE FUNCTION f_get_recursos_working() RETURNS TABLE(id integer, f timestamp without time zone, lat numeric, lng numeric)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT DISTINCT (RECURSOID), MAX(FECHA), UBICACIONLAT, UBICACIONLNG
FROM HIST_UBICACION
WHERE ESTADO = 1
GROUP BY RECURSOID, UBICACIONLAT, UBICACIONLNG;
END
$$;

ALTER FUNCTION public.f_get_recursos_working() OWNER TO postgres;

--
-- TOC entry 220 (class 1255 OID 16625)
-- Name: f_get_severity_incident(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_severity_incident(integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$
DECLARE
severity INTEGER := -1;
BEGIN
SELECT 
60*(EXTRACT(HOUR FROM (NOW()::TIMESTAMP - I.FECHANOTIFICACION))) + (EXTRACT(MINUTE FROM (NOW()::TIMESTAMP - I.FECHANOTIFICACION))) + I.GRAVEDAD
INTO severity
FROM HIST_INCIDENCIAS I
WHERE I.incidenciaid = $1;
RETURN severity;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;
END
$_$;


ALTER FUNCTION public.f_get_severity_incident(integer) OWNER TO postgres;

--
-- TOC entry 221 (class 1255 OID 16626)
-- Name: f_get_tipos_incidencia(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_tipos_incidencia() RETURNS TABLE(tipoincidenciaid integer, nombretipoincidencia character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT *
FROM TIPO_INCIDENCIA;
END
$$;


ALTER FUNCTION public.f_get_tipos_incidencia() OWNER TO postgres;

--
-- TOC entry 222 (class 1255 OID 16627)
-- Name: f_get_type_resource(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_type_resource(id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
type_id INTEGER := -1;
BEGIN
SELECT E.TIPORECURSOID INTO type_id FROM ESTACIONES E
JOIN RECURSOS R ON R.ESTACIONID = E.ESTACIONID
WHERE R.RECURSOID = id;
RETURN type_id;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;
END
$$;


ALTER FUNCTION public.f_get_type_resource(id integer) OWNER TO postgres;

--
-- TOC entry 223 (class 1255 OID 16628)
-- Name: f_get_type_station(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_type_station(id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
type_id INTEGER := -1;
BEGIN
SELECT E.TIPORECURSOID INTO type_id FROM ESTACIONES E
WHERE E.ESTACIONID = id;
RETURN type_id;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;
END
$$;


ALTER FUNCTION public.f_get_type_station(id integer) OWNER TO postgres;

--
-- TOC entry 224 (class 1255 OID 16629)
-- Name: f_get_user(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_user(u character varying) RETURNS TABLE(nom character varying, ape character varying, dir character varying, tlf character varying, obs text, v_dni character varying, v_user character varying, pass text)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT NOMBRE, APELLIDO, DIRECCION, TELEFONO, NOTAS, DNI, USERNAME, PASSWORD 
FROM USUARIOS
WHERE USERNAME = u;

END
$$;


ALTER FUNCTION public.f_get_user(u character varying) OWNER TO postgres;

--
-- TOC entry 225 (class 1255 OID 16630)
-- Name: f_get_user_id(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_user_id(dni character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
id INTEGER := -1;
BEGIN
SELECT U.USUARIOID INTO id
FROM USUARIOS U
WHERE U.DNI = dni;
RETURN id;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;
END
$$;


ALTER FUNCTION public.f_get_user_id(dni character varying) OWNER TO postgres;

--
-- TOC entry 226 (class 1255 OID 16631)
-- Name: f_insert_user(character varying, character varying, character varying, character varying, character varying, character varying, character varying, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_insert_user(name character varying, ape character varying, dir character varying, tlf character varying, obs character varying, dni character varying, v_user character varying, pass text) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
cod INTEGER := -1;
count INTEGER := 0;
BEGIN
SELECT INTO count COUNT(*) FROM USUARIOS WHERE USERNAME = v_user;
IF (count > 0) THEN
RETURN -1;
ELSE
INSERT INTO USUARIOS (NOMBRE, APELLIDO, DIRECCION, TELEFONO, NOTAS, DNI, USERNAME, PASSWORD)
VALUES (name, ape, dir, tlf, obs, dni, v_user, pass)
RETURNING USUARIOID INTO cod;
RETURN cod;
END IF;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;
END
$$;


ALTER FUNCTION public.f_insert_user(name character varying, ape character varying, dir character varying, tlf character varying, obs character varying, dni character varying, v_user character varying, pass text) OWNER TO postgres;

--
-- TOC entry 227 (class 1255 OID 16632)
-- Name: f_nueva_incidencia(integer, numeric, numeric, integer, character varying, text, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_nueva_incidencia(integer, numeric, numeric, integer, character varying, text, integer, integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$
DECLARE
ret integer;
BEGIN
ret:=-1;
INSERT INTO hist_incidencias(tipoincidenciaid,ubicacionlat,ubicacionlng,usuarioid,telefono,notas,gravedad,numeroafectados,resolucion)
VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$8) RETURNING incidenciaid INTO ret;
RETURN ret;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;
END
$_$;


ALTER FUNCTION public.f_nueva_incidencia(integer, numeric, numeric, integer, character varying, text, integer, integer) OWNER TO postgres;

--
-- TOC entry 228 (class 1255 OID 16633)
-- Name: f_nuevo_recurso(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_nuevo_recurso(integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$
DECLARE
cod INTEGER := -1;
BEGIN
INSERT INTO recursos (estacionid,nombrerecurso)
VALUES ($1,(SELECT nombretiporecurso FROM tipo_recurso WHERE tiporecursoid = f_get_type_station($1)))
RETURNING recursoid INTO cod;
RETURN cod;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;
END
$_$;


ALTER FUNCTION public.f_nuevo_recurso(integer) OWNER TO postgres;

--
-- TOC entry 229 (class 1255 OID 16634)
-- Name: f_persona_recogida(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_persona_recogida(integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$
DECLARE
count integer;
ret integer;
BEGIN
count = 0;
UPDATE hist_incidencias SET
resolucion = resolucion-1
WHERE incidenciaid = $1 AND resolucion>0
RETURNING resolucion INTO count;

IF count = 0 THEN
ret = (SELECT * FROM f_fin_incidencia($1));
END IF;

return count;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;
END
$_$;


ALTER FUNCTION public.f_persona_recogida(integer) OWNER TO postgres;

--
-- TOC entry 230 (class 1255 OID 16635)
-- Name: f_puntuar_incidencia(integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_puntuar_incidencia(integer, integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$
DECLARE
count integer;
BEGIN
count:=-1;
IF $2>=0 AND $2<=10 THEN
UPDATE hist_incidencias SET
resolucion = $2
WHERE incidenciaid = $1;
GET DIAGNOSTICS count = ROW_COUNT;
END IF;
return count;
EXCEPTION
WHEN OTHERS THEN
RETURN -2;
END
$_$;


ALTER FUNCTION public.f_puntuar_incidencia(integer, integer) OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 172 (class 1259 OID 16636)
-- Name: estaciones; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE estaciones (
    estacionid integer NOT NULL,
    tiporecursoid integer NOT NULL,
    nombreestacion character varying(20),
    ubicacionestacionlat numeric(9,6),
    ubicacionestacionlng numeric(9,6)
);


ALTER TABLE estaciones OWNER TO postgres;

--
-- TOC entry 173 (class 1259 OID 16639)
-- Name: estaciones_estacionid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE estaciones_estacionid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE estaciones_estacionid_seq OWNER TO postgres;

--
-- TOC entry 2155 (class 0 OID 0)
-- Dependencies: 173
-- Name: estaciones_estacionid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE estaciones_estacionid_seq OWNED BY estaciones.estacionid;


--
-- TOC entry 174 (class 1259 OID 16641)
-- Name: hist_incidencias; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE hist_incidencias (
    incidenciaid integer NOT NULL,
    tipoincidenciaid integer NOT NULL,
    fechanotificacion timestamp without time zone DEFAULT now(),
    fecharesolucion timestamp without time zone,
    ubicacionlat numeric(9,6),
    ubicacionlng numeric(9,6),
    usuarioid integer DEFAULT 0,
    telefono character varying(9),
    notas text,
    gravedad integer,
    numeroafectados integer,
    resolucion integer
);


ALTER TABLE hist_incidencias OWNER TO postgres;

--
-- TOC entry 175 (class 1259 OID 16649)
-- Name: hist_incidencias_incidenciaid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE hist_incidencias_incidenciaid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE hist_incidencias_incidenciaid_seq OWNER TO postgres;

--
-- TOC entry 2156 (class 0 OID 0)
-- Dependencies: 175
-- Name: hist_incidencias_incidenciaid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE hist_incidencias_incidenciaid_seq OWNED BY hist_incidencias.incidenciaid;


--
-- TOC entry 176 (class 1259 OID 16651)
-- Name: hist_incidencias_recursos; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE hist_incidencias_recursos (
    incidenciaid integer NOT NULL,
    recursoid integer NOT NULL,
    ubicacionorigenlat numeric(9,6),
    ubicacionorigenlng numeric(9,6),
    fechasalida timestamp without time zone,
    fechallegada timestamp without time zone
);


ALTER TABLE hist_incidencias_recursos OWNER TO postgres;

--
-- TOC entry 177 (class 1259 OID 16654)
-- Name: hist_ubicacion; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE hist_ubicacion (
    recursoid integer NOT NULL,
    fecha timestamp without time zone DEFAULT now() NOT NULL,
    estado integer,
    ubicacionlat numeric(9,6),
    ubicacionlng numeric(9,6)
);


ALTER TABLE hist_ubicacion OWNER TO postgres;

--
-- TOC entry 178 (class 1259 OID 16658)
-- Name: recursos; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE recursos (
    recursoid integer NOT NULL,
    estacionid integer NOT NULL,
    nombrerecurso character varying(20)
);


ALTER TABLE recursos OWNER TO postgres;

--
-- TOC entry 179 (class 1259 OID 16661)
-- Name: recursos_recursoid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE recursos_recursoid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE recursos_recursoid_seq OWNER TO postgres;

--
-- TOC entry 2157 (class 0 OID 0)
-- Dependencies: 179
-- Name: recursos_recursoid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE recursos_recursoid_seq OWNED BY recursos.recursoid;


--
-- TOC entry 180 (class 1259 OID 16663)
-- Name: tipo_incidencia; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tipo_incidencia (
    tipoincidenciaid integer NOT NULL,
    nombretipoincidencia character varying(20)
);


ALTER TABLE tipo_incidencia OWNER TO postgres;

--
-- TOC entry 181 (class 1259 OID 16666)
-- Name: tipo_incidencia_tipoincidenciaid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tipo_incidencia_tipoincidenciaid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tipo_incidencia_tipoincidenciaid_seq OWNER TO postgres;

--
-- TOC entry 2158 (class 0 OID 0)
-- Dependencies: 181
-- Name: tipo_incidencia_tipoincidenciaid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tipo_incidencia_tipoincidenciaid_seq OWNED BY tipo_incidencia.tipoincidenciaid;


--
-- TOC entry 182 (class 1259 OID 16668)
-- Name: tipo_recurso; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tipo_recurso (
    tiporecursoid integer NOT NULL,
    nombretiporecurso character varying(20)
);


ALTER TABLE tipo_recurso OWNER TO postgres;

--
-- TOC entry 183 (class 1259 OID 16671)
-- Name: tipo_recurso_tiporecursoid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tipo_recurso_tiporecursoid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tipo_recurso_tiporecursoid_seq OWNER TO postgres;

--
-- TOC entry 2159 (class 0 OID 0)
-- Dependencies: 183
-- Name: tipo_recurso_tiporecursoid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tipo_recurso_tiporecursoid_seq OWNED BY tipo_recurso.tiporecursoid;


--
-- TOC entry 184 (class 1259 OID 16673)
-- Name: usuarios; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE usuarios (
    usuarioid integer NOT NULL,
    privilegios integer DEFAULT 0,
    nombre character varying(30),
    apellido character varying(30),
    direccion character varying(80),
    telefono character varying(9),
    dni character varying(9),
    notas text,
    username character varying(20),
    password text
);


ALTER TABLE usuarios OWNER TO postgres;

--
-- TOC entry 185 (class 1259 OID 16680)
-- Name: usuarios_usuarioid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE usuarios_usuarioid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE usuarios_usuarioid_seq OWNER TO postgres;

--
-- TOC entry 2160 (class 0 OID 0)
-- Dependencies: 185
-- Name: usuarios_usuarioid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE usuarios_usuarioid_seq OWNED BY usuarios.usuarioid;


--
-- TOC entry 1989 (class 2604 OID 16682)
-- Name: estacionid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY estaciones ALTER COLUMN estacionid SET DEFAULT nextval('estaciones_estacionid_seq'::regclass);


--
-- TOC entry 1992 (class 2604 OID 16683)
-- Name: incidenciaid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias ALTER COLUMN incidenciaid SET DEFAULT nextval('hist_incidencias_incidenciaid_seq'::regclass);


--
-- TOC entry 1994 (class 2604 OID 16684)
-- Name: recursoid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recursos ALTER COLUMN recursoid SET DEFAULT nextval('recursos_recursoid_seq'::regclass);


--
-- TOC entry 1995 (class 2604 OID 16685)
-- Name: tipoincidenciaid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_incidencia ALTER COLUMN tipoincidenciaid SET DEFAULT nextval('tipo_incidencia_tipoincidenciaid_seq'::regclass);


--
-- TOC entry 1996 (class 2604 OID 16686)
-- Name: tiporecursoid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_recurso ALTER COLUMN tiporecursoid SET DEFAULT nextval('tipo_recurso_tiporecursoid_seq'::regclass);


--
-- TOC entry 1998 (class 2604 OID 16687)
-- Name: usuarioid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY usuarios ALTER COLUMN usuarioid SET DEFAULT nextval('usuarios_usuarioid_seq'::regclass);


--
-- TOC entry 2133 (class 0 OID 16636)
-- Dependencies: 172
-- Data for Name: estaciones; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO estaciones VALUES (1, 1, 'AMBULANCIAS ARRASATE', 43.063081, -2.505862);
INSERT INTO estaciones VALUES (2, 2, 'POLICIAS ARRASATE', 43.072096, -2.473052);


--
-- TOC entry 2161 (class 0 OID 0)
-- Dependencies: 173
-- Name: estaciones_estacionid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('estaciones_estacionid_seq', 6, true);


--
-- TOC entry 2135 (class 0 OID 16641)
-- Dependencies: 174
-- Data for Name: hist_incidencias; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2162 (class 0 OID 0)
-- Dependencies: 175
-- Name: hist_incidencias_incidenciaid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('hist_incidencias_incidenciaid_seq', 50, true);


--
-- TOC entry 2137 (class 0 OID 16651)
-- Dependencies: 176
-- Data for Name: hist_incidencias_recursos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2138 (class 0 OID 16654)
-- Dependencies: 177
-- Data for Name: hist_ubicacion; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- TOC entry 2139 (class 0 OID 16658)
-- Dependencies: 178
-- Data for Name: recursos; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- TOC entry 2163 (class 0 OID 0)
-- Dependencies: 179
-- Name: recursos_recursoid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('recursos_recursoid_seq', 8, true);


--
-- TOC entry 2141 (class 0 OID 16663)
-- Dependencies: 180
-- Data for Name: tipo_incidencia; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO tipo_incidencia VALUES (1, 'Ambulancia');
INSERT INTO tipo_incidencia VALUES (2, 'Policia');
INSERT INTO tipo_incidencia VALUES (3, 'Bomberos');


--
-- TOC entry 2164 (class 0 OID 0)
-- Dependencies: 181
-- Name: tipo_incidencia_tipoincidenciaid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tipo_incidencia_tipoincidenciaid_seq', 1, true);


--
-- TOC entry 2143 (class 0 OID 16668)
-- Dependencies: 182
-- Data for Name: tipo_recurso; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO tipo_recurso VALUES (1, 'Ambulancia');
INSERT INTO tipo_recurso VALUES (2, 'Policia');
INSERT INTO tipo_recurso VALUES (3, 'Bomberos');


--
-- TOC entry 2165 (class 0 OID 0)
-- Dependencies: 183
-- Name: tipo_recurso_tiporecursoid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tipo_recurso_tiporecursoid_seq', 1, true);


--
-- TOC entry 2145 (class 0 OID 16673)
-- Dependencies: 184
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO usuarios VALUES (0, 0, 'user', NULL, NULL, NULL, NULL, NULL, 'user', NULL);
INSERT INTO usuarios VALUES (12, 0, 'Urko', 'Pineda', 'C/ Blablabla', '634466832', '72851274Q', 'dasjkhsfkahsdfkjsd', 'urkopineda', '1234');
INSERT INTO usuarios VALUES (13, 0, 'Jon', 'Ayerdi', 'Pontxi Zabala 7 2ÂºB', '946555698', '45958487Z', 'Soy guay', 'jayer', '1234');


--
-- TOC entry 2166 (class 0 OID 0)
-- Dependencies: 185
-- Name: usuarios_usuarioid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('usuarios_usuarioid_seq', 13, true);


--
-- TOC entry 2000 (class 2606 OID 16689)
-- Name: estaciones_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY estaciones
    ADD CONSTRAINT estaciones_pk PRIMARY KEY (estacionid);


--
-- TOC entry 2002 (class 2606 OID 16691)
-- Name: hist_incidencias_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hist_incidencias
    ADD CONSTRAINT hist_incidencias_pk PRIMARY KEY (incidenciaid);


--
-- TOC entry 2004 (class 2606 OID 16693)
-- Name: hist_incidencias_recursos_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hist_incidencias_recursos
    ADD CONSTRAINT hist_incidencias_recursos_pk PRIMARY KEY (incidenciaid, recursoid);


--
-- TOC entry 2006 (class 2606 OID 16695)
-- Name: hist_ubicacion_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hist_ubicacion
    ADD CONSTRAINT hist_ubicacion_pk PRIMARY KEY (recursoid, fecha);


--
-- TOC entry 2008 (class 2606 OID 16697)
-- Name: recursos_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY recursos
    ADD CONSTRAINT recursos_pk PRIMARY KEY (recursoid);


--
-- TOC entry 2010 (class 2606 OID 16699)
-- Name: tipo_incidencia_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tipo_incidencia
    ADD CONSTRAINT tipo_incidencia_pk PRIMARY KEY (tipoincidenciaid);


--
-- TOC entry 2012 (class 2606 OID 16701)
-- Name: tipo_recurso_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tipo_recurso
    ADD CONSTRAINT tipo_recurso_pk PRIMARY KEY (tiporecursoid);


--
-- TOC entry 2014 (class 2606 OID 16703)
-- Name: usuarios_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_pk PRIMARY KEY (usuarioid);


--
-- TOC entry 2016 (class 2606 OID 16705)
-- Name: usuarios_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_username_key UNIQUE (username);


--
-- TOC entry 2017 (class 2606 OID 16706)
-- Name: estaciones_tipo_recurso_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY estaciones
    ADD CONSTRAINT estaciones_tipo_recurso_fk FOREIGN KEY (tiporecursoid) REFERENCES tipo_recurso(tiporecursoid);


--
-- TOC entry 2020 (class 2606 OID 16711)
-- Name: hist_incidencias_recursos_hist_incidencias_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias_recursos
    ADD CONSTRAINT hist_incidencias_recursos_hist_incidencias_fk FOREIGN KEY (incidenciaid) REFERENCES hist_incidencias(incidenciaid);


--
-- TOC entry 2021 (class 2606 OID 16716)
-- Name: hist_incidencias_recursos_recursos_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias_recursos
    ADD CONSTRAINT hist_incidencias_recursos_recursos_fk FOREIGN KEY (recursoid) REFERENCES recursos(recursoid);


--
-- TOC entry 2018 (class 2606 OID 16721)
-- Name: hist_incidencias_tipo_incidencia_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias
    ADD CONSTRAINT hist_incidencias_tipo_incidencia_fk FOREIGN KEY (tipoincidenciaid) REFERENCES tipo_incidencia(tipoincidenciaid);


--
-- TOC entry 2019 (class 2606 OID 16726)
-- Name: hist_incidencias_usuarios_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias
    ADD CONSTRAINT hist_incidencias_usuarios_fk FOREIGN KEY (usuarioid) REFERENCES usuarios(usuarioid);


--
-- TOC entry 2022 (class 2606 OID 16731)
-- Name: hist_ubicacion_recursos_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_ubicacion
    ADD CONSTRAINT hist_ubicacion_recursos_fk FOREIGN KEY (recursoid) REFERENCES recursos(recursoid);


--
-- TOC entry 2023 (class 2606 OID 16736)
-- Name: recursos_estaciones_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recursos
    ADD CONSTRAINT recursos_estaciones_fk FOREIGN KEY (estacionid) REFERENCES estaciones(estacionid);


--
-- TOC entry 2153 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2016-01-20 11:59:14

--
-- PostgreSQL database dump complete
--

