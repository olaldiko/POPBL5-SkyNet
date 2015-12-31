--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.4
-- Dumped by pg_dump version 9.4.4
-- Started on 2015-12-28 16:19:26

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
-- TOC entry 2099 (class 0 OID 0)
-- Dependencies: 186
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 199 (class 1255 OID 44227)
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
-- TOC entry 200 (class 1255 OID 44228)
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
-- TOC entry 201 (class 1255 OID 44229)
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
-- TOC entry 202 (class 1255 OID 44230)
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
-- TOC entry 203 (class 1255 OID 44231)
-- Name: f_get_latest_location(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_latest_location(id integer) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
	DECLARE
		lat DECIMAL(9,6) := 0;
		lng DECIMAL(9,6) := 0;
		datetim TIMESTAMP := NOW();
		loc VARCHAR := 'N/A';
	BEGIN
		SELECT H.UBICACIONLAT, H.UBICACIONLNG, H.FECHA INTO lat, lng, datetim
		FROM HIST_UBICACION H
		WHERE H.RECURSOID = id AND H.FECHA = MAX(H.FECHA);
		loc = datetim || '#' || lat || '#' || lng;
	EXCEPTION
		WHEN OTHERS THEN
			RETURN 'ERROR';
	END
$$;


ALTER FUNCTION public.f_get_latest_location(id integer) OWNER TO postgres;

--
-- TOC entry 204 (class 1255 OID 44232)
-- Name: f_get_location_resource(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_location_resource(id integer) RETURNS record
    LANGUAGE plpgsql
    AS $$
	DECLARE
		x RECORD;
	BEGIN
		SELECT H.UBICACIONLAT AS LAT, H.UBICACIONLNG AS LNG, H.FECHA AS FECHA INTO x
		FROM HIST_UBICACION H
		WHERE H.RECURSOID = id AND H.FECHA = MAX(H.FECHA);
		RETURN x;
	EXCEPTION
		WHEN OTHERS THEN
			RETURN NULL;
	END
$$;


ALTER FUNCTION public.f_get_location_resource(id integer) OWNER TO postgres;

--
-- TOC entry 205 (class 1255 OID 44233)
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
-- TOC entry 206 (class 1255 OID 44234)
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
-- TOC entry 207 (class 1255 OID 44235)
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
-- TOC entry 208 (class 1255 OID 44236)
-- Name: f_get_severity_incident(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION f_get_severity_incident(id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
	DECLARE
		severity INTEGER := -1;
	BEGIN
		SELECT (EXTRACT(MINUTE FROM (NOW()::TIMESTAMP - I.FECHANOTIFICACION)) * I.GRAVEDAD) INTO severity
		FROM HIST_INCIDENCIAS I;
		RETURN severity;
	EXCEPTION
		WHEN OTHERS THEN
			RETURN -2;
	END
$$;


ALTER FUNCTION public.f_get_severity_incident(id integer) OWNER TO postgres;

--
-- TOC entry 209 (class 1255 OID 44237)
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
-- TOC entry 210 (class 1255 OID 44238)
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
-- TOC entry 211 (class 1255 OID 44239)
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
-- TOC entry 212 (class 1255 OID 44240)
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

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 172 (class 1259 OID 44241)
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
-- TOC entry 173 (class 1259 OID 44244)
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
-- TOC entry 2100 (class 0 OID 0)
-- Dependencies: 173
-- Name: estaciones_estacionid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE estaciones_estacionid_seq OWNED BY estaciones.estacionid;


--
-- TOC entry 174 (class 1259 OID 44246)
-- Name: hist_incidencias; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE hist_incidencias (
    incidenciaid integer NOT NULL,
    tipoincidenciaid integer NOT NULL,
    fechanotificacion timestamp without time zone DEFAULT now(),
    fecharesolucion timestamp without time zone DEFAULT NULL,
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
-- TOC entry 175 (class 1259 OID 44253)
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
-- TOC entry 2101 (class 0 OID 0)
-- Dependencies: 175
-- Name: hist_incidencias_incidenciaid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE hist_incidencias_incidenciaid_seq OWNED BY hist_incidencias.incidenciaid;


--
-- TOC entry 176 (class 1259 OID 44255)
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
-- TOC entry 177 (class 1259 OID 44258)
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
-- TOC entry 178 (class 1259 OID 44262)
-- Name: recursos; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE recursos (
    recursoid integer NOT NULL,
    estacionid integer NOT NULL,
    nombrerecurso character varying(20)
);


ALTER TABLE recursos OWNER TO postgres;

--
-- TOC entry 179 (class 1259 OID 44265)
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
-- TOC entry 2102 (class 0 OID 0)
-- Dependencies: 179
-- Name: recursos_recursoid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE recursos_recursoid_seq OWNED BY recursos.recursoid;


--
-- TOC entry 180 (class 1259 OID 44267)
-- Name: tipo_incidencia; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tipo_incidencia (
    tipoincidenciaid integer NOT NULL,
    nombretipoincidencia character varying(20)
);


ALTER TABLE tipo_incidencia OWNER TO postgres;

--
-- TOC entry 181 (class 1259 OID 44270)
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
-- TOC entry 2103 (class 0 OID 0)
-- Dependencies: 181
-- Name: tipo_incidencia_tipoincidenciaid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tipo_incidencia_tipoincidenciaid_seq OWNED BY tipo_incidencia.tipoincidenciaid;


--
-- TOC entry 182 (class 1259 OID 44272)
-- Name: tipo_recurso; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tipo_recurso (
    tiporecursoid integer NOT NULL,
    nombretiporecurso character varying(20)
);


ALTER TABLE tipo_recurso OWNER TO postgres;

--
-- TOC entry 183 (class 1259 OID 44275)
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
-- TOC entry 2104 (class 0 OID 0)
-- Dependencies: 183
-- Name: tipo_recurso_tiporecursoid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tipo_recurso_tiporecursoid_seq OWNED BY tipo_recurso.tiporecursoid;


--
-- TOC entry 184 (class 1259 OID 44277)
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
-- TOC entry 185 (class 1259 OID 44286)
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
-- TOC entry 2105 (class 0 OID 0)
-- Dependencies: 185
-- Name: usuarios_usuarioid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE usuarios_usuarioid_seq OWNED BY usuarios.usuarioid;


--
-- TOC entry 1935 (class 2604 OID 44288)
-- Name: estacionid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY estaciones ALTER COLUMN estacionid SET DEFAULT nextval('estaciones_estacionid_seq'::regclass);


--
-- TOC entry 1937 (class 2604 OID 44289)
-- Name: incidenciaid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias ALTER COLUMN incidenciaid SET DEFAULT nextval('hist_incidencias_incidenciaid_seq'::regclass);


--
-- TOC entry 1939 (class 2604 OID 44290)
-- Name: recursoid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recursos ALTER COLUMN recursoid SET DEFAULT nextval('recursos_recursoid_seq'::regclass);


--
-- TOC entry 1940 (class 2604 OID 44291)
-- Name: tipoincidenciaid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_incidencia ALTER COLUMN tipoincidenciaid SET DEFAULT nextval('tipo_incidencia_tipoincidenciaid_seq'::regclass);


--
-- TOC entry 1941 (class 2604 OID 44292)
-- Name: tiporecursoid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_recurso ALTER COLUMN tiporecursoid SET DEFAULT nextval('tipo_recurso_tiporecursoid_seq'::regclass);


--
-- TOC entry 1943 (class 2604 OID 44293)
-- Name: usuarioid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY usuarios ALTER COLUMN usuarioid SET DEFAULT nextval('usuarios_usuarioid_seq'::regclass);


--
-- TOC entry 2078 (class 0 OID 44241)
-- Dependencies: 172
-- Data for Name: estaciones; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2106 (class 0 OID 0)
-- Dependencies: 173
-- Name: estaciones_estacionid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('estaciones_estacionid_seq', 1, true);


--
-- TOC entry 2080 (class 0 OID 44246)
-- Dependencies: 174
-- Data for Name: hist_incidencias; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2107 (class 0 OID 0)
-- Dependencies: 175
-- Name: hist_incidencias_incidenciaid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('hist_incidencias_incidenciaid_seq', 1, false);


--
-- TOC entry 2082 (class 0 OID 44255)
-- Dependencies: 176
-- Data for Name: hist_incidencias_recursos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2083 (class 0 OID 44258)
-- Dependencies: 177
-- Data for Name: hist_ubicacion; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2084 (class 0 OID 44262)
-- Dependencies: 178
-- Data for Name: recursos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2108 (class 0 OID 0)
-- Dependencies: 179
-- Name: recursos_recursoid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('recursos_recursoid_seq', 1, false);


--
-- TOC entry 2086 (class 0 OID 44267)
-- Dependencies: 180
-- Data for Name: tipo_incidencia; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2109 (class 0 OID 0)
-- Dependencies: 181
-- Name: tipo_incidencia_tipoincidenciaid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tipo_incidencia_tipoincidenciaid_seq', 1, true);


--
-- TOC entry 2088 (class 0 OID 44272)
-- Dependencies: 182
-- Data for Name: tipo_recurso; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2110 (class 0 OID 0)
-- Dependencies: 183
-- Name: tipo_recurso_tiporecursoid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tipo_recurso_tiporecursoid_seq', 1, true);


--
-- TOC entry 2090 (class 0 OID 44277)
-- Dependencies: 184
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO usuarios VALUES (1, 1, 'Jon', 'Ayerdi', 'Pontxi Zabala 7 2ºB', '688683155', '12345678Z', 'Alergico a los cacahuetes.', 'jayer', '1234');
INSERT INTO usuarios VALUES (2, 1, 'Urko', 'Pineda', 'Durango', '946555698', '98765432A', 'Feo.', 'turkish', '4321');
INSERT INTO usuarios VALUES (3, 1, 'Gorka', 'Olalde', 'Monte', '123456789', '98765432P', 'âº', 'olaldiko', '1324');
INSERT INTO usuarios VALUES (4, 0, 'a', 'a', 'a', '123456789', '12345678A', 'a', 'a', 'a');
INSERT INTO usuarios VALUES (5, 0, 'b', 'b', 'b', '123456789', '12345678B', 'b', 'b', 'b');


--
-- TOC entry 2111 (class 0 OID 0)
-- Dependencies: 185
-- Name: usuarios_usuarioid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('usuarios_usuarioid_seq', 8, true);


--
-- TOC entry 1945 (class 2606 OID 44295)
-- Name: estaciones_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY estaciones
    ADD CONSTRAINT estaciones_pk PRIMARY KEY (estacionid);


--
-- TOC entry 1947 (class 2606 OID 44297)
-- Name: hist_incidencias_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hist_incidencias
    ADD CONSTRAINT hist_incidencias_pk PRIMARY KEY (incidenciaid);


--
-- TOC entry 1949 (class 2606 OID 44299)
-- Name: hist_incidencias_recursos_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hist_incidencias_recursos
    ADD CONSTRAINT hist_incidencias_recursos_pk PRIMARY KEY (incidenciaid, recursoid);


--
-- TOC entry 1951 (class 2606 OID 44301)
-- Name: hist_ubicacion_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hist_ubicacion
    ADD CONSTRAINT hist_ubicacion_pk PRIMARY KEY (recursoid, fecha);


--
-- TOC entry 1953 (class 2606 OID 44303)
-- Name: recursos_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY recursos
    ADD CONSTRAINT recursos_pk PRIMARY KEY (recursoid);


--
-- TOC entry 1955 (class 2606 OID 44305)
-- Name: tipo_incidencia_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tipo_incidencia
    ADD CONSTRAINT tipo_incidencia_pk PRIMARY KEY (tipoincidenciaid);


--
-- TOC entry 1957 (class 2606 OID 44307)
-- Name: tipo_recurso_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tipo_recurso
    ADD CONSTRAINT tipo_recurso_pk PRIMARY KEY (tiporecursoid);


--
-- TOC entry 1959 (class 2606 OID 44309)
-- Name: usuarios_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_pk PRIMARY KEY (usuarioid);


--
-- TOC entry 1961 (class 2606 OID 44285)
-- Name: usuarios_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_username_key UNIQUE (username);


--
-- TOC entry 1962 (class 2606 OID 44310)
-- Name: estaciones_tipo_recurso_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY estaciones
    ADD CONSTRAINT estaciones_tipo_recurso_fk FOREIGN KEY (tiporecursoid) REFERENCES tipo_recurso(tiporecursoid);


--
-- TOC entry 1965 (class 2606 OID 44315)
-- Name: hist_incidencias_recursos_hist_incidencias_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias_recursos
    ADD CONSTRAINT hist_incidencias_recursos_hist_incidencias_fk FOREIGN KEY (incidenciaid) REFERENCES hist_incidencias(incidenciaid);


--
-- TOC entry 1966 (class 2606 OID 44320)
-- Name: hist_incidencias_recursos_recursos_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias_recursos
    ADD CONSTRAINT hist_incidencias_recursos_recursos_fk FOREIGN KEY (recursoid) REFERENCES recursos(recursoid);


--
-- TOC entry 1963 (class 2606 OID 44325)
-- Name: hist_incidencias_tipo_incidencia_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias
    ADD CONSTRAINT hist_incidencias_tipo_incidencia_fk FOREIGN KEY (tipoincidenciaid) REFERENCES tipo_incidencia(tipoincidenciaid);


--
-- TOC entry 1964 (class 2606 OID 44330)
-- Name: hist_incidencias_usuarios_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_incidencias
    ADD CONSTRAINT hist_incidencias_usuarios_fk FOREIGN KEY (usuarioid) REFERENCES usuarios(usuarioid);


--
-- TOC entry 1967 (class 2606 OID 44335)
-- Name: hist_ubicacion_recursos_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hist_ubicacion
    ADD CONSTRAINT hist_ubicacion_recursos_fk FOREIGN KEY (recursoid) REFERENCES recursos(recursoid);


--
-- TOC entry 1968 (class 2606 OID 44340)
-- Name: recursos_estaciones_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recursos
    ADD CONSTRAINT recursos_estaciones_fk FOREIGN KEY (estacionid) REFERENCES estaciones(estacionid);


--
-- TOC entry 2098 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2015-12-28 16:19:27

--
-- PostgreSQL database dump complete
--

