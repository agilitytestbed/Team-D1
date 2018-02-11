CREATE TABLE Money_Transaction(
	transaction_id serial,
	name text,
	amount bigint,
	PRIMARY KEY(transaction_id)
);

CREATE TABLE IF NOT EXISTS Category(
	category_id serial,
	name text,
	PRIMARY KEY(category_id)
);

CREATE TABLE IF NOT EXISTS Transaction_Category(
	transaction_id bigint,
	category_id bigint,
	FOREIGN KEY(transaction_id) REFERENCES Money_Transaction(transaction_id),
	FOREIGN KEY(category_id) REFERENCES Category(category_id),
	PRIMARY KEY(transaction_id, category_id)
);

CREATE TABLE IF NOT EXISTS User_Transaction(
	session_id text,
	transaction_id bigint,
	FOREIGN KEY(transaction_id) REFERENCES Money_Transaction(transaction_id),
	PRIMARY KEY(session_id, transaction_id)
);

CREATE TABLE IF NOT EXISTS User_Category(
	session_id text,
	category_id bigint,
	FOREIGN KEY(category_id) REFERENCES Category(category_id),
	PRIMARY KEY(session_id, category_id)
);
