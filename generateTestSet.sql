CREATE OR REPLACE FUNCTION f_random_sample()
	RETURNS VOID AS $$
DECLARE
	i int;
	j int;
	t text;
BEGIN
	i := 0;
	WHILE i < 1000 LOOP
		j := (random()*1377716)::int;
		IF (SELECT signature FROM codes WHERE id = j) IS NOT NULL THEN
			t := 'd:\examples\' || j || '.txt';
			
			EXECUTE 'COPY (SELECT code FROM CODES WHERE id = ' || j || ') TO ''' || t || ''' WITH CSV QUOTE '' ''';
			i := i + 1;
		END IF;
	END LOOP;
	RAISE NOTICE '%', i;
END
$$  LANGUAGE plpgsql VOLATILE;