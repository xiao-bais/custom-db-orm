SELECT TABLE_NAME AS `table`,
       TABLE_COMMENT AS `desc`
FROM `information_schema`.`TABLES`
WHERE TABLE_NAME IN (%s) AND TABLE_SCHEMA = '%s'