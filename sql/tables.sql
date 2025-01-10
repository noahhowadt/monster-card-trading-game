CREATE TABLE IF NOT EXISTS users
(
    id       UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    coins    integer      NOT NULL,
    name     VARCHAR(255) NULL,
    bio      text         NULL,
    image    VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS packages
(
    id UUID PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS cards
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    damage     FLOAT        NOT NULL CHECK (damage >= 0), -- Damage must be non-negative
    package_id UUID         REFERENCES packages (id) ON DELETE SET NULL,
    user_id    UUID         REFERENCES users (id) ON DELETE SET NULL,
    CHECK ((package_id IS NOT NULL AND user_id IS NULL) OR (package_id IS NULL AND user_id IS NOT NULL))
);

CREATE TABLE IF NOT EXISTS deck
(
    id      UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    card_id UUID NOT NULL REFERENCES cards (id) ON DELETE CASCADE,
    UNIQUE (user_id, card_id) -- Prevent duplicate cards in a user's deck
);

CREATE TYPE card_type AS ENUM ('monster', 'spell');
CREATE TABLE trades
(
    id             UUID PRIMARY KEY,
    card_to_trade  UUID      NOT NULL UNIQUE REFERENCES cards (id),
    type           card_type NOT NULL DEFAULT 'monster',
    minimum_damage FLOAT     NOT NULL
);

CREATE TABLE IF NOT EXISTS stats
(
    user_id UUID PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    wins    INTEGER DEFAULT 0 NOT NULL,
    losses  INTEGER DEFAULT 0 NOT NULL
);



