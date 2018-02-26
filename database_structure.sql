CREATE TABLE IF NOT EXISTS Money_Transaction(
  transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
  name text,
  amount bigint
);

CREATE TABLE IF NOT EXISTS Category(
  category_id INTEGER PRIMARY KEY AUTOINCREMENT,
  name text
);

CREATE TABLE IF NOT EXISTS Transaction_Category(
  transaction_id int,
  category_id int,
  FOREIGN KEY(transaction_id) REFERENCES Money_Transaction(transaction_id) ON DELETE CASCADE,
  FOREIGN KEY(category_id) REFERENCES Category(category_id) ON DELETE CASCADE,
  PRIMARY KEY(transaction_id, category_id)
);

CREATE TABLE IF NOT EXISTS User_Transaction(
  session_id text,
  transaction_id int,
  FOREIGN KEY(transaction_id) REFERENCES Money_Transaction(transaction_id) ON DELETE CASCADE,
  PRIMARY KEY(session_id, transaction_id)
);

CREATE TABLE IF NOT EXISTS User_Category(
  session_id text,
  category_id int,
  FOREIGN KEY(category_id) REFERENCES Category(category_id) ON DELETE CASCADE,
  PRIMARY KEY(session_id, category_id)
);
