-- 高并发优化索引脚本（MySQL 8）
-- 执行说明：可重复执行，不会因索引已存在而报错。

SET @schema_name = DATABASE();

-- user_browse_record: 用户行为查询与推荐召回核心表
SET @idx := 'idx_ubr_user_update';
SET @sql := (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM information_schema.statistics
            WHERE table_schema = @schema_name
              AND table_name = 'user_browse_record'
              AND index_name = @idx
        ),
        'SELECT ''index idx_ubr_user_update exists''',
        IF(
            EXISTS(
                SELECT 1 FROM information_schema.columns
                WHERE table_schema = @schema_name
                  AND table_name = 'user_browse_record'
                  AND column_name = 'browse_time'
            ),
            'CREATE INDEX idx_ubr_user_update ON user_browse_record(user_id, browse_time DESC)',
            IF(
                EXISTS(
                    SELECT 1 FROM information_schema.columns
                    WHERE table_schema = @schema_name
                      AND table_name = 'user_browse_record'
                      AND column_name = 'create_time'
                ),
                'CREATE INDEX idx_ubr_user_update ON user_browse_record(user_id, create_time DESC)',
                'SELECT ''skip idx_ubr_user_update: no usable time column'''
            )
        )
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx := 'idx_ubr_user_event_update';
SET @sql := (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM information_schema.statistics
            WHERE table_schema = @schema_name
              AND table_name = 'user_browse_record'
              AND index_name = @idx
        ),
        'SELECT ''index idx_ubr_user_event_update exists''',
        IF(
            EXISTS(
                SELECT 1 FROM information_schema.columns
                WHERE table_schema = @schema_name
                  AND table_name = 'user_browse_record'
                  AND column_name = 'attraction_id'
            ),
            IF(
                EXISTS(
                    SELECT 1 FROM information_schema.columns
                    WHERE table_schema = @schema_name
                      AND table_name = 'user_browse_record'
                      AND column_name = 'browse_time'
                ),
                'CREATE INDEX idx_ubr_user_event_update ON user_browse_record(user_id, attraction_id, browse_time DESC)',
                IF(
                    EXISTS(
                        SELECT 1 FROM information_schema.columns
                        WHERE table_schema = @schema_name
                          AND table_name = 'user_browse_record'
                          AND column_name = 'create_time'
                    ),
                    'CREATE INDEX idx_ubr_user_event_update ON user_browse_record(user_id, attraction_id, create_time DESC)',
                    'CREATE INDEX idx_ubr_user_event_update ON user_browse_record(user_id, attraction_id)'
                )
            ),
            'SELECT ''skip idx_ubr_user_event_update: attraction_id missing'''
        )
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- user_collection: 收藏状态判断与列表查询
SET @idx := 'idx_uc_user_create';
SET @sql := (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM information_schema.statistics
            WHERE table_schema = @schema_name
              AND table_name = 'user_collection'
              AND index_name = @idx
        ),
        'SELECT ''index idx_uc_user_create exists''',
        IF(
            EXISTS(
                SELECT 1 FROM information_schema.columns
                WHERE table_schema = @schema_name
                  AND table_name = 'user_collection'
                  AND column_name = 'collection_time'
            ),
            'CREATE INDEX idx_uc_user_create ON user_collection(user_id, is_deleted, collection_time DESC)',
            IF(
                EXISTS(
                    SELECT 1 FROM information_schema.columns
                    WHERE table_schema = @schema_name
                      AND table_name = 'user_collection'
                      AND column_name = 'update_time'
                ),
                'CREATE INDEX idx_uc_user_create ON user_collection(user_id, is_deleted, update_time DESC)',
                'SELECT ''skip idx_uc_user_create: no usable time column'''
            )
        )
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- attraction: 城市 + 类型 + 审核状态 + 热度 排序/筛选
SET @idx := 'idx_attr_city_type_audit_browse';
SET @sql := (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM information_schema.statistics
            WHERE table_schema = @schema_name
              AND table_name = 'attraction'
              AND index_name = @idx
        ),
        'SELECT ''index idx_attr_city_type_audit_browse exists''',
        'CREATE INDEX idx_attr_city_type_audit_browse ON attraction(city_id, type_id, audit_status, browse_count DESC)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx := 'idx_attr_status_audit_type';
SET @sql := (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM information_schema.statistics
            WHERE table_schema = @schema_name
              AND table_name = 'attraction'
              AND index_name = @idx
        ),
        'SELECT ''index idx_attr_status_audit_type exists''',
        'CREATE INDEX idx_attr_status_audit_type ON attraction(status, audit_status, type_id)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- user_preference: 推荐画像读取
SET @idx := 'idx_up_user_update';
SET @sql := (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM information_schema.statistics
            WHERE table_schema = @schema_name
              AND table_name = 'user_preference'
              AND index_name = @idx
        ),
        'SELECT ''index idx_up_user_update exists''',
        IF(
            EXISTS(
                SELECT 1 FROM information_schema.columns
                WHERE table_schema = @schema_name
                  AND table_name = 'user_preference'
                  AND column_name = 'update_time'
            ),
            'CREATE INDEX idx_up_user_update ON user_preference(user_id, update_time DESC)',
            IF(
                EXISTS(
                    SELECT 1 FROM information_schema.columns
                    WHERE table_schema = @schema_name
                      AND table_name = 'user_preference'
                      AND column_name = 'create_time'
                ),
                'CREATE INDEX idx_up_user_update ON user_preference(user_id, create_time DESC)',
                'SELECT ''skip idx_up_user_update: no usable time column'''
            )
        )
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
