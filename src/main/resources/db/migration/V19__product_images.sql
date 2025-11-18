CREATE TABLE product_images (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT      NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    image_url   VARCHAR(500) NOT NULL,
    sort_index  INT         NOT NULL DEFAULT 0,
    caption     VARCHAR(255)
);

CREATE INDEX idx_product_images_product_id
    ON product_images(product_id);