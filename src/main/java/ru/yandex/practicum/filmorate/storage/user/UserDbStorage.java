package ru.yandex.practicum.filmorate.storage.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.BadDataException;
import ru.yandex.practicum.filmorate.exceptions.UnknownDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("userDbStorage")
@Component
@Slf4j
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Получение всех пользователей
    @Override
    public Collection<User> findAllUsers() {
        log.info("findAllUsers");
        String sql = "SELECT user_id, user_name, user_email, user_birthdate, user_login FROM users";
        return jdbcTemplate.query(sql, new RowMapper<User>() {
            @Override
            public User mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setName(rs.getString("user_name"));
                user.setEmail(rs.getString("user_email"));
                user.setBirthday(rs.getDate("user_birthdate").toLocalDate());
                user.setLogin(rs.getString("user_login"));
                return user;
            }
        });
    }

    //Создание пользователя
    @Override
    public User createUser(@RequestBody User user) {
        log.info("createUser");
        validateUser(user);
        user.setId(getNextUserId());
        String sql = "INSERT INTO users (user_id, user_name, user_email, user_birthdate, user_login) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getLogin());
        log.info("Новый пользователь создан с ID: " + user.getId());
        return user;
    }

    //Обновление пользователя
    @Override
    public User updateUser(@RequestBody User user) {
        log.info("updateUser");
        validateUser(user);
        if (!userExists(user.getId())) {
            throw new UnknownDataException("Пользователь отсутствует");
        }
        String sql = "UPDATE users SET user_name = ?, user_email = ?, user_birthdate = ?, user_login = ? WHERE user_id = ?";

        jdbcTemplate.update(sql,
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getLogin(),
                user.getId());

        log.info("Пользователь с ID " + user.getId() + " обновлён");
        return user;
    }

    //Метод для получения нового уникального ID для пользователя
    private Integer getNextUserId() {
        log.info("getNextUserId");
        String sql = "SELECT COALESCE(MAX(user_id), 0) FROM users";
        Long currentMaxId = jdbcTemplate.queryForObject(sql, Long.class);
        return Math.toIntExact(currentMaxId + 1);
    }

    //Метод проверки создания пользователя
    public void validateUser(User user) {
        log.info("validateUser");
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new BadDataException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BadDataException("Адрес электронной почты не может быть пустым");
        }
        if (!user.getEmail().contains("@")) {
            throw new BadDataException("Неверный адрес электронной почты");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new BadDataException("Дата рождения в будущем");
        }
    }

    //Метод проверки существования id пользователя в базе данных. Если есть - вернёт true
    private boolean userExists(int userId) {
        log.info("userExist");
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count > 0;
    }

    public boolean checkFriendsAvalaibility(Integer id, Integer friendId) {
        log.info("checkFriendAvailability");
        String sql = "SELECT COUNT(*) FROM users WHERE user_id IN (?, ?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id, friendId);
        return count != null && count == 2;
    }

    public User addFriend(Integer id, Integer friendId) {
        log.info("addFriend");
        if (!checkFriendsAvalaibility(id, friendId)) {
            throw new UnknownDataException("Запрошенные ресурсы отсутствуют. Невозможно добавить в друзья.");
        }
        String checkExistenceSql = "SELECT COUNT(*) FROM user_friendlist WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkExistenceSql, Integer.class, id, friendId);
        if (count != null && count > 0) {
            throw new UnknownDataException("Дружба уже существует.");
        }

        String sqlInsert1 = "INSERT INTO user_friendlist (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlInsert1, id, friendId);
        return getUserWithFriends(id);
    }

    private User getUserWithFriends(Integer userId) {
        log.info("getUserwithFriends");
        String sqlUser = "SELECT * FROM users WHERE user_id = ?";
        User user = jdbcTemplate.queryForObject(sqlUser, this::mapRowToUser, userId);
        String sqlFriends = "SELECT friend_id FROM user_friendlist WHERE user_id = ?";
        Set<Integer> friendIds = new HashSet<>(jdbcTemplate.queryForList(sqlFriends, Integer.class, userId));
        Set<User> friends = new HashSet<>();
        for (Integer friendId : friendIds) {
            User friend = getUserById(friendId);
            friends.add(friend);
        }
        user.setFriends(friends);
        return user;
    }

    public User getUserById(Integer id) {
        log.info("getUserById");
        String sql = "SELECT * FROM users WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        log.info("mapRowTOUser");
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("user_email"));
        user.setLogin(rs.getString("user_login"));
        user.setName(rs.getString("user_name"));
        user.setBirthday(rs.getDate("user_birthdate").toLocalDate());
        return user;
    }

    //Удаление из друзей. Боже, надо было делать сразу с рассчётом на это. Столько лишних переписываний
    public User removeFriend(Integer id, Integer friendId) {
        log.info("removeFriend"+friendId+"у пользователя"+id);
        if (!checkFriendsAvalaibility(id, friendId)) {
            throw new UnknownDataException("Запрошенные ресурсы отсутствуют. Невозможно удалить из друзей.");
        }
        String sqlDelete = "DELETE FROM user_friendlist WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlDelete, id, friendId);
        return getUserWithFriends(id);
    }

    //Общие друзья
    public Collection<User> getCommonFriends(Integer id, Integer friendId) {
        log.info("getCommonFriends");
        if (!checkFriendsAvalaibility(id, friendId)) {
            throw new UnknownDataException("Запрошенные ресурсы отсутствуют. Невозможно сформировать список общих друзей.");
        }
        String sqlFriendsOfFirstUser = "SELECT friend_id FROM user_friendlist WHERE user_id = ?";
        Set<Integer> friendsOfFirstUser = new HashSet<>(jdbcTemplate.queryForList(sqlFriendsOfFirstUser, Integer.class, id));
        String sqlFriendsOfSecondUser = "SELECT friend_id FROM user_friendlist WHERE user_id = ?";
        Set<Integer> friendsOfSecondUser = new HashSet<>(jdbcTemplate.queryForList(sqlFriendsOfSecondUser, Integer.class, friendId));
        friendsOfFirstUser.retainAll(friendsOfSecondUser);
        if (friendsOfFirstUser.isEmpty()) {
            return new ArrayList<>();
        }
        String sqlCommonFriends = String.format(
                "SELECT * FROM users WHERE user_id IN (%s)",
                friendsOfFirstUser.stream().map(String::valueOf).collect(Collectors.joining(", "))
        );
        return jdbcTemplate.query(sqlCommonFriends, this::mapRowToUser);
    }

    public Collection<User> getFriends(Integer id) {
        log.info("getFriends");
        String sqlCheckUser = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        int userCount = jdbcTemplate.queryForObject(sqlCheckUser, Integer.class, id);
        if (userCount == 0) {
            throw new UnknownDataException("Запрошенные ресурсы отсутствуют. Невозможно сформировать список друзей");
        }
        String sqlFriendsIds = "SELECT friend_id FROM user_friendlist WHERE user_id = ?";
        List<Integer> friendsIds = jdbcTemplate.queryForList(sqlFriendsIds, Integer.class, id);
        if (friendsIds.isEmpty()) {
            return new ArrayList<>();
        }
        String sqlFriendsData = String.format(
                "SELECT * FROM users WHERE user_id IN (%s)",
                friendsIds.stream().map(String::valueOf).collect(Collectors.joining(", "))
        );
        List<User> friends = jdbcTemplate.query(sqlFriendsData, this::mapRowToUser);
        for (User friend : friends) {
            Set<User> friendSet = new HashSet<>(getUserWithFriends(friend.getId()).getFriends());
            friend.setFriends(friendSet);
        }
        return friends;
    }

}