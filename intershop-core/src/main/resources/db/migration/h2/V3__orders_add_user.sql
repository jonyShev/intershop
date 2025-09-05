ALTER TABLE orders
  ADD COLUMN user_id UUID NOT NULL;

CREATE INDEX IF NOT EXISTS ix_orders_user_id ON orders(user_id);
