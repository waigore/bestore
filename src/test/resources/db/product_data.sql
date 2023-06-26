INSERT INTO PRODUCT (ID, PRODUCT_TYPE_ID, STATUS, CODE, DEFAULT_DISPLAY_NAME)
    VALUES (1, 1, 'ACTIVE', 'SAMSUNG_TV_01_BLACK', 'Samsung Smart TV Black');
INSERT INTO PRODUCT (ID, PRODUCT_TYPE_ID, STATUS, CODE, DEFAULT_DISPLAY_NAME)
    VALUES (2, 1, 'ACTIVE', 'LG_TV_01_BLACK', 'LG Smart TV Black');
INSERT INTO PRODUCT (ID, PRODUCT_TYPE_ID, STATUS, CODE, DEFAULT_DISPLAY_NAME)
    VALUES (3, 3, 'ACTIVE', 'IPHONE_15_WHITE', 'iPhone 15 White');

INSERT INTO PRODUCT_PRICE (ID, PRODUCT_ID, CURRENCY, PRICE, START_DATE_TIME, END_DATE_TIME)
    VALUES (1, 1, 'HKD', 10000, '2023-05-01 00:00:00', '2023-08-31 23:59:59');
INSERT INTO PRODUCT_PRICE (ID, PRODUCT_ID, CURRENCY, PRICE, START_DATE_TIME, END_DATE_TIME)
    VALUES (2, 2, 'HKD', 12000, '2023-05-01 00:00:00', '2023-08-31 23:59:59');
INSERT INTO PRODUCT_PRICE (ID, PRODUCT_ID, CURRENCY, PRICE, START_DATE_TIME, END_DATE_TIME)
    VALUES (3, 3, 'HKD', 9000, '2023-05-01 00:00:00', '2023-08-31 23:59:59');