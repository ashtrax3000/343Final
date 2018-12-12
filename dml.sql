create table users (
        userid int not null generated always as identity (start with 1, increment by 1),
        firstname varchar(30) not null,
        lastname  varchar(30) not null,
        username  varchar(20) not null,
        password  varchar(20) not null,
        email varchar(50) not null,
        phonenumber varchar(15) not null,
        constraint pk_users primary key (userid)
);


create table appointments (
        appid int not null not null generated always as identity (start with 1, increment by 1),
        userid int not null,
        title varchar(70) not null,
        description long varchar not null,
        location varchar(70) not null,
        begintime timestamp not null,
        endtime timestamp not null,
        remindertype varchar(30) not null,
        remindertime int not null,
        constraint enum_remindertype check (remindertype in ('email', 'telephone','clock')),
        constraint pk_appt primary key (appid),
        constraint fk_users_appt foreign key (userid) references users (userid)
);

                                            
INSERT INTO APP.APPOINTMENTS (USERID, TITLE, DESCRIPTION, LOCATION, BEGINTIME, ENDTIME, REMINDERTYPE, REMINDERTIME) 
	VALUES (1, 'Project Due', 'Project 2', 'ENG 322', '2018-12-12 10:06:02.285', '2018-12-12 10:10:44.575', 'email', 60),
	(1, 'Homework D23', 'Project 2', 'LOL 302', '2018-12-12 10:06:02.285', '2018-12-12 10:10:44.575', 'telephone', 60),
	(1, 'Project Due', 'Project 2', 'VEC 111', '2018-12-12 10:06:02.285', '2018-12-12 10:10:44.575', 'telephone', 60),
	(1, 'Test', 'Project 2', 'CEC 233', '2018-12-12 10:06:02.285', '2018-12-12 10:10:44.575', 'email', 60),
	(1, 'Review chapter 1', 'Project 2', 'CEC 233', '2018-12-12 10:06:02.285', '2018-12-12 10:10:44.575', 'email', 30),
	(1, 'Meeting @ library', 'Study group with classmates', 'Schools Library', '2018-12-15 10:06:02.285', '2018-12-15 10:10:44.575', 'email', 60),
	(1, 'Doctors Appt', 'Go to schools clinic for results', 'Clinic', '2018-12-13 10:06:02.285', '2018-12-13 10:10:44.575', 'telephone', 25),
	(1, 'Library checkout', 'Project 2', 'CEC 588', '2018-12-14 10:06:02.285', '2018-12-14 11:10:44.575', 'email', 60),
	(1, 'Homework Due', 'Homework 6', 'Online class', '2018-12-13 10:06:02.285', '2018-12-13 10:10:44.575', 'telephone', 60),		
	(1, 'Interview', 'Job interview at student center', 'Student Center', '2018-12-12 10:06:02.285', '2018-12-12 10:10:44.575', 'email', 15)


INSERT INTO APP.USERS (FIRSTNAME, LASTNAME, USERNAME, PASSWORD, EMAIL, PHONENUMBER) 
	VALUES 
        ('Admin', 'Admin', '1', '1', 'admin@gmail.com', '666-999-0000'),                             
	('Andrew', 'Mattsow', 'andrew01', 'qwe123', 'andre.334@gmail.com', '346-566-2344'),
	('Marc', 'Morrison', 'marc01', 'qwe123', 'marc01@gmail.com', '777-444-2344'),
	('Jackson', 'Collison', 'jackson01', 'qwe123', 'jackson01@gmail.com', '222-556-8653'),
	('Loreine', 'Hernandez', 'loreine01', 'qwe123', 'loreine01@gmail.com', '989-111-2344')





