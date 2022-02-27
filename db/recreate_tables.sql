/* DO NOT CHANGE ORDER OF DROP STATEMENTS */
DROP TABLE IF EXISTS Historie;
DROP TABLE IF EXISTS Follower;
DROP TABLE IF EXISTS Comment;
DROP TABLE IF EXISTS Image;
DROP TABLE IF EXISTS HootMentions;
DROP TABLE IF EXISTS Reaction;
DROP TABLE IF EXISTS Post;
DROP TABLE IF EXISTS HootTags;
DROP TABLE IF EXISTS Tag;
DROP TABLE IF EXISTS Hoot;
DROP TABLE IF EXISTS HootType;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Interaction;

CREATE TABLE Historie (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    timestamp DateTime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    currentLoggedIn INT UNSIGNED,
    postsPerSecond FLOAT UNSIGNED,
    requestsPerSecond FLOAT UNSIGNED,
    loginsPerSecond FLOAT UNSIGNED,
    currentlyRegisteredUsers INT UNSIGNED,
    trendingHashtags VARCHAR(191) /*   191 * 4bit < 767 bit   */
)
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE User (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(191) NOT NULL,
    imagePath VARCHAR(191),
    passwordHash VARCHAR(191) NOT NULL,
    lastLogin DateTime DEFAULT CURRENT_TIMESTAMP,
    created DateTime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified DateTime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT userName_unique UNIQUE (username)
)
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE Follower (
    user INT UNSIGNED NOT NULL,
    follows INT UNSIGNED NOT NULL CHECK ( follows <> user ),
    created DateTime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `Follower_fk_user_userid`
        FOREIGN KEY (user) REFERENCES User (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `Follower_fk_follows_userid`
        FOREIGN KEY (follows) REFERENCES User (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `Follower_user_fk_Follower_follows_fk` PRIMARY KEY (user, follows)
);

CREATE TABLE Interaction (
    interaction VARCHAR(191) PRIMARY KEY
)
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE HootType (
    hootType VARCHAR(191) PRIMARY KEY
)
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE Hoot (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user INT UNSIGNED NOT NULL,
    hootType VARCHAR(191) NOT NULL,
    created DateTime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified DateTime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `Hoot_fk_user_userid`
        FOREIGN KEY (user) REFERENCES User (id)
        ON DELETE NO ACTION
        ON UPDATE CASCADE,
    CONSTRAINT `Hoot_fk_hootType_hootTypeid`
        FOREIGN KEY (hootType) REFERENCES HootType (hootType)
        ON DELETE NO ACTION
        ON UPDATE CASCADE
)
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE Reaction (
    user INT UNSIGNED NOT NULL,
    hoot INT UNSIGNED NOT NULL,
    interaction VARCHAR(191) NOT NULL,
    created DateTime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified DateTime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `Reaction_fk_user_userid`
        FOREIGN KEY (user) REFERENCES User (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `Reaction_fk_hoot_hootid`
        FOREIGN KEY (hoot) REFERENCES Hoot (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `Reaction_fk_interaction_interactionid`
        FOREIGN KEY (interaction) REFERENCES Interaction (interaction)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `Reaction_user_fk_Reaction_hoot_fk` PRIMARY KEY (user, hoot)
)
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE Comment (
    hoot INT UNSIGNED PRIMARY KEY,
    parent INT UNSIGNED NOT NULL,
    content VARCHAR(191),
    CONSTRAINT `Comment_fk_hoot_hootid`
        FOREIGN KEY (hoot) REFERENCES Hoot (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `Comment_fk_parent_hootid`
        FOREIGN KEY (parent) REFERENCES Hoot (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
)
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE Post (
    hoot INT UNSIGNED PRIMARY KEY,
    content VARCHAR(191),
    onlyFollower BOOLEAN NOT NULL,
    CONSTRAINT `Post_fk_hoot_hootid`
        FOREIGN KEY (hoot) REFERENCES Hoot (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
)
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE Image (
    hoot INT UNSIGNED PRIMARY KEY,
    imagePath VARCHAR(191) NOT NULL,
    content VARCHAR(191),
    onlyFollower BOOLEAN NOT NULL,
    CONSTRAINT `Image_fk_hoot_hootid`
        FOREIGN KEY (hoot) REFERENCES Hoot (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
)
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE HootMentions (
    hoot INT UNSIGNED NOT NULL,
    mention INT UNSIGNED NOT NULL,
    CONSTRAINT `HootMentions_fk_hoot_hootid`
        FOREIGN KEY (hoot) REFERENCES Hoot (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `HootMentions_fk_user_userid`
        FOREIGN KEY (mention) REFERENCES User (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `HootMentions_hoot_fk_HootMentions_mention_fk` PRIMARY KEY (hoot, mention)
)
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE Tag (
    tag VARCHAR(191) PRIMARY KEY
)
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE HootTags (
    hoot INT UNSIGNED NOT NULL,
    tag VARCHAR(191) NOT NULL,
    CONSTRAINT `HootTags_fk_hoot_hootid`
        FOREIGN KEY (hoot) REFERENCES Hoot (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `HootTags_fk_tag_tagid`
        FOREIGN KEY (tag) REFERENCES Tag (tag)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `HootTags_hoot_fk_HootTags_tag_fk` PRIMARY KEY (hoot, tag)
)
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

/* Inital Data */

SET NAMES utf8mb4;

INSERT INTO Historie (currentLoggedIn, postsPerSecond, requestsPerSecond, loginsPerSecond, currentlyRegisteredUsers, trendingHashtags)
VALUES (50, 1.3, 42.1, 24.9, 2, 'Hier, folgt, ein, Emoji, 🐈');

INSERT INTO Historie (trendingHashtags)
VALUES ('🐈');

INSERT INTO User (username, passwordHash)
VALUES ('BeispielUser1','1234'), ('BeispielUser2','5678');

INSERT INTO Follower (user, follows)
VALUES (1, 2);

INSERT INTO Interaction (interaction)
VALUES ('LIKE'), ('DISLIKE');

INSERT INTO HootType (hootType)
VALUES ('Post'), ('Comment'), ('Image');

INSERT INTO Hoot (user, hootType)
VALUES (2, 'Post');

INSERT INTO Post (hoot, content, onlyFollower)
VALUES (1, 'Dies ist mein erster Hoot - bitte seid nett zu mir :)', FALSE);

INSERT INTO Reaction (user, hoot, interaction)
VALUES (2, 1, 'DISLIKE');

INSERT INTO HootMentions (hoot, mention)
VALUES (1, 2);

INSERT INTO Tag (tag)
VALUES ('sommerwind');

INSERT INTO HootTags (hoot, tag)
VALUES (1, 'sommerwind');
