CREATE OR REPLACE FUNCTION create_lsh() 
	RETURNS VOID AS $$
DECLARE
	rec RECORD;
BEGIN
	DROP TABLE IF EXISTS LSH1;
	CREATE TABLE LSH1 (Band INTEGER, Minisign INTEGER[], Code INTEGER);
	
	FOR rec IN SELECT signature, id FROM Codes WHERE signature IS NOT NULL
	LOOP
		FOR i IN 0..19 LOOP
			INSERT INTO LSH1 VALUES (i, rec.signature[i*5+1:i*5+5], rec.id);
		END LOOP;
	END LOOP;
	
	--CREATE INDEX idx_lsh1 ON LSH1 USING GIN (band, minisign gin__int_ops) WITH (fastupdate = OFF);
	-- BETTER PERFORMANCE
	CREATE INDEX idx_lsh1 ON LSH1 USING GIN (minisign gin__int_ops) WITH (fastupdate = OFF);
END $$ LANGUAGE plpgsql VOLATILE;