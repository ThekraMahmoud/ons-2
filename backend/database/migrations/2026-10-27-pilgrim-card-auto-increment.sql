-- Preserve existing IDs and card relationships; only enable generated IDs.
ALTER TABLE pilgrim_card MODIFY COLUMN id int NOT NULL AUTO_INCREMENT;
