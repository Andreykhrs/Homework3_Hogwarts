CREATE TABLE car (
	id bigint NOT NULL PRIMARY KEY,
	brand character varying(255) NOT NULL,
	model character varying(255) NOT NULL,
	cost MONEY
	)
CREATE TABLE person (
	id bigint NOT NULL PRIMARY KEY,
	name character varying(255) UNIQUE NOT NULL,
	age integer NOT NULL CHECK (age > 0),
	license BOOLEAN,
	car_id bigint REFERENCES car (id)
	)