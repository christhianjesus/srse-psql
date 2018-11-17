CREATE OR REPLACE FUNCTION import_badge(varchar)
	RETURNS VOID AS $$
DECLARE
	res TEXT[] := (xpath('@*', $1::xml))::TEXT[];
BEGIN
	INSERT INTO Badges (Id, UserId, Name, Date)
	SELECT res[1]::INTEGER, res[2]::INTEGER, res[3], res[4]::TIMESTAMP
	WHERE NOT EXISTS (SELECT 1 FROM Badges WHERE Id = res[1]::INTEGER);
END $$  LANGUAGE plpgsql VOLATILE;