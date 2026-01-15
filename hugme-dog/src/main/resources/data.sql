USE dogCancer;
-- 📘 1. Board (게시판 - ID 자동 증가 가정)
INSERT INTO board (type, created_at, modified_at, activated)
VALUES
    ('INFO_SHARE', NOW(), NOW(), true),
    ('QNA', NOW(), NOW(), true),
    ('REVIEW', NOW(), NOW(), true),
    ('EMERGENCY_NOTE', NOW(), NOW(), true),
    ('TREATMENT_DIARY', NOW(), NOW(), true),
    ('HAPPY_MOMENT', NOW(), NOW(), true),
    ('SUPPORT', NOW(), NOW(), true),
    ('FREE_TALK', NOW(), NOW(), true);

-- 👤 2. Member (ID를 BINARY(16)으로 변환)
INSERT INTO member (id, user_id, email, password, phone, name, created_at, birthday, modified_at, activated, profile_url)
VALUES
    (UUID_TO_BIN('11111111-1111-1111-1111-111111111111'), 'ajkd', 'doglover@example.com', 'pw1234', '010-4677-6023', '홍길동', NOW(), '2001-11-11', NOW(), true,'https://res.cloudinary.com/dyz2lq1f0/image/upload/v1764656860/%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C_modrhq.jpg'),
    (UUID_TO_BIN('22222222-2222-2222-2222-222222222222'), 'user1', 'vetinfo@example.com', 'pw5678', '010-1234-5678', '홍길동', NOW(), '2001-11-11', NOW(), true, 'https://res.cloudinary.com/dyz2lq1f0/image/upload/v1764656851/%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C_2_koqas5.jpg'),
    (UUID_TO_BIN('33333333-3333-3333-3333-333333333333'), 'user2', 'supporter@example.com', 'pw9999', '010-1234-7890', '홍길동', NOW(), '2001-11-11', NOW(), true, 'https://res.cloudinary.com/dyz2lq1f0/image/upload/v1764656843/%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C_1_fi2riw.jpg');

-- 📝 3. Post (게시글 - user_id 참조 부분 변환)
INSERT INTO post (board_id, user_id, title, content, created_at, modified_at, activated)
VALUES
    (1, UUID_TO_BIN('22222222-2222-2222-2222-222222222222'), '영양제 추천 리스트', '우리 강아지에게 도움이 된 영양제를 공유합니다.', NOW(), NOW(), true),
    (2, UUID_TO_BIN('11111111-1111-1111-1111-111111111111'), '강아지가 밥을 안 먹어요', '식욕이 떨어졌을 때 대처 방법이 궁금합니다.', NOW(), NOW(), true),
    (3, UUID_TO_BIN('11111111-1111-1111-1111-111111111111'), '00동물병원 후기', '의사 선생님이 친절하고 꼼꼼하게 진료해주셨어요.', NOW(), NOW(), true),
    (4, UUID_TO_BIN('11111111-1111-1111-1111-111111111111'), '응급 시 체온 유지 방법', '갑자기 쓰러졌을 때 이렇게 대처했어요.', NOW(), NOW(), true),
    (5, UUID_TO_BIN('22222222-2222-2222-2222-222222222222'), '치료 3주차 일지', '오늘은 조금 기운이 돌아온 것 같아요.', NOW(), NOW(), true),
    (6, UUID_TO_BIN('11111111-1111-1111-1111-111111111111'), '행복한 산책', '가을 바람을 맞으며 같이 산책했어요 🍂', NOW(), NOW(), true),
    (7, UUID_TO_BIN('33333333-3333-3333-3333-333333333333'), '오늘은 유난히 힘드네요', '다른 분들도 이런 날 있으신가요?', NOW(), NOW(), true),
    (8, UUID_TO_BIN('33333333-3333-3333-3333-333333333333'), '강아지용 옷 추천 좀요', '겨울 대비 따뜻한 옷 찾고 있어요!', NOW(), NOW(), true);

-- 🖼️ 4. PostImage (게시글 이미지 - post_id는 자동 증가값 가정)
-- (post_id가 UUID라면 여기도 UUID_TO_BIN을 써야 하지만, 보통 게시글 ID는 Long(숫자)을 쓰므로 그대로 둡니다.)
INSERT INTO post_image (post_id, origin_file_name, rename_file_name, save_path)
VALUES
    (3, 'hospital.jpg', 'hospital_1.jpg', 'https://res.cloudinary.com/dyz2lq1f0/image/upload/v1763707069/post_uploads/m1ryt6ptdy6wyydp4yog.png'),
    (6, 'walk.jpg', 'walk_1.jpg', 'https://res.cloudinary.com/dyz2lq1f0/image/upload/v1763706392/post_uploads/ajqvyjwdjvyy6be9ooav.png'),
    (8, 'dogclothes.jpg', 'dogclothes_1.jpg', 'https://res.cloudinary.com/dyz2lq1f0/image/upload/v1764656636/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7_2025-05-06_150107_mscbyh.png');

