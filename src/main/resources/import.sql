CREATE TYPE transfer_status AS ENUM ('PROCESSING', 'SUCCESS', 'FAILURE');

CREATE TABLE transfer (
  transfer_id UUID NOT NULL PRIMARY KEY,
  transfer_id UUID NOT NULL,
  source_account_id UUID NOT NULL,
  target_account_id UUID NOT NULL,
  "value" DECIMAL(19, 2) NOT NULL,
  status transfer_status NOT NULL,
  created_at TIMESTAMP NOT NULL,
  balance_updated_at TIMESTAMP
  bacen_updated_at TIMESTAMP
);
