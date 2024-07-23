CREATE TABLE transfer (
  transfer_id UUID NOT NULL PRIMARY KEY,
  client_source_id UUID NOT NULL,
  source_account_id UUID NOT NULL,
  client_target_id UUID NOT NULL,
  target_account_id UUID NOT NULL,
  "value" DECIMAL(19, 2) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP
);