-- 🔖 5. PostHashtag (게시글 해시태그)
INSERT INTO post_hashtag (post_id, hashtag_content)
VALUES
    (1, '#영양제'),
    (1, '#면역력'),
    (3, '#병원후기'),
    (5, '#치료일지'),
    (7, '#위로'),
    (8, '#강아지옷');

-- 💬 6. Comments (댓글 - user_id 참조 부분 변환)
INSERT INTO comments (user_id, post_id, content, created_at, modified_at, activated)
VALUES
    (UUID_TO_BIN('22222222-2222-2222-2222-222222222222'), 2, '식욕 촉진제 사용해보셨어요?', NOW(), NOW(), true),
    (UUID_TO_BIN('33333333-3333-3333-3333-333333333333'), 2, '저도 비슷한 경험 있었어요.', NOW(), NOW(), true),
    (UUID_TO_BIN('11111111-1111-1111-1111-111111111111'), 3, '좋은 병원이네요! 공유 감사해요.', NOW(), NOW(), true),
    (UUID_TO_BIN('22222222-2222-2222-2222-222222222222'), 7, '힘내세요. 다들 같은 마음이에요.', NOW(), NOW(), true);

-- ❤️ 7. Like (좋아요 - user_id 참조 부분 변환)
INSERT INTO favorite (magazine_id, post_id, user_id, created_at, modified_at, activated)
VALUES
    (NULL, 3, UUID_TO_BIN('22222222-2222-2222-2222-222222222222'), NOW(), NOW(), TRUE),
    (NULL, 5, UUID_TO_BIN('11111111-1111-1111-1111-111111111111'), NOW(), NOW(), TRUE),
    (NULL, 6, UUID_TO_BIN('22222222-2222-2222-2222-222222222222'), NOW(), NOW(), TRUE),
    (NULL, 7, UUID_TO_BIN('33333333-3333-3333-3333-333333333333'), NOW(), NOW(), TRUE);

-- 1. gender 컬럼을 숫자가 아닌 문자로 변경
ALTER TABLE dog MODIFY gender VARCHAR(20);

-- 2. dog_size 컬럼도 문자로 변경
ALTER TABLE dog MODIFY dog_size VARCHAR(20);


INSERT INTO dog (user_id, dog_name, gender, is_neutered, breed, birth, disease, dog_size, created_at, modified_at, imageurl, weight, is_rainbow)
VALUES
    -- 1. 첫 번째 회원 ('ajkd')의 강아지 : 뽀삐 (소형견, 말티푸)
    (
        UUID_TO_BIN('11111111-1111-1111-1111-111111111111'),
        '뽀삐',
        'MALE',
        true,
        '말티푸',
        '2020-05-05 00:00:00',
        false,
        'SMALL',
        NOW(),
        NOW(),
        'https://res.cloudinary.com/dyz2lq1f0/image/upload/v1764656860/%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C_modrhq.jpg',
        4.12,
        true
    ),

    -- 2. 두 번째 회원 ('user1')의 강아지 : 초코 (중형견, 푸들)
    (
        UUID_TO_BIN('22222222-2222-2222-2222-222222222222'),
        '초코',
        'FEMALE',
        false,
        '푸들',
        '2021-08-15 00:00:00',
        true,
        'MEDIUM',
        NOW(),
        NOW(),
        'https://res.cloudinary.com/dyz2lq1f0/image/upload/v1764656860/%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C_modrhq.jpg',
        3.12,
        true
    ),

    -- 3. 세 번째 회원 ('user2')의 강아지 : 맥스 (대형견, 리트리버)
    (
        UUID_TO_BIN('33333333-3333-3333-3333-333333333333'),
        '맥스',
        'MALE',
        true,
        '골든 리트리버',
        '2019-01-10 00:00:00',
        false,
        'LARGE',
        NOW(),
        NOW(),
        'https://res.cloudinary.com/dyz2lq1f0/image/upload/v1764656860/%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C_modrhq.jpg',
        5.1,
        false
    );
