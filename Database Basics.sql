DROP TABLE Members;
DROP TABLE Committee;

CREATE TABLE Committee (
    committeeName varchar2(50) PRIMARY KEY,
    chair varchar2(50),
    committeeAdvisor varchar2(50),
    emailAlias varchar(30)
);

CREATE TABLE Members (
    memberID RAW(16) DEFAULT SYS_GUID() PRIMARY KEY,
    firstName varchar2(20),
    lastName varchar2(40),
    email varchar2(100),
    guardianEmail varchar2(100),    
    membershipLevel varchar2(20),
    committeeMembership varchar2(50),
    
    CONSTRAINT committeeMembership
        FOREIGN KEY (committeeMembership) REFERENCES Committee(committeeName)
);

INSERT INTO Committee VALUES ('N/A', '', '', 'misc');
INSERT INTO Committee VALUES ('Ceremonies', 'Ru Paul', '', 'ceremony');
INSERT INTO Committee VALUES ('Stage Crew', '', '', 'skeletons');
INSERT INTO Committee VALUES ('Nemat', '', '', 'nemat');
INSERT INTO Committee VALUES ('Elangomat', '', '', 'elangomat');
INSERT INTO Committee VALUES ('Vigil Committee', '', '', 'vigil');
INSERT INTO Committee VALUES ('Communications', '', 'Jerry Atric', 'communications');
INSERT INTO Committee VALUES ('Program', '', '', 'program');
INSERT INTO Committee VALUES ('Membership', '', '', 'membership');
INSERT INTO Committee VALUES ('Unit Relations', '', '', 'unit-rel');
INSERT INTO Committee VALUES ('Finance', '', '', 'finance');
INSERT INTO Committee VALUES ('Alumni Relations', '', '', 'alumni');
INSERT INTO Committee VALUES ('LLD', '', '', 'lld');

SELECT * 
FROM Committee;


INSERT INTO Members VALUES (DEFAULT, 'John', 'Doe', 'john@gmail.com', '', 'Brotherhood', (SELECT committeeName 
                                                    FROM Committee
                                                    WHERE emailAlias='ceremony'));
                                                    
INSERT INTO Members VALUES (DEFAULT, 'Emily', 'Fitzpatrick', '', 'catlady12345@yahoo.com', 'Ordeal', (SELECT committeeName 
                                                    FROM Committee
                                                    WHERE emailAlias='misc'));

INSERT INTO Members VALUES (DEFAULT, 'Rick', 'Johnson', 'rjohnson@school.org', 'catlady12345@yahoo.com', 'Brotherhood', (SELECT committeeName 
                                                    FROM Committee
                                                    WHERE emailAlias='finance'));
                                                    
INSERT INTO Members VALUES (DEFAULT, 'Henry', 'Roe', 'henry.roe@outlook.com', '', 'Vigil', (SELECT committeeName 
                                                    FROM Committee
                                                    WHERE emailAlias='membership'));
                                                    
INSERT INTO Members VALUES (DEFAULT, 'John', 'Silver', '', 'silverisbest@aol.com', 'Ordeal Candidate', (SELECT committeeName 
                                                    FROM Committee
                                                    WHERE emailAlias='misc'));                                                    

SELECT *
FROM Members;