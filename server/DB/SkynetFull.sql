--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.4
-- Dumped by pg_dump version 9.4.4
-- Started on 2016-01-11 14:13:35

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 186 (class 3079 OID 11855)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2115 (class 0 OID 0)
-- Dependencies: 186
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 219 (class 1255 OID 44502)
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
-- TOC entry 220 (class 1255 OID 44529)
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
-- TOC entry 199 (class 1255 OID 44355)
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
-- TOC entry 200 (class 1255 OID 44356)
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
-- TOC entry 201 (class 1255 OID 44357)
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
-- TOC entry 202 (class 1255 OID 44358)
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
-- TOC entry 212 (class 1255 OID 44488)
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
-- TOC entry 226 (class 1255 OID 44558)
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
-- TOC entry 214 (class 1255 OID 44491)
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
-- TOC entry 215 (class 1255 OID 44493)
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
-- TOC entry 224 (class 1255 OID 44554)
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
-- TOC entry 225 (class 1255 OID 44557)
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
-- TOC entry 222 (class 1255 OID 44533)
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
-- TOC entry 203 (class 1255 OID 44361)
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
-- TOC entry 204 (class 1255 OID 44362)
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
-- TOC entry 205 (class 1255 OID 44363)
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
-- TOC entry 221 (class 1255 OID 44531)
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
-- TOC entry 216 (class 1255 OID 44494)
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
-- TOC entry 217 (class 1255 OID 44495)
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
-- TOC entry 223 (class 1255 OID 44551)
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
-- TOC entry 211 (class 1255 OID 44480)
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
-- TOC entry 206 (class 1255 OID 44365)
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
-- TOC entry 207 (class 1255 OID 44366)
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
-- TOC entry 208 (class 1255 OID 44367)
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
-- TOC entry 209 (class 1255 OID 44368)
-- Name: f_insert_user(character varying, character varying, character varying, character varying, character varying, character varying, character varying, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_insert_user(name character varying, ape character varying, dir character varying, tlf character varying, obs character varying, dni character varying, v_user character varying, pass text) RETURNS integer
    LANGUAGE plpgsql
    AS $$
	DECLARE
		cod INTEGER := -1;
	BEGIN
		INSERT INTO USUARIOS (NOMBRE, APELLIDO, DIRECCION, TELEFONO, NOTAS, DNI, USERNAME, PASSWORD)
		VALUES (name, ape, dir, tlf, obs, dni, v_user, pass)
		RETURNING USUARIOID INTO cod;
		RETURN cod;
	EXCEPTION
		WHEN OTHERS THEN
			RETURN -2;
	END
$$;


ALTER FUNCTION public.f_insert_user(name character varying, ape character varying, dir character varying, tlf character varying, obs character varying, dni character varying, v_user character varying, pass text) OWNER TO postgres;

--
-- TOC entry 210 (class 1255 OID 44479)
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
-- TOC entry 218 (class 1255 OID 44498)
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
-- TOC entry 227 (class 1255 OID 44567)
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
-- TOC entry 213 (class 1255 OID 44490)
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
-- TOC entry 172 (class 1259 OID 44369)
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
-- TOC entry 173 (class 1259 OID 44372)
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
-- TOC entry 2116 (class 0 OID 0)
-- Dependencies: 173
-- Name: estaciones_estacionid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE estaciones_estacionid_seq OWNED BY estaciones.estacionid;


--
-- TOC entry 174 (class 1259 OID 44374)
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
-- TOC entry 175 (class 1259 OID 44382)
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
-- TOC entry 2117 (class 0 OID 0)
-- Dependencies: 175
-- Name: hist_incidencias_incidenciaid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE hist_incidencias_incidenciaid_seq OWNED BY hist_incidencias.incidenciaid;


--
-- TOC entry 176 (class 1259 OID 44384)
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
-- TOC entry 177 (class 1259 OID 44387)
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
-- TOC entry 178 (class 1259 OID 44391)
-- Name: recursos; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE recursos (
    recursoid integer NOT NULL,
    estacionid integer NOT NULL,
    nombrerecurso character varying(20)
);


ALTER TABLE recursos OWNER TO postgres;

--
-- TOC entry 179 (class 1259 OID 44394)
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
-- TOC entry 2118 (class 0 OID 0)
-- Dependencies: 179
-- Name: recursos_recursoid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE recursos_recursoid_seq OWNED BY recursos.recursoid;


--
-- TOC entry 180 (class 1259 OID 44396)
-- Name: tipo_incidencia; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tipo_incidencia (
    tipoincidenciaid integer NOT NULL,
    nombretipoincidencia character varying(20)
);


ALTER TABLE tipo_incidencia OWNER TO postgres;

--
-- TOC entry 181 (class 1259 OID 44399)
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
-- TOC entry 2119 (class 0 OID 0)
-- Dependencies: 181
-- Name: tipo_incidencia_tipoincidenciaid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tipo_incidencia_tipoincidenciaid_seq OWNED BY tipo_incidencia.tipoincidenciaid;


--
-- TOC entry 182 (class 1259 OID 44401)
-- Name: tipo_recurso; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tipo_recurso (
    tiporecursoid integer NOT NULL,
    nombretiporecurso character varying(20)
);


ALTER TABLE tipo_recurso OWNER TO postgres;

--
-- TOC entry 183 (class 1259 OID 44404)
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
-- TOC entry 2120 (class 0 OID 0)
-- Dependencies: 183
-- Name: tipo_recurso_tiporecursoid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tipo_recurso_tiporecursoid_seq OWNED BY tipo_recurso.tiporecursoid;


--
-- TOC entry 184 (class 1259 OID 44406)
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
-- TOC entry 185 (class 1259 OID 44413)
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
-- TOC entry 2121 (class 0 OID 0)
-- Dependencies: 185
-- Name: usuarios_usuarioid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE usuarios_usuarioid_seq OWNED BY usuarios.usuarioid;


--
-- TOC entry 1950 (class 2604 OID 44415)
-- Name: estacionid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY estaciones ALTER COLUMN estacionid SET DEFAULT nextval('estaciones_estacionid_seq'::regclass);


--
-- TOC entry 1953 (class 2604 OID 44416)
-- Name: incidenciaid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias ALTER COLUMN incidenciaid SET DEFAULT nextval('hist_incidencias_incidenciaid_seq'::regclass);


--
-- TOC entry 1955 (class 2604 OID 44417)
-- Name: recursoid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recursos ALTER COLUMN recursoid SET DEFAULT nextval('recursos_recursoid_seq'::regclass);


--
-- TOC entry 1956 (class 2604 OID 44418)
-- Name: tipoincidenciaid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_incidencia ALTER COLUMN tipoincidenciaid SET DEFAULT nextval('tipo_incidencia_tipoincidenciaid_seq'::regclass);


--
-- TOC entry 1957 (class 2604 OID 44419)
-- Name: tiporecursoid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_recurso ALTER COLUMN tiporecursoid SET DEFAULT nextval('tipo_recurso_tiporecursoid_seq'::regclass);


--
-- TOC entry 1959 (class 2604 OID 44420)
-- Name: usuarioid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY usuarios ALTER COLUMN usuarioid SET DEFAULT nextval('usuarios_usuarioid_seq'::regclass);


--
-- TOC entry 2094 (class 0 OID 44369)
-- Dependencies: 172
-- Data for Name: estaciones; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO estaciones VALUES (5, 1, 'AMBULANCIAS ARRASATE', 43.063081, -2.505862);
INSERT INTO estaciones VALUES (6, 2, 'POLICIAS ARRASATE', 43.072096, -2.473052);


--
-- TOC entry 2122 (class 0 OID 0)
-- Dependencies: 173
-- Name: estaciones_estacionid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('estaciones_estacionid_seq', 6, true);


--
-- TOC entry 2096 (class 0 OID 44374)
-- Dependencies: 174
-- Data for Name: hist_incidencias; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO hist_incidencias VALUES (6, 1, '2015-12-28 18:59:06.843', NULL, 1.200000, 3.400000, 0, '945654234', 'hola', 2, 5, 5);
INSERT INTO hist_incidencias VALUES (7, 1, '2015-12-28 19:03:37.812', NULL, 1.200000, 3.400000, 1, '-', 'sadsada', 98, 78, 78);
INSERT INTO hist_incidencias VALUES (8, 1, '2015-12-28 19:16:22.072', NULL, 4.800000, 89.454000, 3, '123456789', 'latlng', 3, 5, 5);
INSERT INTO hist_incidencias VALUES (9, 2, '2015-12-28 19:39:55.714', NULL, 4.800000, 8.400000, 0, '987654321', 'Comentarios', 2, 78, 78);
INSERT INTO hist_incidencias VALUES (4, 1, '2015-12-28 16:57:09.33', '2015-12-28 23:22:37.908', 8.200000, 1.800000, 0, '946010514', 'Hola ke ase', 4, 9, 7);
INSERT INTO hist_incidencias VALUES (10, 1, '2015-12-30 19:42:04.726', NULL, 23.000000, 23.000000, 0, '342343244', 'aaa', 4, 12, 12);
INSERT INTO hist_incidencias VALUES (11, 1, '2015-12-30 19:51:58.347', NULL, 1.000000, 1.000000, 0, '111111111', 'aaa', 4, 12, 12);
INSERT INTO hist_incidencias VALUES (12, 1, '2015-12-30 19:53:38.683', NULL, 12.000000, 12.000000, 0, '444444444', 'sadasd', 4, 12, 12);
INSERT INTO hist_incidencias VALUES (13, 1, '2015-12-30 19:58:36.142', NULL, 12.000000, 12.000000, 0, '121212121', 'bbb', 4, 12, 12);
INSERT INTO hist_incidencias VALUES (14, 1, '2015-12-30 20:05:25.765', NULL, 12.000000, 12.000000, 0, '234232344', 'sdda', 4, 12, 12);
INSERT INTO hist_incidencias VALUES (15, 1, '2015-12-30 20:06:14.572', NULL, 3.000000, 3.000000, 0, '23132', 'sdasdasd', 4, 34, 34);
INSERT INTO hist_incidencias VALUES (16, 1, '2015-12-30 20:06:26.048', NULL, 1.000000, 1.000000, 0, '1', '1', 4, 1, 1);
INSERT INTO hist_incidencias VALUES (17, 1, '2015-12-30 20:55:52.56', NULL, 12.000000, 12.000000, 0, '213123213', 'asddas', 4, 12, 12);
INSERT INTO hist_incidencias VALUES (18, 1, '2015-12-30 20:57:26.441', NULL, 12.000000, 12.000000, 0, '222222222', '22', 4, 12, 12);
INSERT INTO hist_incidencias VALUES (19, 1, '2015-12-30 20:57:37.14', NULL, 2.000000, 2.000000, 0, '222', '2', 4, 2, 2);
INSERT INTO hist_incidencias VALUES (20, 1, '2015-12-30 21:03:22.861', NULL, 1.000000, 1.000000, 0, '1', '1', 4, 1, 1);
INSERT INTO hist_incidencias VALUES (21, 1, '2015-12-30 21:06:50.829', NULL, 1.000000, 1.000000, 0, '1', '1', 4, 1, 1);
INSERT INTO hist_incidencias VALUES (22, 1, '2015-12-30 21:07:53.99', NULL, 1.000000, 1.000000, 0, '1', '1', 4, 1, 1);
INSERT INTO hist_incidencias VALUES (23, 1, '2015-12-30 21:09:27.546', NULL, 1.000000, 1.000000, 0, '1', '1', 4, 1, 1);
INSERT INTO hist_incidencias VALUES (24, 1, '2015-12-30 21:12:33.007', NULL, 1.000000, 1.000000, 0, '1', '1', 4, 1, 1);
INSERT INTO hist_incidencias VALUES (25, 1, '2015-12-30 21:14:09.026', NULL, 1.000000, 1.000000, 0, '1', '1', 4, 1, 1);
INSERT INTO hist_incidencias VALUES (26, 1, '2016-01-05 19:43:54.857', NULL, 8.200000, 1.800000, 5, '946010514', 'Hola ke ase', 4, 9, 9);
INSERT INTO hist_incidencias VALUES (5, 1, '2015-12-28 17:02:57.759', '2016-01-09 20:30:02.804', 8.200000, 1.800000, 0, '946010514', 'Hola ke ase', 4, 9, -1);


--
-- TOC entry 2123 (class 0 OID 0)
-- Dependencies: 175
-- Name: hist_incidencias_incidenciaid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('hist_incidencias_incidenciaid_seq', 26, true);


--
-- TOC entry 2098 (class 0 OID 44384)
-- Dependencies: 176
-- Data for Name: hist_incidencias_recursos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2099 (class 0 OID 44387)
-- Dependencies: 177
-- Data for Name: hist_ubicacion; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO hist_ubicacion VALUES (2, '2016-01-04 18:53:23.6', 0, 43.000000, -2.000000);
INSERT INTO hist_ubicacion VALUES (2, '2016-01-04 18:53:24.851', 0, 43.000000, -2.000000);
INSERT INTO hist_ubicacion VALUES (2, '2016-01-04 18:53:31.601', 0, 43.000000, -3.000000);
INSERT INTO hist_ubicacion VALUES (2, '2016-01-04 19:01:23.038', 0, 43.000000, -2.000000);
INSERT INTO hist_ubicacion VALUES (2, '2016-01-04 19:02:25.176', 0, 43.000000, -4.000000);
INSERT INTO hist_ubicacion VALUES (2, '2016-01-04 19:10:34.579', 0, 44.000000, -4.000000);
INSERT INTO hist_ubicacion VALUES (7, '2016-01-09 19:17:49.083', 0, 43.200000, -2.555000);
INSERT INTO hist_ubicacion VALUES (7, '2016-01-09 19:19:25.019', 1, 43.200000, -2.555000);


--
-- TOC entry 2100 (class 0 OID 44391)
-- Dependencies: 178
-- Data for Name: recursos; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO recursos VALUES (1, 5, 'Ambulancia');
INSERT INTO recursos VALUES (2, 5, 'Ambulancia');
INSERT INTO recursos VALUES (3, 6, 'Policia');
INSERT INTO recursos VALUES (4, 6, 'Policia');
INSERT INTO recursos VALUES (5, 6, 'Policia');
INSERT INTO recursos VALUES (6, 6, 'Policia');
INSERT INTO recursos VALUES (7, 5, 'Ambulancia');


--
-- TOC entry 2124 (class 0 OID 0)
-- Dependencies: 179
-- Name: recursos_recursoid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('recursos_recursoid_seq', 7, true);


--
-- TOC entry 2102 (class 0 OID 44396)
-- Dependencies: 180
-- Data for Name: tipo_incidencia; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO tipo_incidencia VALUES (1, 'Ambulancia');
INSERT INTO tipo_incidencia VALUES (2, 'Policia');
INSERT INTO tipo_incidencia VALUES (3, 'Bomberos');


--
-- TOC entry 2125 (class 0 OID 0)
-- Dependencies: 181
-- Name: tipo_incidencia_tipoincidenciaid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tipo_incidencia_tipoincidenciaid_seq', 1, true);


--
-- TOC entry 2104 (class 0 OID 44401)
-- Dependencies: 182
-- Data for Name: tipo_recurso; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO tipo_recurso VALUES (1, 'Ambulancia');
INSERT INTO tipo_recurso VALUES (2, 'Policia');
INSERT INTO tipo_recurso VALUES (3, 'Bomberos');


--
-- TOC entry 2126 (class 0 OID 0)
-- Dependencies: 183
-- Name: tipo_recurso_tiporecursoid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tipo_recurso_tiporecursoid_seq', 1, true);


--
-- TOC entry 2106 (class 0 OID 44406)
-- Dependencies: 184
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO usuarios VALUES (1, 1, 'Jon', 'Ayerdi', 'Pontxi Zabala 7 2ºB', '688683155', '12345678Z', 'Alergico a los cacahuetes.', 'jayer', '1234');
INSERT INTO usuarios VALUES (2, 1, 'Urko', 'Pineda', 'Durango', '946555698', '98765432A', 'Feo.', 'turkish', '4321');
INSERT INTO usuarios VALUES (3, 1, 'Gorka', 'Olalde', 'Monte', '123456789', '98765432P', 'âº', 'olaldiko', '1324');
INSERT INTO usuarios VALUES (4, 0, 'a', 'a', 'a', '123456789', '12345678A', 'a', 'a', 'a');
INSERT INTO usuarios VALUES (5, 0, 'b', 'b', 'b', '123456789', '12345678B', 'b', 'b', 'b');
INSERT INTO usuarios VALUES (0, 0, NULL, NULL, NULL, NULL, NULL, NULL, 'INVITADO', NULL);


--
-- TOC entry 2127 (class 0 OID 0)
-- Dependencies: 185
-- Name: usuarios_usuarioid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('usuarios_usuarioid_seq', 8, true);


--
-- TOC entry 1961 (class 2606 OID 44422)
-- Name: estaciones_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY estaciones
    ADD CONSTRAINT estaciones_pk PRIMARY KEY (estacionid);


--
-- TOC entry 1963 (class 2606 OID 44424)
-- Name: hist_incidencias_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hist_incidencias
    ADD CONSTRAINT hist_incidencias_pk PRIMARY KEY (incidenciaid);


--
-- TOC entry 1965 (class 2606 OID 44426)
-- Name: hist_incidencias_recursos_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hist_incidencias_recursos
    ADD CONSTRAINT hist_incidencias_recursos_pk PRIMARY KEY (incidenciaid, recursoid);


--
-- TOC entry 1967 (class 2606 OID 44428)
-- Name: hist_ubicacion_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hist_ubicacion
    ADD CONSTRAINT hist_ubicacion_pk PRIMARY KEY (recursoid, fecha);


--
-- TOC entry 1969 (class 2606 OID 44430)
-- Name: recursos_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY recursos
    ADD CONSTRAINT recursos_pk PRIMARY KEY (recursoid);


--
-- TOC entry 1971 (class 2606 OID 44432)
-- Name: tipo_incidencia_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tipo_incidencia
    ADD CONSTRAINT tipo_incidencia_pk PRIMARY KEY (tipoincidenciaid);


--
-- TOC entry 1973 (class 2606 OID 44434)
-- Name: tipo_recurso_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tipo_recurso
    ADD CONSTRAINT tipo_recurso_pk PRIMARY KEY (tiporecursoid);


--
-- TOC entry 1975 (class 2606 OID 44436)
-- Name: usuarios_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_pk PRIMARY KEY (usuarioid);


--
-- TOC entry 1977 (class 2606 OID 44438)
-- Name: usuarios_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_username_key UNIQUE (username);


--
-- TOC entry 1978 (class 2606 OID 44439)
-- Name: estaciones_tipo_recurso_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY estaciones
    ADD CONSTRAINT estaciones_tipo_recurso_fk FOREIGN KEY (tiporecursoid) REFERENCES tipo_recurso(tiporecursoid);


--
-- TOC entry 1981 (class 2606 OID 44444)
-- Name: hist_incidencias_recursos_hist_incidencias_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias_recursos
    ADD CONSTRAINT hist_incidencias_recursos_hist_incidencias_fk FOREIGN KEY (incidenciaid) REFERENCES hist_incidencias(incidenciaid);


--
-- TOC entry 1982 (class 2606 OID 44449)
-- Name: hist_incidencias_recursos_recursos_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias_recursos
    ADD CONSTRAINT hist_incidencias_recursos_recursos_fk FOREIGN KEY (recursoid) REFERENCES recursos(recursoid);


--
-- TOC entry 1979 (class 2606 OID 44454)
-- Name: hist_incidencias_tipo_incidencia_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias
    ADD CONSTRAINT hist_incidencias_tipo_incidencia_fk FOREIGN KEY (tipoincidenciaid) REFERENCES tipo_incidencia(tipoincidenciaid);


--
-- TOC entry 1980 (class 2606 OID 44459)
-- Name: hist_incidencias_usuarios_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias
    ADD CONSTRAINT hist_incidencias_usuarios_fk FOREIGN KEY (usuarioid) REFERENCES usuarios(usuarioid);


--
-- TOC entry 1983 (class 2606 OID 44464)
-- Name: hist_ubicacion_recursos_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_ubicacion
    ADD CONSTRAINT hist_ubicacion_recursos_fk FOREIGN KEY (recursoid) REFERENCES recursos(recursoid);


--
-- TOC entry 1984 (class 2606 OID 44469)
-- Name: recursos_estaciones_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recursos
    ADD CONSTRAINT recursos_estaciones_fk FOREIGN KEY (estacionid) REFERENCES estaciones(estacionid);


--
-- TOC entry 2114 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2016-01-11 14:13:36

--
-- PostgreSQL database dump complete
--

