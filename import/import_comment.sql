CREATE OR REPLACE FUNCTION import_comment(varchar)
	RETURNS VOID AS $$
DECLARE
	x xml := $1;
	comment_id INTEGER := (xpath('@Id', x))[1]::TEXT::INTEGER;
BEGIN
	INSERT INTO Comments (Id, PostId, Score, Text, CreationDate, UserId)
	SELECT
		comment_id,
		(xpath('@PostId', x))[1]::TEXT::INTEGER,
		(xpath('@Score', x))[1]::TEXT::INTEGER,
		(xpath('@Text', x))[1]::TEXT,
		(xpath('@CreationDate', x))[1]::TEXT::TIMESTAMP,
		(xpath('@UserId', x))[1]::TEXT::INTEGER
	WHERE NOT EXISTS (SELECT 1 FROM Comments WHERE Id = comment_id);
END $$  LANGUAGE plpgsql VOLATILE;