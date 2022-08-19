package com.newproject.service;

import com.newproject.entity.User;
import com.newproject.mapper.UserMapper;
import com.newproject.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
@Service
public class UpdateDB {

    public UpdateDB() {
    }
    public String addUser(String username, String email, String DOB, String firstname,
                        String password, String lastname) {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        byte[] salt = generateSalt();
        try {
            Date DateOfBirth = new SimpleDateFormat("dd/MM/yyyy").parse(DOB);
            int res = userMapper.addUser(new User(
                    username,
                    email,
                    DateOfBirth,
                    firstname,
                    lastname,
                    Base64.getEncoder().encodeToString(salt),
                    generateHash(password, salt), "nojwt"));
            sqlSession.commit();
            sqlSession.close();
            if (res > 0) {
                return "Success!";
            } else {
                return "failed to create a new user";
            }
        } catch (Exception e) {
            return e.toString();
        }
        //提交事务
    }

    public User getUserById(String username){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.getUserByUsername(username);
        sqlSession.close();
        return user;
    }

    public String updateEmail(String username, String email) {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = getUserById(username);
        user.setEmail(email);
        int res = userMapper.updateUserEmail(user);
        sqlSession.commit();
        sqlSession.close();
        if (res > 0) {
            return "Success!";
        } else{
            return "Failed to update!";
        }
    }

    public int updateJwt(String username, String jwt){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = getUserById(username);
        user.setJwt(jwt);
        int res = userMapper.updateUserName(user);
        sqlSession.commit();
        sqlSession.close();
        return res;
    }

    public String updateUserName(String username, String firstname, String lastname) {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = getUserById(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        int res = userMapper.updateUserName(user);
        sqlSession.commit();
        sqlSession.close();
        if (res > 0) {
            return "Success!";
        } else{
            return "Failed to update!";
        }
    }

    public String updateDOB(String username, String DOB) {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = getUserById(username);
        try {
            Date DateOfBirth = new SimpleDateFormat("dd/MM/yyyy").parse(DOB);
            user.setDOB(DateOfBirth);
            int res = userMapper.updateDOB(user);
            sqlSession.commit();
            sqlSession.close();
            if (res > 0) {
                return "Success!";
            } else{
                return "Failed to update!";
            }
        } catch (ParseException e) {
            return "Invalid Input";
        }
    }

    public String updatePassword(String username, String password) {
        try {
            SqlSession sqlSession = MybatisUtils.getSqlSession();
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = getUserById(username);
            byte[] salt = generateSalt();
            user.sethash(generateHash(password, salt));
            user.setSalt(Base64.getEncoder().encodeToString(salt));
            int res = userMapper.updateUserPassword(user);
            sqlSession.commit();
            sqlSession.close();
            if (res > 0) {
                return "Success!";
            } else {
                return "Failed to update!";
            }
        } catch (Exception e) {
            return e.toString();
        }
    }


    public String deleteUser(String username) {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        int res = userMapper.deleteUser(username);
        sqlSession.commit();
        // Note if we do not commit, the database will never get updated.
        sqlSession.close();
        if (res > 0) {
            return "Success!";
        } else {
            return "User does not exist";
        }
    }

    public String getHash(String username) {
        User user = getUserById(username);
        return user.gethash();
    }

    public String getsalt(String username) {
        User user = getUserById(username);
        return user.getSalt();
    }

    private byte[] generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public static String generateHash(String password, byte[] salt) {

        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }




}
