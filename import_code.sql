CREATE OR REPLACE FUNCTION extract_code(text) RETURNS SETOF TEXT AS $$
DECLARE
	code text;
BEGIN
	FOR code IN SELECT (regexp_matches($1, '<[pc][^&]+?>[^&]*?<[pc][^&]+?>(.+?)</[pc][^&]+?>[^&]*?</[pc][^&]+?>', 'gi'))[1]
	LOOP
		RETURN NEXT code;
	END LOOP;
	RETURN;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

CREATE OR REPLACE FUNCTION import_code() 
	RETURNS VOID AS $$
DECLARE
	PostCode RECORD;
BEGIN
	DROP TABLE IF EXISTS Codes;
	CREATE TABLE Codes (id SERIAL CONSTRAINT PK_Codes PRIMARY KEY, code TEXT NOT NULL, signature int[]);
	
	DROP TABLE IF EXISTS PostCodes;
	CREATE TABLE PostCodes (post INTEGER, code INTEGER);
	
	FOR PostCode IN SELECT id, extract_code(body) code FROM Posts NATURAL JOIN questions
	LOOP
		INSERT INTO Codes (code)
		SELECT PostCode.code;
		INSERT INTO PostCodes (post, code)
		SELECT PostCode.id, lastval();
	END LOOP;
	
	FOR PostCode IN SELECT id, extract_code(body) code FROM Posts NATURAL JOIN answers
	LOOP
		INSERT INTO Codes (code)
		SELECT PostCode.code;
		INSERT INTO PostCodes (post, code)
		SELECT PostCode.id, lastval();
	END LOOP;

END $$ LANGUAGE plpgsql VOLATILE;