-- 1. 최상위 카테고리 (Depth 0)
INSERT INTO magazine_category (category_id, category_name, depth, parent_id) VALUES (1, '암 정보 센터', 0, NULL);
INSERT INTO magazine_category (category_id, category_name, depth, parent_id) VALUES (2, '생활 가이드', 0, NULL);
INSERT INTO magazine_category (category_id, category_name, depth, parent_id) VALUES (3, '최신 연구 동향', 0, NULL);

-- 2. 하위 카테고리 (Depth 1) - 암 종류
INSERT INTO magazine_category (category_id, category_name, depth, parent_id) VALUES (4, '위암', 1, 1);
INSERT INTO magazine_category (category_id, category_name, depth, parent_id) VALUES (5, '폐암', 1, 1);
INSERT INTO magazine_category (category_id, category_name, depth, parent_id) VALUES (6, '유방암', 1, 1);
INSERT INTO magazine_category (category_id, category_name, depth, parent_id) VALUES (7, '갑상선암', 1, 1);

-- 3. 하위 카테고리 (Depth 1) - 생활/식단
INSERT INTO magazine_category (category_id, category_name, depth, parent_id) VALUES (8, '항암 식단', 1, 2);
INSERT INTO magazine_category (category_id, category_name, depth, parent_id) VALUES (9, '운동 가이드', 1, 2);

INSERT INTO resource (resource_id, category_id, category_title, cancer_title, section_header, subtitle, detail_title, detail_content, resourceurl)
VALUES (1, 4, '암 정보', '위암', '개요', '위암이란?', '위암의 정의와 원인',
        '위암은 위에 생기는 악성 종양을 말합니다. 주로 위점막 세포에서 발생하며, 헬리코박터 파일로리균 감염이 주요 원인 중 하나입니다. 짠 음식이나 탄 음식을 즐기는 식습관도 위암 발생 위험을 높입니다.',
        'https://cancer.go.kr/stomach');

-- 폐암 (영어 혼용, 복합명사 테스트용)
INSERT INTO resource (resource_id, category_id, category_title, cancer_title, section_header, subtitle, detail_title, detail_content, resourceurl)
VALUES (2, 5, '암 정보', '폐암', '치료', '비소세포폐암 치료법', '항암화학요법과 표적치료',
        'Lung Cancer(폐암)의 치료는 크게 수술, 항암화학요법, 방사선 치료로 나뉩니다. 최근에는 면역항암제(Immunotherapy)가 비소세포폐암 치료에 널리 쓰이고 있습니다.',
        'https://cancer.go.kr/lung');

-- 유방암 (형태소 분석 테스트: "자가검진" vs "자가 검진")
INSERT INTO resource (resource_id, category_id, category_title, cancer_title, section_header, subtitle, detail_title, detail_content, resourceurl)
VALUES (3, 6, '암 정보', '유방암', '예방', '조기 발견', '자가검진 방법',
        '유방암은 조기 발견이 생존율을 크게 높입니다. 매달 생리가 끝난 후 3~5일 뒤에 유방 자가검진을 실시하는 것이 좋습니다. 멍울이 만져지거나 유두 분비물이 있다면 즉시 병원을 찾아야 합니다.',
        'https://cancer.go.kr/breast');

-- 항암 식단 (자연어 처리 테스트: "먹었습니다" vs "섭취")
INSERT INTO resource (resource_id, category_id, category_title, cancer_title, section_header, subtitle, detail_title, detail_content, resourceurl)
VALUES (4, 8, '생활 가이드', '공통', '식습관', '면역력 강화', '항암에 좋은 음식',
        '항암 치료 중에는 단백질 섭취가 매우 중요합니다. 붉은 고기보다는 닭고기, 생선, 두부 등을 드시는 것이 좋습니다. 또한 브로콜리 같은 십자화과 채소는 항암 효과가 뛰어납니다.',
        'https://health.guide/diet');

-- 1. 위암 매거진 (검색어: "위암 원인", "짠 음식")
INSERT INTO magazine (magazine_id, resource_id, cancer_title, content_origin, content_summary, content_translation, resource_name, resourceurl)
VALUES (1, 1, '위암',
        '위암의 가장 큰 원인은 맵고 짠 음식입니다.',
        'The biggest cause of stomach cancer is spicy and salty food.',
        '위암 예방을 위해서는 저염식이 필수입니다. 헬리코박터균 제균 치료도 도움이 됩니다.',
        '국가암정보센터', 'https://cancer.go.kr/stomach');

