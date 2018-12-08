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
        reminderdate timestamp not null,
        constraint enum_remindertype check (remindertype in ('email', 'telephone','clock')),
        constraint pk_appt primary key (appid),
        constraint fk_users_appt foreign key (userid) references users (userid)
);
