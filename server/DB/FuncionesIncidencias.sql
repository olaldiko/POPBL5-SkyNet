
INSERT INTO USUARIOS (usuarioid,USERNAME) values (0,'INVITADO');

INSERT INTO tipo_incidencia values(1,'Ambulancia');
INSERT INTO tipo_incidencia values(2,'Policia');
INSERT INTO tipo_incidencia values(3,'Bomberos');

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

SELECT f_nueva_incidencia(1,8.2,1.8,0,'946010514','Hola ke ase',4,9);
SELECT * FROM hist_incidencias;

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

