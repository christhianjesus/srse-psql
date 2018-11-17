CREATE OR REPLACE FUNCTION import_user(varchar)
	RETURNS VOID AS $$
DECLARE
	x xml := $1;
BEGIN
	--raise NOTICE '%', (xpath('@Id', $1))[1]::TEXT::INTEGER;
	INSERT INTO Users
	VALUES(
		(xpath('@Id', x))[1]::TEXT::INTEGER,
		(xpath('@Reputation', x))[1]::TEXT::INTEGER,
		(xpath('@CreationDate', x))[1]::TEXT::TIMESTAMP,
		(xpath('@DisplayName', x))[1]::TEXT,
		(xpath('@LastAccessDate', x))[1]::TEXT::TIMESTAMP,
		(xpath('@WebsiteUrl', x))[1]::TEXT,
		(xpath('@Location', x))[1]::TEXT,
		(xpath('@AboutMe', x))[1]::TEXT,
		(xpath('@Views', x))[1]::TEXT::INTEGER,
		(xpath('@UpVotes', x))[1]::TEXT::INTEGER,
		(xpath('@DownVotes', x))[1]::TEXT::INTEGER,
		(xpath('@Age', x))[1]::TEXT::INTEGER,
		(xpath('@AccountId', x))[1]::TEXT::INTEGER
		);
END $$  LANGUAGE plpgsql VOLATILE;