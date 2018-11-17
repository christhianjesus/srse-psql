CREATE OR REPLACE FUNCTION import_post(varchar) 
	RETURNS VOID AS $$
DECLARE
	x xml := $1;
	PostId INTEGER := (xpath('@Id', x))[1]::TEXT::INTEGER;
	tag TEXT[];
BEGIN
	INSERT INTO Posts (Id,PostTypeId,AcceptedAnswerId,CreationDate,Score,ViewCount,
						Body,OwnerUserId,OwnerDisplayName,LastEditorUserId,LastEditDate,
						LastActivityDate,Title,AnswerCount,CommentCount,FavoriteCount,
						ClosedDate,ParentId,CommunityOwnedDate)
	SELECT
		PostId,
		(xpath('@PostTypeId', x))[1]::TEXT::INTEGER,
		(xpath('@AcceptedAnswerId', x))[1]::TEXT::INTEGER,
		(xpath('@CreationDate', x))[1]::TEXT::TIMESTAMP,
		(xpath('@Score', x))[1]::TEXT::INTEGER,
		(xpath('@ViewCount', x))[1]::TEXT::INTEGER,
		(xpath('@Body', x))[1]::TEXT,
		(xpath('@OwnerUserId', x))[1]::TEXT::INTEGER,
		(xpath('@OwnerDisplayName', x))[1]::TEXT,
		(xpath('@LastEditorUserId', x))[1]::TEXT::INTEGER,
		(xpath('@LastEditDate', x))[1]::TEXT::TIMESTAMP,
		(xpath('@LastActivityDate', x))[1]::TEXT::TIMESTAMP,
		(xpath('@Title', x))[1]::TEXT,
		(xpath('@AnswerCount', x))[1]::TEXT::INTEGER,
		(xpath('@CommentCount', x))[1]::TEXT::INTEGER,
		(xpath('@FavoriteCount', x))[1]::TEXT::INTEGER,
		(xpath('@ClosedDate', x))[1]::TEXT::TIMESTAMP,
		(xpath('@ParentId', x))[1]::TEXT::INTEGER,
		(xpath('@CommunityOwnedDate', x))[1]::TEXT::TIMESTAMP
	WHERE NOT EXISTS (SELECT 1 FROM Posts WHERE Id = PostId);
	
	FOR tag IN SELECT regexp_matches((xpath('@Tags', x))[1]::TEXT, '&lt;([^&]+)&gt', 'g')
	LOOP
		INSERT INTO PostTags (tag, post)
		SELECT Id, PostId
		FROM Tags
		WHERE TagName = tag[1];
	END LOOP;
END $$ LANGUAGE plpgsql VOLATILE;