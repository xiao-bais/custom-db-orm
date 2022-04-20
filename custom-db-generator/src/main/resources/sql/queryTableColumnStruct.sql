SELECT
    TABLE_NAME AS `table`,
    COLUMN_NAME AS `column`,
    DATA_TYPE AS columnType,
    CASE WHEN COLUMN_KEY = 'PRI' THEN TRUE ELSE FALSE END primaryKey,
    CASE WHEN COLUMN_KEY != '' THEN EXTRA ELSE '' END keyExtra,
    COLUMN_COMMENT AS `desc`
FROM `information_schema`.`COlUMNS` WHERE TABLE_NAME in (%s) AND TABLE_SCHEMA = '%s'