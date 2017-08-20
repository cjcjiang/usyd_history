drop table A3_ISSUE;
drop table A3_USER;

CREATE TABLE A3_USER
(FIRSTNAME VARCHAR2(100) not null, 
LASTNAME VARCHAR2(100) not null, 
ID NUMBER GENERATED  AS IDENTITY primary key);

Insert into A3_USER (FIRSTNAME,LASTNAME) values ('Dean','Smith');

Insert into A3_USER (FIRSTNAME,LASTNAME) values ('Yuming','JIANG');

Insert into A3_USER (FIRSTNAME,LASTNAME) values ('Xiaomin','CHANG');

CREATE TABLE A3_ISSUE 
(ID NUMBER GENERATED ALWAYS AS IDENTITY primary key, 
DESCRIPTION VARCHAR2(1000), 
PROJECTID NUMBER, 
TITLE VARCHAR2(100),  
CREATOR NUMBER not null REFERENCES A3_USER, 
RESOLVER NUMBER REFERENCES A3_USER, 
VERIFIER NUMBER REFERENCES A3_USER);

Insert into A3_ISSUE (TITLE,DESCRIPTION,CREATOR,RESOLVER,VERIFIER) values ('Division by zero','Division by 0 doesn''t yield error or infinity as would be expected. Instead it results in -1.',1,1,1);

Insert into A3_ISSUE (TITLE,DESCRIPTION,CREATOR,RESOLVER,VERIFIER) values ('Factorial with addition anomaly','Performing a factorial and then addition produces an off by 1 error',1,1,1);

Insert into A3_ISSUE (TITLE,DESCRIPTION,CREATOR,RESOLVER,VERIFIER) values ('Incorrect’ BODMAS order','Addition occurring before multiplication',1,1,1);

Insert into A3_ISSUE (TITLE,DESCRIPTION,CREATOR,RESOLVER,VERIFIER) values ('Yming JIANG ISSUE','created by Yuming, solved by Yuming, verified by Yuming',2,2,2);

Insert into A3_ISSUE (TITLE,DESCRIPTION,CREATOR,RESOLVER,VERIFIER) values ('Xiaomin CHANG ISSUE','created by Xiaomin, solved by Xiaomin, verified by Xiaomin',3,3,3);

alter table a3_issue add (Version INTEGER default 0);

commit;

/*
  Don't run these two procedures at the same time with the above code.
  Please run these two procedures without other code like create table, insert and so on. 
  Please run these two produces one by one.
  Without doing this, there will be an exception.
*/

CREATE OR REPLACE PROCEDURE ADDPROCEDURE
(
  TITLE IN VARCHAR2 
, DESCRIPTION IN VARCHAR2 
, CREATOR IN NUMBER 
, RESOLVER IN NUMBER 
, VERIFIER IN NUMBER 
) AS 
BEGIN
  Insert into A3_ISSUE (TITLE,DESCRIPTION,CREATOR,RESOLVER,VERIFIER) values (TITLE,DESCRIPTION,CREATOR,RESOLVER,VERIFIER);
END ADDPROCEDURE;
/*
  store procedure for updating issues
  including optimistic offline locking
*/
CREATE OR REPLACE PROCEDURE UPDATEPROCEDURE 
(
  TITLE1 IN VARCHAR2 
, DESCRIPTION1 IN VARCHAR2 
, CREATOR1 IN NUMBER 
, RESOLVER1 IN NUMBER 
, VERIFIER1 IN NUMBER 
, ID1 IN NUMBER 
) AS 
currentversion integer;-- version number for the lock
BEGIN
  SELECT version INTO currentversion FROM A3_ISSUE WHERE ID=id1;
  update A3_ISSUE set TITLE=title1,DESCRIPTION=description1,CREATOR=creator1,RESOLVER=resolver1,VERIFIER=verifier1,version=version+1 where ID=id1 and version=currentversion;
END UPDATEPROCEDURE;