-- 2. 폐암 매거진 (검색어: "Lung", "면역치료")
INSERT INTO magazine (magazine_id, resource_id, cancer_title, content_origin, content_summary, content_translation, resource_name, resourceurl)
VALUES (2, 2, '폐암',
        '폐암은 초기 증상이 없어 조기 발견이 어렵습니다.',
        'Lung cancer is difficult to detect early because there are no initial symptoms.',
        '비소세포폐암 환자에게는 표적치료제와 면역항암제가 새로운 희망이 되고 있습니다. 흡연자는 정기적인 CT 검진이 필요합니다.',
        '대한폐암학회', 'https://lung.or.kr');

-- 3. 유방암 매거진 (검색어: "자가진단", "멍울")
-- N-gram은 "자가진단" 검색 시 "자가"와 "진단"이 떨어져 있어도 찾을 수 있지만, 형태소 분석은 정확한 의미 단위를 찾습니다.
INSERT INTO magazine (magazine_id, resource_id, cancer_title, content_origin, content_summary, content_translation, resource_name, resourceurl)
VALUES (3, 3, '유방암',
        '유방암 자가검진은 매월 정기적으로 해야 합니다.',
        'Breast self-examination should be done regularly every month.',
        '샤워할 때나 거울 앞에서 유방의 모양 변화나 멍울을 체크하세요. 조기 발견 시 생존율은 90% 이상입니다.',
        '핑크리본 캠페인', 'https://breast.campaign');

-- 4. 식단 매거진 (검색어: "단백질", "브로콜리")
INSERT INTO magazine (magazine_id, resource_id, cancer_title, content_origin, content_summary, content_translation, resource_name, resourceurl)
VALUES (4, 4, '항암식단',
        '잘 먹는 것이 최고의 항암제입니다.',
        'Eating well is the best anticancer drug.',
        '고단백 식사와 신선한 채소 섭취가 체력을 유지하는 비결입니다. 입맛이 없더라도 조금씩 자주 드세요.',
        '암 환자 영양 가이드', 'https://nutrition.guide');

-- 5. 갑상선암 (검색어: "거북이암", "착한암" - 별명 검색 테스트)
-- 이런 건 본문에 없으면 검색이 안 되므로, 검색 엔진 성능 테스트에 좋습니다.
INSERT INTO magazine (magazine_id, resource_id, cancer_title, content_origin, content_summary, content_translation, resource_name, resourceurl)
VALUES (5, NULL, '갑상선암',
        '갑상선암은 진행이 느려 거북이 암이라고도 불립니다.',
        'Thyroid cancer is also called turtle cancer because it progresses slowly.',
        '착한 암이라고 방심하면 안 됩니다. 수술 후에도 호르몬 관리가 평생 필요할 수 있습니다.',
        '갑상선학회', 'https://thyroid.or.kr');


INSERT INTO magazine_image (image_id, magazine_id, origin_file_name, rename_file_name, save_path, type)
VALUES (1, 1, 'stomach_cancer_01.jpg', 'uuid_stomach_01.jpg', '/images/2024/stomach/', 'THUMBNAIL');

INSERT INTO magazine_image (image_id, magazine_id, origin_file_name, rename_file_name, save_path, type)
VALUES (2, 1, 'stomach_chart.png', 'uuid_stomach_chart.png', '/images/2024/stomach/', 'CONTENT');

INSERT INTO magazine_image (image_id, magazine_id, origin_file_name, rename_file_name, save_path, type)
VALUES (3, 2, 'lung_xray.jpg', 'uuid_lung_01.jpg', '/images/2024/lung/', 'THUMBNAIL');

INSERT INTO magazine_image (image_id, magazine_id, origin_file_name, rename_file_name, save_path, type)
VALUES (4, 3, 'breast_self_check.jpg', 'uuid_breast_01.jpg', '/images/2024/breast/', 'THUMBNAIL');

INSERT INTO magazine_image (image_id, magazine_id, origin_file_name, rename_file_name, save_path, type)
VALUES (5, 4, 'healthy_food.jpg', 'uuid_food_01.jpg', '/images/2024/food/', 'THUMBNAIL');



select * from dog;

-- 1. 기존 테이블에 Full-Text 인덱스 추가 (ngram 파서 적용 필수!)
-- title(제목)과 content(내용)을 합쳐서 검색할 수 있게 만듭니다.

ALTER TABLE magazine
    ADD FULLTEXT INDEX idx_ft_title_content (content_translation, cancer_title) WITH PARSER ngram;

SELECT * FROM magazine
WHERE MATCH(content_translation, cancer_title) AGAINST('항암' IN NATURAL LANGUAGE MODE);