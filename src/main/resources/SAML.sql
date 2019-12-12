USE [CxDB]
GO
CREATE TRIGGER UserTypeSAMLTrigger on  [dbo].[UserType]
INSTEAD OF INSERT
AS
BEGIN

INSERT INTO [dbo].[UserType]
	([UserName],
	[UserId],
	[Type])
SELECT  REPLACE([UserName],'SAML#','SAML\'),
	[UserId],
	[Type]
FROM INSERTED
WHERE UserName like 'SAML#%';
END
GO


USE [CxDB]
GO
CREATE TRIGGER UserSAMLTrigger on  [dbo].[Users]
INSTEAD OF INSERT
AS
BEGIN
INSERT INTO [dbo].[Users]
           ([UserName]
           ,[Password]
           ,[DateCreated]
           ,[BusinessUnitID]
           ,[FirstName]
           ,[LastName]
           ,[Email]
           ,[ValidationKey]
           ,[IsAdmin]
           ,[IsActive]
           ,[IsBusinessUnitAdmin]
           ,[JobTitle]
           ,[Phone]
           ,[Company]
           ,[ExpirationDate]
           ,[Country]
           ,[FullPath]
           ,[UPN]
           ,[TeamId]
           ,[is_deprecated]
           ,[CellPhone]
           ,[Skype]
           ,[Language]
           ,[IsAdviseChangePassword]
           ,[SaltForPassword]
           ,[LastLoginDate]
           ,[FailedLogins]
           ,[FailedLoginDate]
           ,[Role])
SELECT  REPLACE([UserName],'SAML#','SAML\')
           ,[Password]
           ,[DateCreated]
           ,[BusinessUnitID]
           ,[FirstName]
           ,[LastName]
           ,[Email]
           ,[ValidationKey]
           ,[IsAdmin]
           ,[IsActive]
           ,[IsBusinessUnitAdmin]
           ,[JobTitle]
           ,[Phone]
           ,[Company]
           ,[ExpirationDate]
           ,[Country]
           ,[FullPath]
           ,[UPN]
           ,[TeamId]
           ,[is_deprecated]
           ,[CellPhone]
           ,[Skype]
           ,[Language]
           ,[IsAdviseChangePassword]
           ,[SaltForPassword]
           ,[LastLoginDate]
           ,[FailedLogins]
           ,[FailedLoginDate]
           ,[Role]
FROM INSERTED
WHERE UserName like 'SAML#%';
END
GO