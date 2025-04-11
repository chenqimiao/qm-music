INSERT INTO user (username, password, email,force_password_change,is_admin, nick_name)
VALUES ('${user.default.username}',
        '${user.default.password}',
        '${user.default.email}',
                true,
                1, '001');