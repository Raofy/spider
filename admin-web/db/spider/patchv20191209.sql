ALTER TABLE ip_info ADD COLUMN type VARCHAR ( 50 ) DEFAULT null COMMENT '类型 http/ https/ socket';
ALTER TABLE ip_info ADD COLUMN extra VARCHAR ( 500 ) DEFAULT null ;