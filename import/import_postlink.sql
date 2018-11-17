CREATE OR REPLACE FUNCTION import_postlink(varchar)
	RETURNS VOID AS $$
DECLARE
	res TEXT[] := (xpath('@*', $1::xml))::TEXT[];
BEGIN
	INSERT INTO PostLinks (Id, CreationDate, PostId, RelatedPostId, LinkTypeId)
	VALUES (res[1]::INTEGER, res[2]::TIMESTAMP, res[3]::INTEGER, res[4]::INTEGER, res[5]::INTEGER);
END $$  LANGUAGE plpgsql VOLATILE;