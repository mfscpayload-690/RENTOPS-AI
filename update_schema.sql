USE rentops_ai;
ALTER TABLE users ADD COLUMN organization VARCHAR(100) AFTER role;