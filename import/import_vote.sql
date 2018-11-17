CREATE OR REPLACE FUNCTION import_vote(varchar)
	RETURNS VOID AS $$
DECLARE
	x xml := $1;
BEGIN
	INSERT INTO Votes
	VALUES(
		(xpath('@Id', x))[1]::TEXT::INTEGER,
		(xpath('@PostId', x))[1]::TEXT::INTEGER,
		(xpath('@VoteTypeId', x))[1]::TEXT::INTEGER,
		(xpath('@UserId', x))[1]::TEXT::INTEGER,
		(xpath('@CreationDate', x))[1]::TEXT::TIMESTAMP
		);
END $$  LANGUAGE plpgsql VOLATILE;