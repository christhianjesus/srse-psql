CREATE OR REPLACE FUNCTION import_tag(varchar)
	RETURNS VOID AS $$
DECLARE
	res TEXT[] := (xpath('@*', $1::xml))::TEXT[];
BEGIN
	INSERT INTO Tags (Id, TagName)
	VALUES (res[1]::INTEGER, res[2]);
END $$  LANGUAGE plpgsql VOLATILE;