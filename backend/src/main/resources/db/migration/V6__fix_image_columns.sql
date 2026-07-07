-- Fix missing image columns and sync with entities
ALTER TABLE services ADD COLUMN IF NOT EXISTS image_url VARCHAR(500);

-- Ensure staff_profiles uses BIGINT if users migrated to BIGINT in V4
-- However, V1 created it as VARCHAR(36). If V4 migrated users, staff_profiles might be broken.
-- For this task, we focus on image columns.
ALTER TABLE staff_profiles MODIFY COLUMN avatar VARCHAR(500);
