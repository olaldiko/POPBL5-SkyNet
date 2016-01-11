
INSERT INTO USUARIOS (usuarioid,USERNAME) values (0,'INVITADO');

INSERT INTO tipo_incidencia values(1,'Ambulancia');
INSERT INTO tipo_incidencia values(2,'Policia');
INSERT INTO tipo_incidencia values(3,'Bomberos');

INSERT INTO tipo_recurso values(1,'Ambulancia');
INSERT INTO tipo_recurso values(2,'Policia');
INSERT INTO tipo_recurso values(3,'Bomberos');

DROP FUNCTION f_nueva_incidencia(integer,numeric,numeric,integer,character varying,text,integer,integer);
CREATE FUNCTION f_nueva_incidencia(integer,numeric,numeric,integer,character varying,text,integer,integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
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
$$;

SELECT f_nueva_incidencia(1,8.2,1.8,5,'946010514','Hola ke ase',4,9);
SELECT * FROM hist_incidencias;

DROP FUNCTION f_get_severity_incident(integer);
CREATE FUNCTION f_get_severity_incident(integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
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
$$;

SELECT * FROM f_get_severity_incident(6);

DROP FUNCTION f_get_incidencias_abiertas();
CREATE FUNCTION f_get_incidencias_abiertas() RETURNS TABLE(incidenciaid integer,tipoincidenciaid integer,fechanotificacion timestamp,fecharesolucion timestamp,ubicacionlat numeric,ubicacionlng numeric,usuarioid integer,telefono character varying,notas text,gravedad integer,numeroafectados integer,resolucion integer)
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

SELECT * FROM f_get_incidencias_abiertas();

DROP FUNCTION f_get_incidencias_abiertas_usuario(integer);
CREATE FUNCTION f_get_incidencias_abiertas_usuario(integer) RETURNS TABLE(incidenciaid integer,tipoincidenciaid integer,fechanotificacion timestamp,fecharesolucion timestamp,ubicacionlat numeric,ubicacionlng numeric,usuarioid integer,telefono character varying,notas text,gravedad integer,numeroafectados integer,resolucion integer)
    LANGUAGE plpgsql
    AS $$
	BEGIN
	RETURN QUERY
		SELECT *
		FROM hist_incidencias
		WHERE hist_incidencias.fecharesolucion IS NULL AND $1>0 AND (hist_incidencias.usuarioid = $1 OR (SELECT privilegios FROM usuarios WHERE usuarios.usuarioid=$1) = 1)
		ORDER BY hist_incidencias.fechanotificacion;
	END
$$;

SELECT * FROM USUARIOS;
SELECT * FROM f_get_incidencias_abiertas_usuario(5);

DROP FUNCTION f_get_tipos_incidencia();
CREATE FUNCTION f_get_tipos_incidencia() RETURNS TABLE(tipoIncidenciaId integer, nombreTipoIncidencia character varying)
    LANGUAGE plpgsql
    AS $$
	BEGIN
	RETURN QUERY
		SELECT *
		FROM TIPO_INCIDENCIA;
	END
$$;

SELECT * FROM f_get_tipos_incidencia();

DROP FUNCTION f_persona_recogida(integer);
CREATE FUNCTION f_persona_recogida(integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
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
$$;

SELECT * FROM f_get_incidencias_abiertas();
SELECT * FROM f_persona_recogida(5);

DROP FUNCTION f_fin_incidencia(integer);
CREATE FUNCTION f_fin_incidencia(integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
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
$$;

SELECT f_fin_incidencia(4);

DROP FUNCTION f_puntuar_incidencia(integer,integer);
CREATE FUNCTION f_puntuar_incidencia(integer,integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
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
$$;

SELECT f_puntuar_incidencia(4,7);

DROP FUNCTION f_get_estaciones();
CREATE FUNCTION f_get_estaciones() RETURNS TABLE(estacionid integer,tiporecursoid integer,nombreestacion character varying,ubicacionestacionlat numeric,ubicacionestacionlng numeric)
    LANGUAGE plpgsql
    AS $$
	BEGIN
	RETURN QUERY
		SELECT *
		FROM ESTACIONES;
	END
$$;

DROP FUNCTION f_get_estaciones(integer);
CREATE FUNCTION f_get_estaciones(integer) RETURNS TABLE(estacionid integer,tiporecursoid integer,nombreestacion character varying,ubicacionestacionlat numeric,ubicacionestacionlng numeric)
    LANGUAGE plpgsql
    AS $$
	BEGIN
	RETURN QUERY
		SELECT *
		FROM ESTACIONES
		WHERE ESTACIONES.tiporecursoid=$1;
	END
$$;

DROP FUNCTION f_get_estacion(integer);
CREATE FUNCTION f_get_estacion(integer) RETURNS TABLE(estacionid integer,tiporecursoid integer,nombreestacion character varying,ubicacionestacionlat numeric,ubicacionestacionlng numeric)
    LANGUAGE plpgsql
    AS $$
	BEGIN
	RETURN QUERY
		SELECT *
		FROM ESTACIONES
		WHERE ESTACIONES.ESTACIONID=$1;
	END
$$;

INSERT INTO tipo_recurso values(1,'Ambulancia');
INSERT INTO tipo_recurso values(2,'Policia');
INSERT INTO tipo_recurso values(3,'Bomberos');

INSERT INTO ESTACIONES (tiporecursoid,nombreestacion,ubicacionestacionlat,ubicacionestacionlng) VALUES (1,'AMBULANCIAS ARRASATE',43.063081, -2.505862);
INSERT INTO ESTACIONES (tiporecursoid,nombreestacion,ubicacionestacionlat,ubicacionestacionlng) VALUES (2,'POLICIAS ARRASATE',43.072096, -2.473052);

SELECT * FROM f_get_estaciones();
SELECT * FROM f_get_estacion(5);

--------------------------------------------------RECURSOS-------------------------------------------------------

DROP FUNCTION f_nuevo_recurso(integer);
CREATE FUNCTION f_nuevo_recurso(integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
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
$$;

SELECT f_nuevo_recurso(6);

DROP FUNCTION f_get_recurso(integer);
CREATE FUNCTION f_get_recurso(integer) RETURNS TABLE(recursoid integer,estacionid integer,nombrerecurso character varying)
    LANGUAGE plpgsql
    AS $$
	BEGIN
	RETURN QUERY
		SELECT *
		FROM recursos
		WHERE recursos.recursoid = $1;
	END
$$;

SELECT * FROM f_get_recurso(4);
SELECT * FROM f_get_type_resource(4);

DROP FUNCTION f_get_recursos();
CREATE FUNCTION f_get_recursos() RETURNS TABLE(recursoid integer,estacionid integer,nombrerecurso character varying)
    LANGUAGE plpgsql
    AS $$
	BEGIN
	RETURN QUERY
		SELECT *
		FROM recursos;
	END
$$;

DROP FUNCTION f_get_recursos(integer);
CREATE FUNCTION f_get_recursos(integer) RETURNS TABLE(recursoid integer,estacionid integer,nombrerecurso character varying)
    LANGUAGE plpgsql
    AS $$
	BEGIN
	RETURN QUERY
		SELECT recursos.*
		FROM recursos INNER JOIN estaciones ON recursos.estacionid = estaciones.estacionid
		WHERE estaciones.tiporecursoid = $1;
	END
$$;

SELECT * FROM f_get_recursos();

DROP FUNCTION f_actualizar_posicion(integer,integer,numeric,numeric);
CREATE FUNCTION f_actualizar_posicion(integer,integer,numeric,numeric) RETURNS integer
    LANGUAGE plpgsql
    AS $$
	DECLARE
		
	BEGIN
		INSERT INTO hist_ubicacion (recursoid,estado,ubicacionlat,ubicacionlng)
		VALUES ($1,$2,$3,$4);
		RETURN 1;
	EXCEPTION
		WHEN OTHERS THEN
			RETURN -2;
	END
$$;

DROP FUNCTION f_get_location_resource(integer);
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

SELECT * FROM f_get_recursos();
SELECT * FROM f_actualizar_posicion(2,0,44.0,-4.0);
SELECT * FROM f_get_location_resource(2);

DROP FUNCTION f_check_recurso(integer);
CREATE FUNCTION f_check_recurso(integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
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
$$;

SELECT * FROM f_check_recurso(5);

