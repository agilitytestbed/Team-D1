CREATE TABLE IF NOT EXISTS User_Table(
  user_id INTEGER PRIMARY KEY AUTOINCREMENT,
  session_id TEXT,
  highest_transaction_id BIGINT,
  highest_category_id BIGINT,
  highest_category_rule_id BIGINT,
  highest_saving_goal_id BIGINT,
  highest_payment_request_id BIGINT,
  highest_user_message_id BIGINT
);

CREATE TABLE IF NOT EXISTS Transaction_Table(
  user_id INTEGER,
  transaction_id BIGINT,
  date DATETIME,
  amount FLOAT,
  description TEXT,
  external_iban TEXT,
  type TEXT,
  FOREIGN KEY(user_id) REFERENCES User_Table(user_id),
  PRIMARY KEY(user_id, transaction_id)
);

CREATE TABLE IF NOT EXISTS Category_Table(
  user_id INTEGER,
  category_id BIGINT,
  name TEXT,
  FOREIGN KEY(user_id) REFERENCES User_Table(user_id),
  PRIMARY KEY(user_id, category_id)
);

CREATE TABLE IF NOT EXISTS Transaction_Category(
  user_id INTEGER,
  transaction_id BIGINT,
  category_id BIGINT,
  FOREIGN KEY(user_id) REFERENCES User_Table(user_id),
  FOREIGN KEY(transaction_id) REFERENCES Transaction_Table(transaction_id),
  FOREIGN KEY(category_id) REFERENCES Category_Table(category_id),
  PRIMARY KEY(user_id, transaction_id, category_id)
);

CREATE TABLE IF NOT EXISTS Category_Rule(
  user_id INTEGER,
  category_rule_id BIGINT,
  description TEXT,
  external_iban TEXT,
  type TEXT,
  category_id BIGINT,
  apply_on_history BOOLEAN,
  FOREIGN KEY(user_id) REFERENCES User_Table(user_id),
  FOREIGN KEY(category_id) REFERENCES Category_Table(category_id),
  PRIMARY KEY(user_id, category_rule_id)
);

CREATE TABLE IF NOT EXISTS Saving_Goal(
  user_id INTEGER,
  saving_goal_id BIGINT,
  creation_date DATETIME,
  deletion_date DATETIME,
  name TEXT,
  goal FLOAT,
  save_per_month FLOAT,
  min_balance_required FLOAT,
  FOREIGN KEY(user_id) REFERENCES User_Table(user_id),
  PRIMARY KEY(user_id, saving_goal_id)
);

CREATE TABLE IF NOT EXISTS Payment_Request(
  user_id INTEGER,
  payment_request_id BIGINT,
  description TEXT,
  due_date DATETIME,
  amount FLOAT,
  number_of_requests BIGINT,
  filled BOOLEAN,
  FOREIGN KEY(user_id) REFERENCES User_Table(user_id),
  PRIMARY KEY(user_id, payment_request_id)
);

CREATE TABLE IF NOT EXISTS Payment_Request_Transaction(
  user_id INTEGER,
  payment_request_id BIGINT,
  transaction_id BIGINT,
  FOREIGN KEY(user_id) REFERENCES User_Table(user_id),
  FOREIGN KEY(payment_request_id) REFERENCES Payment_Request(payment_request_id),
  FOREIGN KEY(transaction_id) REFERENCES Transaction_Table(transaction_id),
  PRIMARY KEY(user_id, payment_request_id, transaction_id)
);

CREATE TABLE IF NOT EXISTS User_Message(
  user_id INTEGER,
  user_message_id BIGINT,
  message TEXT,
  date DATETIME,
  read BOOLEAN,
  type TEXT,
  FOREIGN KEY(user_id) REFERENCES User_Table(user_id),
  PRIMARY KEY(user_id, user_message_id)
);

