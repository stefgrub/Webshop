ALTER TABLE orders ADD COLUMN IF NOT EXISTS status varchar(50);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS created_at timestamptz;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS full_name varchar(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS street varchar(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS postal_code varchar(50);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS city varchar(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS country varchar(100);